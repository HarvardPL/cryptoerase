package accrue.cryptoerase.visit;

import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.ast.PolicyErasure;
import accrue.cryptoerase.securityPolicy.AccessPath;
import accrue.cryptoerase.securityPolicy.AccessPathField;
import accrue.cryptoerase.types.CETypeSystem;
import polyglot.ast.ArrayInit;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Instanceof;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Return;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;

public class ConditionChecker extends ErrorHandlingVisitor {

	public ConditionChecker(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	@Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		Type condType = ((CETypeSystem) ts).Condition();
		
		if (n instanceof PolicyErasure) {
			/* This should never happen because conditions that are fields must be static final,
			 * but check just in case...
			 */
			PolicyErasure pe = (PolicyErasure) n;
			AccessPath ap = CESecurityPolicyFactory.singleton().exprToAccessPath(pe.erasureCondition());
			if (ap instanceof AccessPathField) {
				for (FieldInstance fi : ((AccessPathField) ap).fieldInstance()) {
					if (!fi.flags().isFinal()) {
						throw new SemanticException("Conditions should only be accessed through final fields", n.position());
					}
				}
			}
		}
		
		if (n instanceof LocalDecl) {
			LocalDecl ld = (LocalDecl) n;
			if (ts.typeEquals(ld.type().type(), condType)) {
				Expr init = ld.init();
				if (!ld.flags().isFinal()) {
					throw new SemanticException("Conditions must be final");
				}
				if (!(init instanceof New)) {
					throw new SemanticException("Conditions may only be initialized by construction.", n.position());
				}
			} else if (ts.typeEquals(ld.type().type(), condType.arrayOf())) {
				throw new SemanticException("May not store conditions in an array", n.position());
			}
			
			if (ld.init() != null && ts.typeEquals(ld.init().type(), condType)) {
				if (!ts.typeEquals(ld.type().type(), condType)) {
					throw new SemanticException("Conditions may only be stored in locals of type Condition", n.position());
				}
			}
		}
		
		if (n instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) n;
			if (ts.typeEquals(fd.type().type(), condType)) {
				if (!fd.flags().isStatic()) {
					throw new SemanticException("Conditions must be either locals or static fields");
				}
				if (!fd.flags().isFinal()) {
					throw new SemanticException("Conditions must be final");
				}
				Expr init = fd.init();
				if (!(init instanceof New)) {
					throw new SemanticException("Conditions may only be initialized by construction.", n.position());
				}
			} else if (ts.typeEquals(fd.type().type(), condType.arrayOf())) {
				throw new SemanticException("May not store conditions in an array", n.position());
			}
			
			if (fd.init() != null && ts.typeEquals(fd.init().type(), condType)) {
				if (!ts.typeEquals(fd.type().type(), condType)) {
					throw new SemanticException("Conditions may only be stored in fields of type Condition", n.position());
				}
			}
		}
		
		if (n instanceof Assign) {
			Assign a = (Assign) n;
			if (ts.typeEquals(a.type(), condType) || ts.typeEquals(a.right().type(), condType)) {
				throw new SemanticException("Conditions may not be assigned", a.position());
			}
		}
		
		if (n instanceof Formal) {
			Formal f = (Formal) n;
			if (ts.typeEquals(f.type().type(), condType)) {
				throw new SemanticException("May not pass conditions as arguments", f.position());
			}
		}
		
		if (n instanceof Return) {
			Return r = (Return) n;
			if (r.expr() != null && ts.typeEquals(r.expr().type(), condType)) {
				throw new SemanticException("May not return conditions", n.position());
			}
		}
		
		if (n instanceof Cast) {
			Cast c = (Cast) n;
			if (ts.typeEquals(c.expr().type(), condType)) {
				throw new SemanticException("May not cast conditions!", c.position());
			}
		}
		
		if (n instanceof ProcedureCall) {
			ProcedureCall pc = (ProcedureCall) n;
			for (Expr arg : pc.arguments()) {
				if (ts.typeEquals(arg.type(), condType)) {
					throw new SemanticException("May not pass conditions as arguments", arg.position());
				}
			}
		}
		
		if (n instanceof Call) {
			Call c = (Call) n;
			if (c.target() instanceof Expr) {
				Expr target = (Expr) c.target();
				if (ts.typeEquals(target.type(), condType)) {
					if (!c.methodInstance().name().equals("set")) {
						throw new SemanticException("May not call methods other than 'set' on conditions", c.position());
					}
				}
			}
		}
		
		if (n instanceof Binary) {
			Binary b = (Binary) n;
			if (ts.typeEquals(b.left().type(), condType) || ts.typeEquals(b.right().type(), condType)) {
				throw new SemanticException("May not interrogate condition objects", b.position());
			}
		}
		
		if (n instanceof Instanceof) {
			Instanceof io = (Instanceof) n;
			if (ts.typeEquals(io.expr().type(), condType)) {
				throw new SemanticException("May not interrogate condition objects", io.position());
			}
		}
		
		return this;
	}

}
