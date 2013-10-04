package cryptflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import accrue.analysis.domination.DominatorDataFlow;
import accrue.analysis.domination.PostDominatorDataFlow;
import accrue.infoflow.InfoFlowScheduler;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisGoal;
import accrue.infoflow.analysis.constraints.Unit;

public class CFConstraintsGoal extends AbstractGoal {
		
		/**
		 * Get the only copy of this goal.
		 * 
		 * @param extInfo
		 *            Compiler extension info class
		 * @return The instance of the {@link Goal} object for this class
		 */
	    public static Goal singleton(CryptflowExtensionInfo extInfo) {
	        Scheduler scheduler = extInfo.scheduler();
	        return scheduler.internGoal(new CFConstraintsGoal(extInfo));
	    }
	    
	    private final CryptflowExtensionInfo extInfo;

		/**
		 * Create this {@link IFConsAnalysisGoal}
		 * 
		 * @param extInfo
		 *            Compiler extension info class
		 */
	    protected CFConstraintsGoal(CryptflowExtensionInfo extInfo) {
	        super(null, "CryptflowConstraintsGoal");
	        this.extInfo = extInfo;
	    }

		/**
		 * {@link PostDominatorDataFlow} and {@link DominatorDataFlow} are
		 * prerequisites for the constraint-based information flow dataflow
		 * <p>
		 * {@inheritDoc}
		 * 
		 * @param Polyglot compiler scheduler
		 */
	    @Override
		public Collection<Goal> prerequisiteGoals(Scheduler scheduler) {
	        List<Goal> l = new ArrayList<Goal>();
	        l.add(((CryptflowScheduler)scheduler).PostDominator());
	        l.add(((CryptflowScheduler)scheduler).Dominator());
	        return l;
	    }

		@Override
		public Pass createPass(ExtensionInfo extInfo) {
	        return CFConstraintsPass.singleton((CryptflowExtensionInfo)extInfo, this);
	    }

	    /**
	     * Create a factory for managing {@link SecurityPolicy}
	     */
	    public SecurityPolicyFactory<Unit> createSecurityPolicyFactory() {
	        return extInfo.createSecurityPolicyFactory(this);
	    }
}
