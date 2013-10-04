package cryptoerase.constraints;

import accrue.ObjAnalExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;

public class CEConstraintsAnalysisFactory extends IFConsAnalysisFactory {

    public CEConstraintsAnalysisFactory(ObjAnalExtensionInfo extInfo,
            SecurityPolicyFactory<Unit> securityPolicyFactory) {
        super(extInfo, securityPolicyFactory);
    }

    @Override
    public AnalysisUtil<Unit> analysisUtil(WorkQueue<Unit> wq, AnalysisUnit pc) {
        return new CEAnalysisUtil(wq, pc, extInfo);
    }

}
