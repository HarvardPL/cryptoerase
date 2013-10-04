package cryptflow;

import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.dataflow.PowerSetSecurityPolicyFactory;

public class CryptflowExtensionInfo<A extends Ordered<A>> extends InfoFlowExtensionInfo<A> {
    @Override
    protected Scheduler createScheduler() {
        return new CryptflowScheduler(this);
    }

    @Override
    public SecurityPolicyFactory<A> createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return new CryptoSecurityPolicyFactory<A>();
    }

}
