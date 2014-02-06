package cryptoerase.securityPolicy;

import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import accrue.infoflow.analysis.SecurityPolicy;
import cryptoerase.CESecurityPolicyFactory;

/**
 * Erasure security policies. They consist of a KindPolicy (indicating whether it is a PubKey, PrivKey, or Other)
 * and a flow policy (L, H, Lc/H, etc.)
 */
public class CESecurityPolicy implements SecurityPolicy {
    private final KindPolicy kindPol;

    private final FlowPolicy flowPol;

    public static final CESecurityPolicy ERROR =
            new CESecurityPolicy(KindPolicy.OTHER,
                                 CESecurityPolicyFactory.ERROR);

    public static final CESecurityPolicy BOTTOM = new CESecurityPolicy(null,
                                                                       null);

    static public CESecurityPolicy create(KindPolicy kindPol, FlowPolicy flowPol) {
        if (kindPol == null && flowPol == null) {
            return BOTTOM;
        }
        if (kindPol == null || flowPol == null) {
            throw new InternalCompilerError("Bad");
        }
        return new CESecurityPolicy(kindPol, flowPol);

    }

    protected CESecurityPolicy(KindPolicy kindPol, FlowPolicy flowPol) {
        this.kindPol = kindPol;
        this.flowPol = flowPol;
    }

    public KindPolicy kindPol() {
        return kindPol;
    }

    public FlowPolicy flowPol() {
        return flowPol;
    }

    @Override
    public boolean isBottom() {
        return kindPol == null && flowPol == null;
    }

    @Override
    public boolean leq(SecurityPolicy o) {
        if (this.isBottom()) {
            return true;
        }
        if (o.isBottom()) {
            return false;
        }
        if (o instanceof CESecurityPolicy) {
            CESecurityPolicy that = (CESecurityPolicy) o;
            return this.kindPol.leq(that.kindPol)
                    && this.flowPol.leq(that.flowPol);
        }
        else {
            throw new InternalCompilerError("Got compared to " + o + " "
                    + o.getClass());
        }
    }

    @Override
    public SecurityPolicy upperBound(SecurityPolicy o) {
        if (o instanceof CESecurityPolicy) {
            if (this.isBottom()) return o;
            if (o.isBottom()) return this;

            CESecurityPolicy that = (CESecurityPolicy) o;

            if (this.kindPol.equals(that.kindPol)) {
                return create(this.kindPol,
                              this.flowPol.upperBound(that.flowPol));
            }
            return ERROR;
        }
        else {
            throw new InternalCompilerError("Got compared to " + o + " "
                    + o.getClass());
        }

    }

    @Override
    public SecurityPolicy widen(SecurityPolicy o) {
        return this.upperBound(o);
    }

    @Override
    public void prettyPrint(CodeWriter cw) {
        if (this.kindPol != KindPolicy.OTHER) {
            this.kindPol.prettyPrint(cw);
            cw.write("{");
            this.flowPol.prettyPrint(cw);
            cw.write("}");
        }
        else {
            this.flowPol.prettyPrint(cw);
        }
    }

    public CESecurityPolicy flowPol(FlowPolicy newFlowPol) {
        if (this.flowPol == newFlowPol
                || (this.flowPol != null && this.flowPol.equals(newFlowPol))) {
            return this;
        }
        return create(this.kindPol, newFlowPol);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.kindPol != KindPolicy.OTHER) {
            if (this.kindPol != null) {
                sb.append(this.kindPol.toString());
            }
            else {
                sb.append("<null>");
            }
            sb.append("{");
            if (this.flowPol != null) {
                sb.append(this.flowPol);
            }
            else {
                sb.append("<null>");
            }
            sb.append("}");
        }
        else {
            if (this.flowPol != null) {
                sb.append(this.flowPol);
            }
            else {
                sb.append("<null>");
            }
        }
        return sb.toString();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flowPol == null) ? 0 : flowPol.hashCode());
        result = prime * result + ((kindPol == null) ? 0 : kindPol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CESecurityPolicy other = (CESecurityPolicy) obj;
        if (flowPol == null) {
            if (other.flowPol != null) return false;
        }
        else if (!flowPol.equals(other.flowPol)) return false;
        if (kindPol == null) {
            if (other.kindPol != null) return false;
        }
        else if (!kindPol.equals(other.kindPol)) return false;
        return true;
    }

}
