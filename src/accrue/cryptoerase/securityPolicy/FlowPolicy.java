package accrue.cryptoerase.securityPolicy;

import accrue.infoflow.analysis.SecurityPolicy;

public abstract class FlowPolicy implements SecurityPolicy {

    @Override
    public abstract FlowPolicy upperBound(SecurityPolicy o);

}
