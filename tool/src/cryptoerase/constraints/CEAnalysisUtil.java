package cryptoerase.constraints;

import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;

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

}
