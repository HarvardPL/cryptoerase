package accrue.cryptoerase.translate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import accrue.cryptoerase.securityPolicy.AccessPathField;
import accrue.cryptoerase.securityPolicy.ErasurePolicy;
import accrue.cryptoerase.types.CEFieldInstance;

public class CERewriter extends ExtensionRewriter {

	public CERewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
	}

	@Override
	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
		CERewriter rw = (CERewriter) v;
		
		if (n instanceof ClassBody) {
			ClassBody cb = (ClassBody) n;
			ClassBody oldcb = (ClassBody) old;
			
			Set<CEFieldInstance> instanceErasureFields = new LinkedHashSet<>();
			Set<CEFieldInstance> staticErasureFields = new LinkedHashSet<>();
			
			for (ClassMember m : oldcb.members()) {
				if (m instanceof FieldDecl) {
					CEFieldInstance cefi = (CEFieldInstance) ((FieldDecl) m).fieldInstance();
					if (cefi.declaredPolicy() != null && cefi.declaredPolicy().flowPol() instanceof ErasurePolicy) {
						if (cefi.flags().isStatic()) {
							staticErasureFields.add(cefi);
						} else {
							instanceErasureFields.add(cefi);
						}
					}
				}
			}
			
			if (!instanceErasureFields.isEmpty()) {
				boolean foundCtor = false;
				List<ClassMember> afterCtors =
	                    new ArrayList<ClassMember>(cb.members().size());
				for (ClassMember m : cb.members()) {
	                if (m instanceof ConstructorDecl) {
	                    ConstructorDecl cd = (ConstructorDecl) m;
	                    foundCtor = true;
	                    m = rewriteConstructorDecl(this.context().currentClass(), cd, instanceErasureFields);
	                }
	                afterCtors.add(m);
	            }
				assert foundCtor;
				cb = cb.members(afterCtors);
			}
			
			if (!staticErasureFields.isEmpty()) {
				for (CEFieldInstance cefi : staticErasureFields) {
					cb = cb.addMember(qq().parseMember("static { %S }", createErasureListener(cefi, false)));
				}
			}
			return cb;
		}
		
		return super.leaveCall(old, n, v);
	}
	
    private ConstructorDecl rewriteConstructorDecl(ClassType ct,
            ConstructorDecl cd, Set<CEFieldInstance> erasureListenersToAdd) throws SemanticException {
        Block block = cd.body();
        ConstructorCall constructorCall = null;
        boolean doListeners = true;
        // any statements other than the constructor call:
        List<Stmt> remainingStatements = new ArrayList<Stmt>();

        if (block.statements().size() > 0) {
            Stmt fst = block.statements().get(0);
            if (fst instanceof ConstructorCall) {
                remainingStatements.addAll(block.statements()
                                                .subList(1,
                                                         block.statements()
                                                              .size()));
                constructorCall = (ConstructorCall) fst;
                if (constructorCall.kind() == ConstructorCall.THIS) {
                    // Not a super call, don't do field initializers
                    doListeners = false;
                }
            }
            else {
                remainingStatements.addAll(block.statements());
            }
        }

        List<Stmt> statements = new ArrayList<Stmt>();
        if (constructorCall != null) {
            // original constructor had a constructor call, add it
            statements.add(constructorCall);
        }
        if (doListeners) {
            for (CEFieldInstance cefi : erasureListenersToAdd) {
                statements.add(createErasureListener(cefi, true));
            }
        }
        // add any remaining statements from the original constructor
        statements.addAll(remainingStatements);

        block = block.statements(statements);
        cd = (ConstructorDecl) cd.body(block);

        return cd;
    }

	private Stmt createErasureListener(CEFieldInstance fi, boolean includeObject) throws SemanticException {
		ErasurePolicy ep = (ErasurePolicy) fi.declaredPolicy().flowPol();
		Expr defaultValue = defaultValue(fi.type());
		Expr field = qq().parseExpr(fi.name());
		Type type = to_ts().typeForName("accrue.cryptoerase.runtime.ErasureListener");
		Expr newExpr = qq().parseExpr("new %T() { public void erase() { %E = %E; } }", type, field, defaultValue);
		FieldInstance cond = ((AccessPathField) ep.condition()).fieldInstance().iterator().next();
		TypeNode container = typeToJava(cond.container(), Position.COMPILER_GENERATED);
		Expr condExpr = qq().parseExpr("%T.%s", container, cond.name());
		if (includeObject) {
			return qq().parseStmt("%E.register(this, %E);", condExpr, newExpr);
		} else {
			return qq().parseStmt("%E.register(%E);", condExpr, newExpr);
		}
	}

	private Expr defaultValue(Type type) {
		Lit lit;
        if (type.isReference()) {
            lit = (Lit) to_nf().NullLit(Position.COMPILER_GENERATED).type(type.typeSystem().Null());
        }
        else if (type.isBoolean()) {
            lit = (Lit) to_nf().BooleanLit(Position.COMPILER_GENERATED, false).type(type);
        }
        else if (type.isInt() || type.isShort() || type.isChar()
                || type.isByte()) {
            lit = (Lit) to_nf().IntLit(Position.COMPILER_GENERATED, IntLit.INT, 0).type(type);
        }
        else if (type.isLong()) {
            lit = (Lit) to_nf().IntLit(Position.COMPILER_GENERATED, IntLit.LONG, 0).type(type);
        }
        else if (type.isFloat()) {
            lit = (Lit) to_nf().FloatLit(Position.COMPILER_GENERATED, FloatLit.FLOAT, 0.0).type(type);
        }
        else if (type.isDouble()) {
            lit = (Lit) to_nf().FloatLit(Position.COMPILER_GENERATED, FloatLit.DOUBLE, 0.0).type(type);
        }
        else throw new InternalCompilerError("Don't know default value for type "
                + type);
        
        return lit;
	}
}
