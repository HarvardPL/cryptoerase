package cryptoerase;

import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;

public class CEExtensionInfo extends InfoFlowExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new CEScheduler(this);
    }

    @Override
    public SecurityPolicyFactory createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return new CESecurityPolicyFactory();
    }

}
