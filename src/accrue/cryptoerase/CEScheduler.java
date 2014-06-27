package accrue.cryptoerase;

import polyglot.ast.NodeFactory;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import accrue.AccrueSchedulerHelper;
import accrue.cryptoerase.constraints.CEConstraintsGoal;
import accrue.cryptoerase.possibleSetConditions.FindPossiblySetConditionsGoal;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.InfoFlowScheduler;

public class CEScheduler extends InfoFlowScheduler {

	public CEScheduler(InfoFlowExtensionInfo extInfo) {
        super(extInfo);
    }

    @Override
    protected AccrueSchedulerHelper createHelper() {
        return new CESchedulerHelper(extInfo, this);
    }

    @Override
    public Goal InfoFlowConstraints() {
        return CEConstraintsGoal.singleton((CryptoErasureExtensionInfo) extInfo);
    }

    class CESchedulerHelper extends AccrueSchedulerHelper {

        public CESchedulerHelper(ExtensionInfo extInfo, Scheduler sched) {
            super(extInfo, sched);
        }

        @Override
        public Goal AnalysesDone(Job job) {
            Goal g = super.AnalysesDone(job);
            try {
                g.addPrerequisiteGoal(InfoFlowConstraints(), this.sched);
                /*g.addPrerequisiteGoal(MissingCodeReportGoal.singleton(extInfo,
                                                                      "missing.txt"),
                                      this.sched);*/
            }
            catch (CyclicDependencyException e) {
                throw new InternalCompilerError(e);
            }
            return g;
        }
    }

    public Goal FindPossiblySetConditions() {
        return FindPossiblySetConditionsGoal.singleton(extInfo);
    }
}
