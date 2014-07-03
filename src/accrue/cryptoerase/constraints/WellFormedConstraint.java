package accrue.cryptoerase.constraints;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import accrue.cryptoerase.securityPolicy.AccessPath;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.cryptoerase.securityPolicy.ErasurePolicy;
import accrue.cryptoerase.securityPolicy.FlowPolicy;
import accrue.cryptoerase.securityPolicy.LevelPolicy;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;

/**
 * A NoConditionConstraint for variable var represents the
 * requirement that the solution for variable var should not
 * contain the condition <code>condition</code> in an erasure
 * policy. If <code>condition</code> is null, then this represents
 * the requirement that var should not contain any conditions (i.e.,
 * it should not be, or include, an erasure policy).
 *
 */
public class WellFormedConstraint implements Constraint {
    private final SecurityPolicyVariable var;
    private final SecurityPolicy polToCheck;
    private final SecurityPolicy conditionPol;
    private final AccessPath condition;
    private final Position pos;

    public WellFormedConstraint(SecurityPolicyVariable var,
    		AccessPath condition,
    		SecurityPolicy conditionPol, Position pos) {
        this.var = var;
        this.conditionPol = conditionPol;
        this.pos = pos;
        this.condition = condition;
        this.polToCheck = null;
    }

    public WellFormedConstraint(SecurityPolicy polToCheck, AccessPath condition, SecurityPolicy conditionPol, Position pos) {
    	this.var = null;
        this.conditionPol = conditionPol;
        this.condition = condition;
        this.pos = pos;
        this.polToCheck = polToCheck;
    }

    public SecurityPolicyVariable var() {
        return this.var;
    }

    public SecurityPolicy polToCheck() {
        return this.polToCheck;
    }
    
    public SecurityPolicy conditionPolicy() {
    	return this.conditionPol;
    }

    @Override
    public Position pos() {
        return this.pos;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfIncreased() {
    	Set<SecurityPolicyVariable> invalid = new LinkedHashSet<SecurityPolicyVariable>();
    	invalid.add(this.var);
    	if (this.conditionPol instanceof SecurityPolicyVariable) {
    		invalid.add((SecurityPolicyVariable) this.conditionPol);
    	}
    	return invalid;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfDecreased() {
    	Set<SecurityPolicyVariable> invalid = new LinkedHashSet<SecurityPolicyVariable>();
    	invalid.add(this.var);
    	if (this.conditionPol instanceof SecurityPolicyVariable) {
    		invalid.add((SecurityPolicyVariable) this.conditionPol);
    	}
    	return invalid;
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
    public boolean satisfies(SecurityPolicy cp, SecurityPolicy condPol) {
        FlowPolicy cpFp = ((CESecurityPolicy) cp).flowPol();
        FlowPolicy condFp = ((FlowPolicy) condPol);
        return satisfies(cpFp, condFp);
    }

    protected boolean satisfies(FlowPolicy p, FlowPolicy condPol) {
    	if (condPol == null) {
    		return true;
    	}
    	if (p == null) {
    		return false;
    	}
        if (p instanceof LevelPolicy) {
            return true;
        }
        if (p instanceof ErasurePolicy) {
        	ErasurePolicy ep = (ErasurePolicy) p;
        	if (ep.condition().mayOverlap(this.condition)) {
        		//System.out.println("\t\t" + condPol + " <= " + ep.initialPolicy() + " ? " + condPol.leq(ep.initialPolicy()));
        		//System.out.println("\t\t" + condPol + " <= " + ep.finalPolicy() + " ? " + condPol.leq(ep.finalPolicy()));
        		if (!condPol.leq(ep.initialPolicy()) || !condPol.leq(ep.finalPolicy())) {
        			return false;
        		}
        	}
        	
            return this.satisfies(ep.initialPolicy(), condPol)
                    && this.satisfies(ep.finalPolicy(), condPol);
        }
        throw new InternalCompilerError("Don't know how to deal with " + p
                + " " + p.getClass());
    }
    
    @Override
	public String toString() {
		return "WellFormedConstraint [var=" + var + ", polToCheck=" + polToCheck
				+ ", pos=" + pos + "]";
	}

}
