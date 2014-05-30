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
import polyglot.util.Position;

public class CETypeSystem_c extends TypeSystem_c implements CETypeSystem {

    private static final PrimitiveType.Kind CONDITION_KIND =
            new PrimitiveType.Kind("condition");
    protected PrimitiveType CONDITION_;

    @Override
    public void initialize(TopLevelResolver loadedResolver,
            ExtensionInfo extInfo) throws SemanticException {
        super.initialize(loadedResolver, extInfo);
        CONDITION_ = new PrimitiveType_c(this, CONDITION_KIND);
    }

    @Override
    public PrimitiveType Condition() {
        return CONDITION_;
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
        // boolean and condition can be implicitly cast to each other
        if ((Condition().equals(fromType) || Boolean().equals(fromType))
                && (Condition().equals(toType) || Boolean().equals(toType))) {
            return true;
        }
        return super.isImplicitCastValid(fromType, toType);
    }

    @Override
    public boolean isCastValid(Type fromType, Type toType) {
        // boolean and condition can be cast to each other
        if ((Condition().equals(fromType) || Boolean().equals(fromType))
                && (Condition().equals(toType) || Boolean().equals(toType))) {
            return true;
        }

        return super.isCastValid(fromType, toType);
    }

}
