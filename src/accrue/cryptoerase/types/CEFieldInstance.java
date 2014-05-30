package accrue.cryptoerase.types;

import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import polyglot.types.FieldInstance;

public interface CEFieldInstance extends FieldInstance {

    void setDeclaredPolicy(CESecurityPolicy declaredPolicy);

    CESecurityPolicy declaredPolicy();

}
