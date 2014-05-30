package accrue.cryptoerase.ast;

import accrue.cryptoerase.types.CETypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.BooleanLit;
import polyglot.util.SerialVersionUID;

public class CEAssignExt extends CEExt_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public boolean isConditionSet() {
        Assign a = (Assign) this.node();
        CETypeSystem ts = (CETypeSystem) a.type().typeSystem();
        if (a.type().equals(ts.Condition()) && a.right() instanceof BooleanLit) {
            // it's an assignment to a condition, with the RHS being a boolean lit
            BooleanLit b = (BooleanLit) a.right();
            return b.value();
        }
        return false;
    }

}
