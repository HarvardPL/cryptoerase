package cryptoerase.securityPolicy;

import accrue.infoflow.analysis.SecurityPolicy;

public abstract class FlowPolicy implements SecurityPolicy {

    /**
     * Handle the cases that depend on the form of p.
     */
    @Override
    public boolean leq(SecurityPolicy p) {
        if (p instanceof ErasurePolicy) {
            ErasurePolicy ep = (ErasurePolicy) p;
            return this.leq(ep.initialPolicy()) && this.leq(ep.finalPolicy());
        }
        return false;
    }

    @Override
    public abstract FlowPolicy upperBound(SecurityPolicy o);

}
