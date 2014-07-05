package accrue.cryptoerase.constraints;

import java.util.Collections;
import java.util.Set;

import org.json.JSONObject;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import accrue.cryptoerase.CESecurityPolicyFactory;
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
 * A NoTopContraint for variable var represents the
 * requirement that the solution for variable var should not
 * be less than or equal to top.
 */
public class NoTopConstraint implements Constraint {
    private final SecurityPolicyVariable var;
    private final CESecurityPolicy polToCheck;
    private final Position pos;

    public NoTopConstraint(SecurityPolicyVariable var, Position pos) {
        this.var = var;
        this.pos = pos;
        this.polToCheck = null;
    }

    public NoTopConstraint(CESecurityPolicy polToCheck, Position pos) {
        this.var = null;
        this.pos = pos;
        this.polToCheck = polToCheck;
    }

    public SecurityPolicyVariable var() {
        return this.var;
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
    public boolean satisfies(SecurityPolicy cp) {
        CESecurityPolicy cesp = (CESecurityPolicy) cp;
        return satisfies(cesp.flowPol());
    }

    protected boolean satisfies(FlowPolicy p) {
    	if (p == null) {
    		return true;
    	}
        if (p instanceof LevelPolicy) {
            return !CESecurityPolicyFactory.TOP.leq(p);
        }
        if (p instanceof ErasurePolicy) {
        	ErasurePolicy ep = (ErasurePolicy) p;
        	return this.satisfies(ep.initialPolicy());
        }
        throw new InternalCompilerError("Don't know how to deal with " + p
                + " " + p.getClass());
    }

	@Override
	public String toString() {
		return "NoTopConstraint [var=" + var + ", polToCheck=" + polToCheck
				+ ", pos=" + pos + "]";
	}
    
}
