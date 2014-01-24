package cryptoerase.possibleSetConditions;

import polyglot.ast.ProcedureDecl;
import polyglot.types.SemanticException;
import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisFactory;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.WorkQueue;

public class SetConditionsAnalysisFactory extends
        AnalysisFactory<SetConditionsAbsVal> {
    public SetConditionsAnalysisFactory(AccrueExtensionInfo extInfo) {
        super(extInfo);
    }

    public static final String ANALYSIS_NAME =
            "cryptoerase.possibleSetConditions";

    @Override
    public String analysisName() {
        return ANALYSIS_NAME;
    }

    @Override
    public String analysisReportName() {
        return analysisName();
    }

    @Override
    public SetConditionsAbsVal startItem(
            AnalysisUtil<SetConditionsAbsVal> autil, ProcedureDecl startProc)
            throws SemanticException {
        // the DFIContext for main(String[] args)
        return SetConditionsAbsVal.EMPTY;
    }

    @Override
    public AnalysisUtil<SetConditionsAbsVal> analysisUtil(
            WorkQueue<SetConditionsAbsVal> wq, AnalysisUnit aunit) {
        return new SetConditionsAnalysisUtil(wq, aunit, extInfo);
    }

}
