package accrue.cryptoerase;

import polyglot.ast.NodeFactory;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.EmptyGoal;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import accrue.AccrueScheduler;
import accrue.AccrueSchedulerHelper;
import accrue.analysis.goals.MissingCodeReportGoal;
import accrue.cryptoerase.constraints.CEConstraintsGoal;
import accrue.cryptoerase.goals.ConditionsChecked;
import accrue.cryptoerase.possibleSetConditions.FindPossiblySetConditionsGoal;
import accrue.cryptoerase.translate.CERewriter;
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

    public Goal PreRemoveCE(Job job) {
    	Goal g = new EmptyGoal(job, "PreRemoveCE");
    	try {
    		// Serialize before translating away!
    		g.addPrerequisiteGoal(Serialized(job), this);
    		g.addPrerequisiteGoal(this.helper().AnalysesDone(job), this);
    	} catch (CyclicDependencyException e) {
    		throw new InternalCompilerError(e);
    	}
    	return internGoal(g);
    }
    
    public Goal RemoveCE(Job job) {
    	Goal g = new VisitorGoal(job, new CERewriter(job, extInfo, extInfo.outputExtensionInfo()));
    	try {
    		g.addPrerequisiteGoal(PreRemoveCE(job), this);
    	} catch (CyclicDependencyException e){
    		throw new InternalCompilerError(e);
    	}
    	
    	return internGoal(g);
    }
    
    @Override
    public Goal CodeGenerated(Job job) {
        // Because we want the target language to compile our
        // translation, do not generate code now.
        Goal g = new EmptyGoal(job, "CodeGenerated");
        // Add a prerequisite goal to translate cryptoerase features.
        try {
            g.addPrerequisiteGoal(RemoveCE(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }
    
    @Override
    public boolean runToCompletion() {
        boolean complete = super.runToCompletion();
        if (complete) {
            // Call the compiler for output files to compile our translated
            // code.
            ExtensionInfo outExtInfo = extInfo.outputExtensionInfo();
            Scheduler outScheduler = outExtInfo.scheduler();

            // Create a goal to compile every source file.
            for (Job job : outScheduler.jobs()) {
                Job newJob = outScheduler.addJob(job.source(), job.ast());
                outScheduler.addGoal(outExtInfo.getCompileGoal(newJob));
            }
            return outScheduler.runToCompletion();
        }
        return complete;
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
				g.addPrerequisiteGoal(
						ConditionsChecked.create(this.sched, job,
								extInfo.typeSystem(), extInfo.nodeFactory()),
						this.sched);
				g.addPrerequisiteGoal(FindPossiblySetConditions(), this.sched);
				g.addPrerequisiteGoal(InfoFlowConstraints(), this.sched);
				g.addPrerequisiteGoal(MissingCodeReportGoal.singleton(extInfo, "missing.txt"), this.sched);
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
