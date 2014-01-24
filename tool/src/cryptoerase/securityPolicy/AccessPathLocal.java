package cryptoerase.securityPolicy;

import polyglot.types.LocalInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import cryptoerase.types.CETypeSystem;

/**
 * Represents a condition for erasure that is a local variable
 */
public class AccessPathLocal extends AccessPath {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected LocalInstance li;

    public AccessPathLocal(LocalInstance li, Position pos) {
        super(pos);
        this.li = li;
        CETypeSystem ts = (CETypeSystem) li.typeSystem();
        if (!li.type().equals(ts.Condition())) {
            throw new InternalCompilerError("Not a condition!");
        }
    }

    @Override
    public String toString() {
        return li.name();
    }

    public LocalInstance localInstance() {
        return this.li;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AccessPathLocal) {
            AccessPathLocal that = (AccessPathLocal) o;
            // use pointer equality for li instead of equals, so
            // that we don't mistakenly equate two local instances
            // with the same name but from different methods/defining
            // scopes
            return this.li == that.li;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return li.hashCode();
    }

    @Override
    public boolean mayOverlap(AccessPath cond) {
        // A local condition can only overlap with another local condition
        return this.equals(cond);
    }

}
