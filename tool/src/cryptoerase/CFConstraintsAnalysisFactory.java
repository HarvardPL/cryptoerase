package cryptflow;

import accrue.ObjAnalExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.Unit;

public class CFConstraintsAnalysisFactory extends IFConsAnalysisFactory {

	public CFConstraintsAnalysisFactory(ObjAnalExtensionInfo extInfo,
			SecurityPolicyFactory<Unit> securityPolicyFactory) {
		super(extInfo, securityPolicyFactory);
	}
	
    @Override
    public AnalysisUtil<Unit> analysisUtil(WorkQueue<Unit> wq, AnalysisUnit pc) {
        return new CFAnalysisUtil(wq, pc, extInfo);
    }


}
