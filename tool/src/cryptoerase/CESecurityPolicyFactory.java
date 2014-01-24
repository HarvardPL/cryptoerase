package cryptoerase;

import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.util.InternalCompilerError;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.AccessPathField;
import cryptoerase.securityPolicy.AccessPathLocal;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.securityPolicy.ErasurePolicy;
import cryptoerase.securityPolicy.LevelPolicy;
import cryptoerase.types.CETypeSystem;

public class CESecurityPolicyFactory<A extends Ordered<A>> extends
        SecurityPolicyFactory<A> {

    private static CESecurityPolicyFactory<?> singleton =
            new CESecurityPolicyFactory();

    public static CESecurityPolicyFactory<?> singleton() {
        return singleton;
    }

    public static SecurityPolicy BOTTOM = new LevelPolicy("BOTTOM");
    public static SecurityPolicy LOW = new LevelPolicy("L");
    public static SecurityPolicy HIGH = new LevelPolicy("H");
    public static SecurityPolicy PUBKEY = new LevelPolicy("PUBKEY");
    public static SecurityPolicy PRIVKEY = new LevelPolicy("PRIVKEY");
    public static SecurityPolicy ERROR = new LevelPolicy("ERROR");

    @Override
    public SecurityPolicy parseSecurityString(String securityString,
            Node source, AnalysisUtil<A> autil) {
        return parseSecurityString(securityString, source);
    }

    public SecurityPolicy parseSecurityString(String securityString, Node source) {
        if ("H".equals(securityString)) return HIGH;
        if ("L".equals(securityString)) return LOW;
        if ("PUBKEY".equals(securityString)) return PUBKEY;
        if ("PRIVKEY".equals(securityString)) return PRIVKEY;
        throw new InternalCompilerError("Illegal security string: "
                + securityString, source.position());
    }

    @Override
    public SecurityPolicy bottom() {
        return BOTTOM;
    }

    public AccessPath exprToAccessPath(Local l) {
        return new AccessPathLocal(l.localInstance(), l.position());
    }

    public AccessPath exprToAccessPath(Expr e, AnalysisUtil autil) {
        if (e instanceof Local) {
            Local l = (Local) e;
            return exprToAccessPath(l);
        }
        else if (e instanceof Field) {
            Field f = (Field) e;
            Receiver target = f.target();
            CETypeSystem ts = (CETypeSystem) autil.typeSystem();
            if (!f.fieldInstance().type().equals(ts.Condition())) {
                throw new InternalCompilerError("Not a condition!");
            }

            return new AccessPathField(autil.abstractLocations(f),
                                       e.toString(),
                                       f.position());

        }
        throw new InternalCompilerError("Expression " + e
                + " not suitable for an access path.", e.position());
    }

    public CESecurityPolicy erasurePolicy(CESecurityPolicy initialPol,
            AccessPath cond, CESecurityPolicy finalPol) {
        return new ErasurePolicy(initialPol, cond, finalPol);
    }

}
