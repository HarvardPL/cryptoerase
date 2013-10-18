package cryptoerase.types;

import polyglot.frontend.ExtensionInfo;
import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.TypeSystem_c;

public class CryptoErasureTypeSystem_c extends TypeSystem_c implements
        CryptoEraseTypeSystem {

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

}
