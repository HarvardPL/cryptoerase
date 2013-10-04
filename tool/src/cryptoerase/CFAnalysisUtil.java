package cryptflow;

import accrue.ObjAnalExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsDataFlow;
import accrue.infoflow.analysis.constraints.Unit;

public class CFAnalysisUtil extends IFConsAnalysisUtil {

	public CFAnalysisUtil(WorkQueue<Unit> workQueue,
			AnalysisUnit currentProcCall, ObjAnalExtensionInfo extInfo) {
		super(workQueue, currentProcCall, extInfo);
	}

	@Override
    protected CFDataFlow createDataflow() {
    	return new CFDataFlow(this, this.workQueue(), this.extInfo.scheduler().currentJob(), this.extInfo.typeSystem(), this.extInfo.nodeFactory());
    }
	
}
