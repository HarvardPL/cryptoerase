package cryptoerase.securityPolicy;

import polyglot.util.CodeWriter;
import accrue.infoflow.analysis.SecurityPolicy;
import cryptoerase.CESecurityPolicyFactory;

/**
 * Erasure security policies
 */
public class LevelPolicy extends AbstractCryptoSecurityPolicy implements
        CryptoSecurityPolicy {

    private final String level;

    public LevelPolicy(String level) {
        this.level = level;
        if (level == null) {
            throw new IllegalArgumentException("Level must be non null");
        }
    }

    @Override
    public boolean leq(SecurityPolicy p) {
        CryptoSecurityPolicy that = (CryptoSecurityPolicy) p;
        if (super.leq(that)) {
            return true;
        }
        if (this == p) return true;
        if (this == CESecurityPolicyFactory.LOW
                && p == CESecurityPolicyFactory.HIGH) return true;
        return false;
    }

    @Override
    public boolean isBottom() {
        return this == CESecurityPolicyFactory.LOW;
    }

    @Override
    public SecurityPolicy upperBound(SecurityPolicy p) {
        CryptoSecurityPolicy that = (CryptoSecurityPolicy) p;
        if (this == that) return this;
        if (this == CESecurityPolicyFactory.HIGH
                && that == CESecurityPolicyFactory.LOW)
            return CESecurityPolicyFactory.HIGH;
        if (this == CESecurityPolicyFactory.LOW
                && that == CESecurityPolicyFactory.HIGH)
            return CESecurityPolicyFactory.HIGH;
        return CESecurityPolicyFactory.ERROR;

    }

    @Override
    public SecurityPolicy widen(SecurityPolicy that) {
        return upperBound(that);
    }

    @Override
    public int hashCode() {
        return level.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return level;
    }

    @Override
    public void prettyPrint(CodeWriter cw) {
        cw.write(this.toString());
    }
}
