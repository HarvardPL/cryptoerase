package cryptoerase.constraints;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;

import polyglot.util.Position;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;

/**
 * A DecryptionConstraint requires that
 * keyPol must be a PRIVKEY(pk){p} with pk  <= decResultPol and p <= decResultPol 
 *
 */
public class DecryptionConstraint implements Constraint {
    private final IFConsSecurityPolicy keyPol;
    private final IFConsSecurityPolicy decResultPol;
    private final Position pos;

    public DecryptionConstraint(IFConsSecurityPolicy keyPol,
            IFConsSecurityPolicy decResultPol, Position pos) {
        this.keyPol = keyPol;
        this.decResultPol = decResultPol;
        this.pos = pos;
    }

    public IFConsSecurityPolicy keyPol() {
        return keyPol;
    }

    public IFConsSecurityPolicy decResultPol() {
        return decResultPol;
    }

    @Override
    public Position pos() {
        return this.pos;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfIncreased() {
        return keyPol.variables();
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfDecreased() {
        Set<SecurityPolicyVariable> s =
                new LinkedHashSet<SecurityPolicyVariable>();
        s.addAll(decResultPol.variables());
        s.addAll(keyPol.variables());
        return s;
    }

    @Override
    public String dotOutput(ConstraintKind kind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject getJSON(String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "DecryptionConstraint(key=" + keyPol + "; result="
                + decResultPol + ")";
    }

}
