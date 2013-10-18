package cryptoerase;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.types.TypeSystem;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.ext.InfoFlowExtFactory_c;
import cryptoerase.ast.CryptoErasureNodeFactory_c;
import cryptoerase.types.CryptoErasureTypeSystem_c;

public class CryptoErasureExtensionInfo extends InfoFlowExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new CEScheduler(this);
    }

    @Override
    public SecurityPolicyFactory createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return new CESecurityPolicyFactory();
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new CryptoErasureNodeFactory_c(new InfoFlowExtFactory_c());
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new CryptoErasureTypeSystem_c();
    }

}
