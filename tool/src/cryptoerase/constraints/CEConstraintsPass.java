package cryptoerase.constraints;

import java.util.HashMap;
import java.util.Map;

import cryptoerase.CEExtensionInfo;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.goals.Goal;
import accrue.analysis.goals.RegisterProceduresGoal;
import accrue.analysis.interprocanalysis.AnalysisFactory;
import accrue.analysis.interprocanalysis.InterProcAnalysisPass;
import accrue.analysis.interprocanalysis.Registrar;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.constraints.ConstraintSolution;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;

/**
 * Constraint-based information flow compiler pass
 * 
 * <p>
 * This analysis does not return any results, but produces output after a
 * successful pass (hence the {@link Unit} type for the analysis results).
 */
public class CEConstraintsPass extends InterProcAnalysisPass<Unit> {

    static private Map<ExtensionInfo, CEConstraintsPass> singletons =
            new HashMap<ExtensionInfo, CEConstraintsPass>();

    /**
     * Create the singleton instance of this class for the given {@link InfoFlowExtensionInfo}
     * @param extInfo compiler extension info
     * @param goal compiler goal used by the scheduler
     * @return The instance of this for <code>extInfo</code>
     */
    static public CEConstraintsPass singleton(CEExtensionInfo extInfo,
            CEConstraintsGoal goal) {
        CEConstraintsPass singleton = singletons.get(extInfo);
        if (singleton == null) {
            singleton =
                    new CEConstraintsPass(extInfo,
                                          RegisterProceduresGoal.registrar(extInfo),
                                          goal,
                                          new CEConstraintsAnalysisFactory(extInfo,
                                                                           goal.createSecurityPolicyFactory()));
            singletons.put(extInfo, singleton);
        }
        return singleton;
    }

    /**
     * Create a new compiler pass
     * 
     * @param extInfo
     *            compiler extension info
     * @param registrar
     *            map of procedures to analysis results
     * @param goal
     *            compiler goal used by the scheduler
     * @param analysisFactory
     *            analyis utility class
     * @param extInfo
     *            compiler extension info
     */
    protected CEConstraintsPass(ExtensionInfo extInfo, Registrar registrar,
            Goal goal, AnalysisFactory<Unit> analysisFactory) {
        super(extInfo, goal, registrar, analysisFactory);
    }

    /**
     * Produce the output specified by the analysis {@link IFConsAnalysisFactory}
     */
    @Override
    protected void postSuccessfulProcess(WorkQueue<Unit> workQueue) {
        super.postSuccessfulProcess(workQueue);
        System.out.println("blahhhh Finished constraints! Now need to do stuff with the set of constraints.");
        // solve the constraints
        CEConstraintsAnalysisFactory fac =
                (CEConstraintsAnalysisFactory) this.factory;
        System.out.println("FOOO");
        ConstraintSolution soln = fac.constraintSet().leastSolution(null);
        System.out.println("Could we solve this set of constraints? "
                + soln.solve());
    }

}
