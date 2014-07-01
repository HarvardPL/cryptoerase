package accrue.cryptoerase.constraints;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.goals.Goal;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import accrue.analysis.goals.RegisterProceduresGoal;
import accrue.analysis.interprocanalysis.AnalysisFactory;
import accrue.analysis.interprocanalysis.InterProcAnalysisPass;
import accrue.analysis.interprocanalysis.Registrar;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.cryptoerase.CryptoErasureExtensionInfo;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.cryptoerase.securityPolicy.ErasurePolicy;
import accrue.cryptoerase.securityPolicy.FlowPolicy;
import accrue.cryptoerase.types.CEFieldInstance;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.ConstraintSolution;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;

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
     * @throws SemanticException 
     */
    @Override
    protected void postSuccessfulProcess(WorkQueue<Unit> workQueue) throws SemanticException {
        super.postSuccessfulProcess(workQueue);
        // solve the constraints
        CEConstraintsAnalysisFactory fac =
                (CEConstraintsAnalysisFactory) this.factory;

        ConstraintSolution soln = fac.constraintSet().leastSolution(null);
        if (!soln.solve()) {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	PrintStream ps = new PrintStream(baos);
        	soln.dumpErrors(ps);
        	String errors = null;
			try {
				errors = baos.toString("UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            throw new SemanticException("Uh oh! The program security constraints DO NOT have a solution!\n" + errors);
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
        }
        */
    }

}
