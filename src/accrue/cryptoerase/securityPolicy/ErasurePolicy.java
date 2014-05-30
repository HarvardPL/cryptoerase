package accrue.cryptoerase.securityPolicy;

import polyglot.util.CodeWriter;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.infoflow.analysis.SecurityPolicy;

/**
 * Erasure security policies
 */
public class ErasurePolicy extends FlowPolicy {

    private final FlowPolicy initialPol;
    private final AccessPath condition;
    private final FlowPolicy finalPol;

    public ErasurePolicy(FlowPolicy initialPol, AccessPath condition,
            FlowPolicy finalPol) {
        this.initialPol = initialPol;
        this.condition = condition;
        this.finalPol = finalPol;
    }

    @Override
    public boolean leq(SecurityPolicy p) {
        if (super.leq(p)) {
            return true;
        }
        FlowPolicy that = (FlowPolicy) p;
        if (this == that) return true;
        if (that instanceof ErasurePolicy) {
            ErasurePolicy ep = (ErasurePolicy) that;
            return this.condition.equals(ep.condition)
                    && this.initialPol.leq(ep.initialPol)
                    && this.finalPol.leq(ep.finalPol);
        }
        return (this.initialPol.leq(that) && this.finalPol.leq(that));
    }

    @Override
    public boolean isBottom() {
        return false;
    }

    @Override
    public FlowPolicy upperBound(SecurityPolicy p) {
        FlowPolicy that = (FlowPolicy) p;
        if (this == that) return this;
        if (that == CESecurityPolicyFactory.LOW) return this;
        if (that == CESecurityPolicyFactory.HIGH) return that;

        if (this.leq(that)) {
            return that;
        }
        if (that.leq(this)) {
            return this;
        }
        if (that instanceof ErasurePolicy) {
            ErasurePolicy eThat = (ErasurePolicy) that;
            if (eThat.condition().equals(this.condition())) {
                // recurse!
                FlowPolicy initialP =
                        this.initialPolicy().upperBound(eThat.initialPolicy());
                FlowPolicy finalP =
                        this.finalPolicy().upperBound(eThat.finalPolicy());
                return new ErasurePolicy(initialP, this.condition, finalP);

            }
            // hard to know what to do. Return top.
            return CESecurityPolicyFactory.HIGH;
        }
        return CESecurityPolicyFactory.ERROR;
    }

    @Override
    public SecurityPolicy widen(SecurityPolicy that) {
        return upperBound(that);
    }

    @Override
    public int hashCode() {
        return this.initialPol.hashCode() ^ this.condition.hashCode()
                ^ this.finalPol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ErasurePolicy) {
            ErasurePolicy that = (ErasurePolicy) obj;
            return this.initialPol.equals(that.initialPol)
                    && this.condition.equals(that.condition)
                    && this.finalPol.equals(that.finalPol);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.initialPol);
        sb.append("/");
        sb.append(this.condition);
        sb.append(" ");
        sb.append(this.finalPol);
        return sb.toString();
    }

    @Override
    public void prettyPrint(CodeWriter cw) {
        cw.write(this.toString());
    }

    public FlowPolicy initialPolicy() {
        return this.initialPol;
    }

    public AccessPath condition() {
        return this.condition;
    }

    public FlowPolicy finalPolicy() {
        return this.finalPol;
    }
}
