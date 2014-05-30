package accrue.cryptoerase.possibleSetConditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import accrue.AccrueExtensionInfo;
import accrue.AccrueScheduler;

public class FindPossiblySetConditionsGoal extends AbstractGoal {
    public static Goal singleton(ExtensionInfo extInfo) {
        Scheduler scheduler = extInfo.scheduler();
        return scheduler.internGoal(new FindPossiblySetConditionsGoal());
    }

    protected FindPossiblySetConditionsGoal() {
        super(null, "FindPossiblySetConditionsGoal");
    }

    @Override
    public Collection<Goal> prerequisiteGoals(Scheduler scheduler) {
        List<Goal> l = new ArrayList<Goal>();
        l.add(((AccrueScheduler) scheduler).helper().PointerAnalysis());
        l.addAll(super.prerequisiteGoals(scheduler));
        return l;
    }

    @Override
    public Pass createPass(ExtensionInfo extInfo) {
        return FindPossiblySetConditionsPass.singleton((AccrueExtensionInfo) extInfo);
    }

}
