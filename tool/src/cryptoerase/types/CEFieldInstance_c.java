package cryptoerase.types;

import polyglot.types.FieldInstance_c;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import cryptoerase.securityPolicy.CESecurityPolicy;

public class CEFieldInstance_c extends FieldInstance_c implements
        CEFieldInstance {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private CESecurityPolicy declaredPolicy = null;

    public CEFieldInstance_c(CETypeSystem_c ts, Position pos,
            ReferenceType container, Flags flags, Type type, String name) {
        super(ts, pos, container, flags, type, name);
    }

    @Override
    public CESecurityPolicy declaredPolicy() {
        return declaredPolicy;
    }

    @Override
    public void setDeclaredPolicy(CESecurityPolicy declaredPolicy) {
        this.declaredPolicy = declaredPolicy;
    }

}
