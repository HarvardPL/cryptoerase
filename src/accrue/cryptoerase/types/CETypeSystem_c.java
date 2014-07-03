package accrue.cryptoerase.types;

import polyglot.frontend.ExtensionInfo;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class CETypeSystem_c extends TypeSystem_c implements CETypeSystem {

	@Override
	public Type Condition() {
		try {
			return this.typeForName("accrue.cryptoerase.runtime.Condition");
		} catch (SemanticException e) {
			throw new InternalCompilerError("Could not find type accrue.cryptoerase.runtime.Condition");
		}
	}	

    @Override
    public FieldInstance fieldInstance(Position pos, ReferenceType container,
            Flags flags, Type type, String name) {
        assert_(container);
        assert_(type);
        return new CEFieldInstance_c(this, pos, container, flags, type, name);
    }

    @Override
    public LocalInstance localInstance(Position pos, Flags flags, Type type,
            String name) {
        assert_(type);
        return new CELocalInstance_c(this, pos, flags, type, name);
    }

	@Override
	public boolean isImplicitCastValid(Type fromType, Type toType) {
		if (typeEquals(fromType, Condition()) && !typeEquals(toType, Condition())) {
			return false;
		}
		return super.isImplicitCastValid(fromType, toType);
	}
}
