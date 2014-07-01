package accrue.cryptoerase.goals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.types.TypeSystem;
import accrue.cryptoerase.visit.ConditionChecker;

public class ConditionsChecked extends VisitorGoal {

	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts,
            NodeFactory nf) {
        return scheduler.internGoal(new ConditionsChecked(job, ts, nf));
    }

    protected ConditionsChecked(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, new ConditionChecker(job, ts, nf));
    }

    @Override
    public Collection<Goal> prerequisiteGoals(Scheduler scheduler) {
        List<Goal> l = new ArrayList<>();
        l.add(scheduler.TypeChecked(job));
        l.addAll(super.prerequisiteGoals(scheduler));
        return l;
    }

}
