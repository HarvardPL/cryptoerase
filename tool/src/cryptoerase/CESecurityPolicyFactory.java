package cryptoerase;

import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import cryptoerase.ast.PolicyNode;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.AccessPathClass;
import cryptoerase.securityPolicy.AccessPathField;
import cryptoerase.securityPolicy.AccessPathLocal;
import cryptoerase.securityPolicy.AccessPathThis;
import cryptoerase.securityPolicy.CryptoSecurityPolicy;
import cryptoerase.securityPolicy.ErasurePolicy;
import cryptoerase.securityPolicy.LevelPolicy;

public class CESecurityPolicyFactory<A extends Ordered<A>> extends
        SecurityPolicyFactory<A> {
    public static SecurityPolicy LOW = new LevelPolicy("L");
    public static SecurityPolicy HIGH = new LevelPolicy("H");
    public static SecurityPolicy PUBKEY = new LevelPolicy("PUBKEY");
    public static SecurityPolicy PRIVKEY = new LevelPolicy("PRIVKEY");
    public static SecurityPolicy ERROR = new LevelPolicy("ERROR");

    @Override
    public SecurityPolicy parseSecurityString(String securityString,
            Node source, AnalysisUtil<A> autil) {
        if ("H".equals(securityString)) return HIGH;
        if ("L".equals(securityString)) return LOW;
        if ("PUBKEY".equals(securityString)) return PUBKEY;
        if ("PRIVKEY".equals(securityString)) return PRIVKEY;
        throw new InternalCompilerError("Illegal security string: "
                + securityString, source.position());
    }

    public CryptoSecurityPolicy convertPolicyNode(PolicyNode pol,
            AnalysisUtil<A> autil) {
        return pol.policy(this, autil);
    }

    @Override
    public SecurityPolicy bottom() {
        return LOW;
    }

    public AccessPath exprToAccessPath(Expr e) throws SemanticException {
        if (e instanceof Local) {
            Local l = (Local) e;
            return new AccessPathLocal(l.localInstance(),
                                       l.name(),
                                       e.position());
        }
        else if (e instanceof Field) {
            Field f = (Field) e;
            Receiver target = f.target();
            if (target instanceof Expr) {
                //              ReferenceType container = null;
                //                if (f.isTypeChecked()) {
                //                    container = f.fieldInstance().container();
                //}
                AccessPath prefix = exprToAccessPath((Expr) f.target());
                return new AccessPathField(prefix,
                                           f.fieldInstance(),
                                           f.name(),
                                           f.position());
            }
            else if (target instanceof TypeNode
                    && ((TypeNode) target).type().isClass()) {
                AccessPath prefix =
                        new AccessPathClass(((TypeNode) target).type()
                                                               .toClass(),
                                            target.position());
                return new AccessPathField(prefix,
                                           f.fieldInstance(),
                                           f.name(),
                                           f.position());
            }
            else {
                throw new InternalCompilerError("Not currently supporting access paths for targets of "
                        + target.getClass());
            }
        }
        else if (e instanceof Special) {
            Special s = (Special) e;
            if (Special.THIS.equals(s.kind())) {
//                if (context.currentClass() == null || context.inStaticContext()) {
//                    throw new SemanticException("Cannot use \"this\" in this scope.",
//                                                e.position());
//                }
                return new AccessPathThis(s.type().toClass(), s.position());
            } /*
                else if (Special.SUPER.equals(s.kind())) {
                    if(context.currentClass() == null || context.inStaticContext() || !context.inCode()) {
                        throw new SemanticException("Cannot use \"super\" in this scope.", e.position());
                    } else {
                        // We are not in a constructor now - using super is safe
                        return new AccessPathThis((ClassType) context.currentClass().superType(), s.position());
                    }
                }
              */
            else {
                throw new InternalCompilerError("Not currently supporting access paths for special of kind "
                        + s.kind());
            }
        }
        else if (e instanceof Cast) {
            return exprToAccessPath(((Cast) e).expr());
        }
        throw new SemanticException("Expression " + e
                + " not suitable for an access path.", e.position());
    }

    public CryptoSecurityPolicy erasurePolicy(CryptoSecurityPolicy initialPol,
            AccessPath cond, CryptoSecurityPolicy finalPol) {
        return new ErasurePolicy(initialPol, cond, finalPol);
    }

}
