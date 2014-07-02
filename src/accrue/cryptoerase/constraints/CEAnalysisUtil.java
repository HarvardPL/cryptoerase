package accrue.cryptoerase.constraints;

import polyglot.ast.Node;
import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.ast.PolicyNode;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.infoflow.analysis.AbstractInfoFlowContext;
import accrue.infoflow.analysis.InfoFlowFactoryHelper;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsContext;

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
    
    public SecurityPolicy copyAndConstrainPC(IFConsContext context, Node node) {
    	IFConsAnalysisFactory af = (IFConsAnalysisFactory)workQueue().factory();
    	return copyAndConstrain(af.pcmapToSecurityPolicy(context), "pc-" + node, node);
    }

}
