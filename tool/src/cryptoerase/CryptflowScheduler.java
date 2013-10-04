package cryptflow;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.util.InternalCompilerError;
import accrue.analysis.AnalysisScheduler;
import accrue.infoflow.InfoFlowExtensionInfo;

public class CryptflowScheduler extends AnalysisScheduler {

	public CryptflowScheduler(CryptflowExtensionInfo extInfo) {
		super(extInfo);
	}

	public Goal InfoFlowConstraints() {
		return CFConstraintsGoal.singleton((CryptflowExtensionInfo) extInfo);
	}

	@Override
	public Goal AnalysesDone(Job job) {
		Goal g = super.AnalysesDone(job);
		try {
			// g.addPrerequisiteGoal(this.InfoFlow(), this);
			g.addPrerequisiteGoal(this.InfoFlowConstraints(), this);
		} catch (CyclicDependencyException e) {
			throw new InternalCompilerError(e);
		}
		return g;
	}

}
