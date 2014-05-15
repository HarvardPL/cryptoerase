package cryptoerase.constraints;

import java.util.HashMap;
import java.util.Map;

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
import cryptoerase.CryptoErasureExtensionInfo;

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
    static public CEConstraintsPass singleton(
            CryptoErasureExtensionInfo extInfo, CEConstraintsGoal goal) {
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
        // solve the constraints
        CEConstraintsAnalysisFactory fac =
                (CEConstraintsAnalysisFactory) this.factory;
        System.out.println("Finished constraints! Now need to do stuff with the set of constraints.");

        /*for (Set<Constraint> cs : fac.constraintSet()
                                     .getAllConstraintSets()
                                     .values()) {
            for (Constraint c : cs) {
                System.out.println("   " + c);
            }
        }*/

        ConstraintSolution soln = fac.constraintSet().leastSolution(null);
        if (soln.solve()) {
            System.out.println("\nGood news! The program security constraints have a solution!");
        }
        else {
            System.out.println("\nUh oh! The program security constraints DO NOT have a solution!");
            soln.dumpErrors(System.out);
            System.out.flush();
            System.exit(1);
        }

        // Set the field instance variables
        /*for (FieldInstance fi : fac.allFieldInstancesWithVars()) {
            SecurityPolicyVariable v = fac.getFieldInstanceVar(fi);
            SecurityPolicy solved = soln.subst(v);
            CEFieldInstance cefi = (CEFieldInstance) fi;
            if (cefi.declaredPolicy() != null) {
                throw new InternalCompilerError("Seem to have inferred policy for a field that has an explicit policy: "
                        + fi);
            }
            cefi.setDeclaredPolicy((CESecurityPolicy) solved);
            System.out.println("Set label of " + fi + " to " + solved);
        }*/

    }

}
