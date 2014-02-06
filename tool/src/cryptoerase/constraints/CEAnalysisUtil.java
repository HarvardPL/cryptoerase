package cryptoerase.constraints;

import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.InfoFlowFactoryHelper;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.ast.PolicyNode;
import cryptoerase.securityPolicy.CESecurityPolicy;

public class CEAnalysisUtil extends IFConsAnalysisUtil {

    public CEAnalysisUtil(WorkQueue<Unit> workQueue,
            AnalysisUnit currentProcCall, AccrueExtensionInfo extInfo) {
        super(workQueue, currentProcCall, extInfo);
    }

    @Override
    protected CEDataFlow createDataflow() {
        return new CEDataFlow(this,
                              this.workQueue(),
                              this.extInfo.scheduler().currentJob(),
                              this.extInfo.typeSystem(),
                              this.extInfo.nodeFactory());
    }

    public CESecurityPolicy convert(PolicyNode policyNode) {
        InfoFlowFactoryHelper<SecurityPolicy, Unit> facHelper =
                (InfoFlowFactoryHelper<SecurityPolicy, Unit>) workQueue.factory();
        CESecurityPolicyFactory<Unit> secPolFac =
                (CESecurityPolicyFactory<Unit>) facHelper.securityPolicyFactory();
        return policyNode.policy(secPolFac);
    }

}
