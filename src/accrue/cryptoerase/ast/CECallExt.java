package accrue.cryptoerase.ast;

import polyglot.ast.Call;
import polyglot.types.Type;
import accrue.cryptoerase.types.CETypeSystem;

public class CECallExt extends CEProcedureCallExt {
	private static final long serialVersionUID = -46187506543797712L;

	@Override
	public boolean isConditionSet() {
		Call n = (Call) node();
		Type t = n.target().type();
		return n.name().equals("set") && t.typeEquals(((CETypeSystem)t.typeSystem()).Condition());
	}
}
