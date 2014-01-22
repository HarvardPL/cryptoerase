package cryptoerase.types;

import polyglot.types.FieldInstance;
import cryptoerase.securityPolicy.CESecurityPolicy;

public interface CEFieldInstance extends FieldInstance {

    void setDeclaredPolicy(CESecurityPolicy declaredPolicy);

    CESecurityPolicy declaredPolicy();

}
