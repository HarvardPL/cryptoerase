package cryptoerase;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.util.InternalCompilerError;
import accrue.analysis.AnalysisScheduler;
import cryptoerase.constraints.CEConstraintsGoal;

public class CEScheduler extends AnalysisScheduler {

    public CEScheduler(CryptoErasureExtensionInfo extInfo) {
        super(extInfo);
    }

    public Goal InfoFlowConstraints() {
        return CEConstraintsGoal.singleton((CryptoErasureExtensionInfo) extInfo);
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
