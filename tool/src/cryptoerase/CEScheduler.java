package cryptoerase;

import cryptoerase.constraints.CEConstraintsGoal;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.util.InternalCompilerError;
import accrue.analysis.AnalysisScheduler;

public class CEScheduler extends AnalysisScheduler {

    public CEScheduler(CEExtensionInfo extInfo) {
        super(extInfo);
    }

    public Goal InfoFlowConstraints() {
        return CEConstraintsGoal.singleton((CEExtensionInfo) extInfo);
    }

    @Override
    public Goal AnalysesDone(Job job) {
        Goal g = super.AnalysesDone(job);
        try {
            // g.addPrerequisiteGoal(this.InfoFlow(), this);
            g.addPrerequisiteGoal(this.InfoFlowConstraints(), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return g;
    }

}
