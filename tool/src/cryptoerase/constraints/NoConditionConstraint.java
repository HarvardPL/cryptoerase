package cryptoerase.constraints;

import java.util.Collections;
import java.util.Set;

import org.json.JSONObject;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.securityPolicy.ErasurePolicy;
import cryptoerase.securityPolicy.LevelPolicy;

/**
 * A NoConditionConstraint for variable var represents the
 * requirement that the solution for variable var should not
 * contain the condition <code>condition</code> in an erasure
 * policy. If <code>condition</code> is null, then this represents
 * the requirement that var should not contain any conditions (i.e.,
 * it should not be, or include, an erasure policy).
 *
 */
public class NoConditionConstraint implements Constraint {
    private final SecurityPolicyVariable var;
    private final SecurityPolicy polToCheck;
    private final AccessPath condition;
    private final Position pos;

    public NoConditionConstraint(SecurityPolicyVariable var,
            AccessPath condition, Position pos) {
        this.var = var;
        this.condition = condition;
        this.pos = pos;
        this.polToCheck = null;
    }

    public NoConditionConstraint(SecurityPolicyVariable var, Position pos) {
        this.var = var;
        this.condition = null;
        this.pos = pos;
        this.polToCheck = null;
    }

    public NoConditionConstraint(CESecurityPolicy polToCheck, Position pos) {
        this.var = null;
        this.condition = null;
        this.pos = pos;
        this.polToCheck = polToCheck;
    }

    public SecurityPolicyVariable var() {
        return this.var;
    }

    public AccessPath condition() {
        return this.condition;
    }

    public SecurityPolicy polToCheck() {
        return this.polToCheck;
    }

    @Override
    public Position pos() {
        return this.pos;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfIncreased() {
        return Collections.singleton(this.var);
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfDecreased() {
        return Collections.singleton(this.var);
    }

    @Override
    public String dotOutput(ConstraintKind kind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject getJSON(String label) {
        throw new UnsupportedOperationException();
    }

    /**
     * Check whether policy p satisfies this constraint
     */
    public boolean satisfies(SecurityPolicy p) {
        if (p instanceof LevelPolicy) {
            return true;
        }
        if (p instanceof ErasurePolicy) {
            if (this.condition == null) {
                // p shouldn't contain any erasure policies!
                return false;
            }
            ErasurePolicy ep = (ErasurePolicy) p;
            if (ep.condition().equals(this.condition)) {
                return false;
            }
            return this.satisfies(ep.initialPolicy())
                    && this.satisfies(ep.finalPolicy());
        }
        throw new InternalCompilerError("Don't know how to deal with " + p
                + " " + p.getClass());
    }

}
