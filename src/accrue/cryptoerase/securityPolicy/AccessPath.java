package accrue.cryptoerase.securityPolicy;

import java.io.Serializable;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

/**
 * 
 */
public abstract class AccessPath implements Serializable {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private Position position;

    protected AccessPath(Position pos) {
        this.position = pos;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public final Position position() {
        return position;
    }

    /**
     * Might this condition overlap with cond? That is, if cond is set,
     * is it possible that this condition will also be set?
     */
    abstract public boolean mayOverlap(AccessPath cond);

}
