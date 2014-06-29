package accrue.cryptoerase.securityPolicy;

import java.io.Serializable;

import polyglot.util.CodeWriter;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.infoflow.analysis.SecurityPolicy;

/**
 * Erasure security policies
 */
public class LevelPolicy extends FlowPolicy implements Serializable {

    private final String level;

    public LevelPolicy(String level) {
        this.level = level;
        if (level == null) {
            throw new IllegalArgumentException("Level must be non null");
        }
    }

    @Override
    public boolean leq(SecurityPolicy p) {
        FlowPolicy that = (FlowPolicy) p;
        if (that instanceof ErasurePolicy) {
        	ErasurePolicy ethat = (ErasurePolicy)that;
        	return this.leq(ethat.initialPolicy()) && this.leq(ethat.finalPolicy());
        }
        
        if (this == p) return true;
        
        if (p == CESecurityPolicyFactory.TOP) return true;
        if (this == CESecurityPolicyFactory.BOTTOM) return true;

        if (this == CESecurityPolicyFactory.LOW
                && p == CESecurityPolicyFactory.HIGH) return true;

        return false;
    }

    @Override
    public boolean isBottom() {
        return this == CESecurityPolicyFactory.BOTTOM;
    }

    @Override
    public FlowPolicy upperBound(SecurityPolicy p) {
        FlowPolicy that = (FlowPolicy) p;
        if (this == that) return this;
        if (this == CESecurityPolicyFactory.BOTTOM) return that;

        if (this.leq(that)) return that;
        if (that.leq(this)) return this;

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
