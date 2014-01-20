package cryptoerase.securityPolicy;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

/**
 * Represents a final access path root.
 * 
 * @see jif.types.label.AccessPath
 */
public abstract class AccessPathRoot extends AccessPath {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected AccessPathRoot(Position pos) {
        super(pos);
    }

    @Override
    public boolean isUninterpreted() {
        return false;
    }

    @Override
    public final AccessPathRoot root() {
        return this;
    }

}
