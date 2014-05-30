package accrue.cryptoerase.securityPolicy;

import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import accrue.infoflow.analysis.SecurityPolicy;

public class KeyKind extends KindPolicy {
    private final FlowPolicy keyBound;
    private final boolean isPublicKey;

    public KeyKind(boolean isPublicKey, FlowPolicy keyBound) {
        this.isPublicKey = isPublicKey;
        this.keyBound = keyBound;
    }

    @Override
    public void prettyPrint(CodeWriter cw) {
        cw.write(isPublicKey ? "PUBKEY" : "PRIVKEY");
        cw.write("(");
        keyBound.prettyPrint(cw);
        cw.write(")");
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(isPublicKey ? "PUBKEY" : "PRIVKEY");
        sb.append("(");
        sb.append(keyBound);
        sb.append(")");
        return sb.toString();
    }

    public FlowPolicy keyBound() {
        return keyBound;
    }

    public boolean isPublicKey() {
        return isPublicKey;
    }

    @Override
    public boolean isBottom() {
        return false;
    }

    @Override
    public boolean leq(SecurityPolicy o) {
        if (o instanceof KindPolicy) {
            return this.equals(o) || o == KindPolicy.OTHER;
        }
        throw new InternalCompilerError("Bad: " + (o == null));
    }

    @Override
    public SecurityPolicy upperBound(SecurityPolicy o) {
        return null;
    }

    @Override
    public SecurityPolicy widen(SecurityPolicy o) {
        return this.upperBound(o);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isPublicKey ? 1231 : 1237);
        result =
                prime * result + ((keyBound == null) ? 0 : keyBound.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        KeyKind other = (KeyKind) obj;
        if (isPublicKey != other.isPublicKey) return false;
        if (keyBound == null) {
            if (other.keyBound != null) return false;
        }
        else if (!keyBound.equals(other.keyBound)) return false;
        return true;
    }

}
