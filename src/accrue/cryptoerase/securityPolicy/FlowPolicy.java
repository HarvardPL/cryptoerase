package accrue.cryptoerase.securityPolicy;

import java.util.Set;

import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;

public abstract class FlowPolicy implements SecurityPolicy {

	@Override
    public abstract FlowPolicy upperBound(SecurityPolicy o);

    public abstract Set<AccessPath> conditions();
    
}
