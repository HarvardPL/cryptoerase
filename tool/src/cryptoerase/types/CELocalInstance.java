package cryptoerase.types;

import polyglot.types.LocalInstance;
import cryptoerase.securityPolicy.CESecurityPolicy;

public interface CELocalInstance extends LocalInstance {

    void setDeclaredPolicy(CESecurityPolicy declaredPolicy);

    CESecurityPolicy declaredPolicy();

}
