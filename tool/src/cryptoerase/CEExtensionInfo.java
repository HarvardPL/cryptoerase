package cryptoerase;

import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.dataflow.PowerSetSecurityPolicyFactory;

public class CEExtensionInfo<A extends Ordered<A>> extends InfoFlowExtensionInfo<A> {
    @Override
    protected Scheduler createScheduler() {
        return new CEScheduler(this);
    }

    @Override
    public SecurityPolicyFactory<A> createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return new CESecurityPolicyFactory<A>();
    }

}
