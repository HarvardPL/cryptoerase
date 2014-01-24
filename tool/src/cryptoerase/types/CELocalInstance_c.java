package cryptoerase.types;

import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class CELocalInstance_c extends LocalInstance_c implements
        CELocalInstance {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    private CESecurityPolicy declaredPolicy = null;

    public CELocalInstance_c(CETypeSystem_c ts, Position pos, Flags flags,
            Type type, String name) {
        super(ts, pos, flags, type, name);
    }

//    @Override
//    public CESecurityPolicy declaredPolicy() {
//        return declaredPolicy;
//    }
//
//    @Override
//    public void setDeclaredPolicy(CESecurityPolicy declaredPolicy) {
//        this.declaredPolicy = declaredPolicy;
//    }

}
