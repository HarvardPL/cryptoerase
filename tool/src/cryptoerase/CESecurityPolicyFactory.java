package cryptoerase;

import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.frontend.goals.Goal;
import polyglot.types.FieldInstance;
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
import cryptoerase.securityPolicy.FlowPolicy;
import cryptoerase.securityPolicy.KeyKind;
import cryptoerase.securityPolicy.KindPolicy;
import cryptoerase.securityPolicy.LevelPolicy;
import cryptoerase.types.CETypeSystem;

public class CESecurityPolicyFactory<A extends Ordered<A>> extends
        SecurityPolicyFactory<A> {

    private static CESecurityPolicyFactory<?> singleton =
            new CESecurityPolicyFactory();

    public static CESecurityPolicyFactory<?> singleton() {
        return singleton;
    }

    public static FlowPolicy BOTTOM = new LevelPolicy("BOTTOM");
    public static FlowPolicy LOW = new LevelPolicy("L");
    public static FlowPolicy HIGH = new LevelPolicy("H");
    public static FlowPolicy ERROR = new LevelPolicy("ERROR");

    @Override
    public SecurityPolicy parseSecurityString(String securityString,
            Node source, AnalysisUtil<A> autil) {
        return parseSecurityString(securityString, source);
    }

    public SecurityPolicy parseSecurityString(String securityString, Node source) {
        if ("H".equals(securityString)) return HIGH;
        if ("L".equals(securityString)) return LOW;
        throw new InternalCompilerError("Illegal security string: "
                + securityString, source.position());
    }

    @Override
    public CESecurityPolicy bottom() {
        return CESecurityPolicy.BOTTOM;
    }

    public AccessPath exprToAccessPath(Local l) {
        return new AccessPathLocal(l.localInstance(), l.position());
    }

    public AccessPath exprToAccessPath(Expr e) {
        if (e instanceof Local) {
            Local l = (Local) e;
            return exprToAccessPath(l);
        }
        else if (e instanceof Field) {
            Field f = (Field) e;
            CETypeSystem ts = (CETypeSystem) f.type().typeSystem();
            FieldInstance fi = f.fieldInstance();
            if (!fi.isCanonical()) {
                Goal g = ts.extensionInfo().scheduler().currentGoal();
                g.setUnreachableThisRun();
            }
            else if (!fi.type().equals(ts.Condition())) {
                throw new InternalCompilerError("Not a condition! ");
            }

            return new AccessPathField(fi, e.toString(), f.position());

        }
        throw new InternalCompilerError("Expression " + e
                + " not suitable for an access path.", e.position());
    }

    public FlowPolicy erasurePolicy(FlowPolicy initialPol, AccessPath cond,
            FlowPolicy finalPol) {
        return new ErasurePolicy(initialPol, cond, finalPol);
    }

    public CESecurityPolicy pubKeyPolicy(FlowPolicy keyBoundPolicy, FlowPolicy p) {
        return CESecurityPolicy.create(new KeyKind(true, keyBoundPolicy), p);
    }

    public CESecurityPolicy privKeyPolicy(FlowPolicy keyBoundPolicy,
            FlowPolicy p) {
        return CESecurityPolicy.create(new KeyKind(false, keyBoundPolicy), p);
    }

    public CESecurityPolicy otherPolicy(FlowPolicy p) {
        return CESecurityPolicy.create(KindPolicy.OTHER, p);
    }

}
