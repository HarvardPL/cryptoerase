package accrue.cryptoerase.possibleSetConditions;

import java.util.HashMap;
import java.util.Map;

import polyglot.frontend.ExtensionInfo;
import accrue.AccrueExtensionInfo;
import accrue.analysis.goals.RegisterProceduresGoal;
import accrue.analysis.interprocanalysis.InterProcAnalysisPass;
import accrue.analysis.interprocanalysis.Registrar;

public class FindPossiblySetConditionsPass extends
        InterProcAnalysisPass<SetConditionsAbsVal> {

    static private Map<ExtensionInfo, FindPossiblySetConditionsPass> singletons =
            new HashMap<ExtensionInfo, FindPossiblySetConditionsPass>();

    static public FindPossiblySetConditionsPass singleton(
            AccrueExtensionInfo extInfo) {

        FindPossiblySetConditionsPass singleton = singletons.get(extInfo);
        if (singleton == null) {
            singleton =
                    new FindPossiblySetConditionsPass(extInfo,
                                                      RegisterProceduresGoal.registrar(extInfo),
                                                      new SetConditionsAnalysisFactory(extInfo));
            singletons.put(extInfo, singleton);
        }
        return singleton;
    }

    private FindPossiblySetConditionsPass(ExtensionInfo extInfo,
            Registrar registrar, SetConditionsAnalysisFactory analysisFactory) {
        super(extInfo,
              FindPossiblySetConditionsGoal.singleton(extInfo),
              registrar,
              analysisFactory);
    }

}
