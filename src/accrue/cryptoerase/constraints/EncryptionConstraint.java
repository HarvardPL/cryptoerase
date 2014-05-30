package accrue.cryptoerase.constraints;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;

import polyglot.util.Position;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;

/**
 * A EncryptionConstraint requires that
 * keyPol must be a PUBKEY(pk){p} with plaintextPol <= pk and p <= encResultPol 
 *
 */
public class EncryptionConstraint implements Constraint {
    private final IFConsSecurityPolicy keyPol;
    private final IFConsSecurityPolicy plaintextPol;
    private final IFConsSecurityPolicy encResultPol;
    private final Position pos;

    public EncryptionConstraint(IFConsSecurityPolicy keyPol,
            IFConsSecurityPolicy plaintextPol,
            IFConsSecurityPolicy encResultPol, Position pos) {
        this.keyPol = keyPol;
        this.plaintextPol = plaintextPol;
        this.encResultPol = encResultPol;
        this.pos = pos;
    }

    public IFConsSecurityPolicy keyPol() {
        return keyPol;
    }

    public IFConsSecurityPolicy plaintextPol() {
        return plaintextPol;
    }

    public IFConsSecurityPolicy encResultPol() {
        return encResultPol;
    }

    @Override
    public Position pos() {
        return this.pos;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfIncreased() {
        Set<SecurityPolicyVariable> s =
                new LinkedHashSet<SecurityPolicyVariable>();
        s.addAll(plaintextPol.variables());
        s.addAll(keyPol.variables());
        return s;
    }

    @Override
    public Set<SecurityPolicyVariable> invalidIfDecreased() {
        Set<SecurityPolicyVariable> s =
                new LinkedHashSet<SecurityPolicyVariable>();
        s.addAll(encResultPol.variables());
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
        return "EncryptionConstraint(key=" + keyPol + "; plaintext="
                + plaintextPol + "; result=" + encResultPol + ")";
    }
}
