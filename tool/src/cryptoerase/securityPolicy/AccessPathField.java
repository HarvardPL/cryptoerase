package cryptoerase.securityPolicy;

import java.util.Set;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.AbstractLocation;

/**
 * Represent a condition e.c, where c is a field of type Condition.
 */
public class AccessPathField extends AccessPath {
    private static final long serialVersionUID = SerialVersionUID.generate();

    /**
     * The set of abstract locations that this condition may refer to.
     */
    protected Set<AbstractLocation> absLocs;

    private String conditionString;

    public AccessPathField(Set<AbstractLocation> absLocs,
            String conditionString, Position pos) {
        super(pos);
        this.absLocs = absLocs;
        this.conditionString = conditionString;
    }

    @Override
    public String toString() {
        return conditionString;
    }

    /**
     * Might it be the case that these this condition refers to the
     * same condition as cond?
     */
    @Override
    public boolean mayOverlap(AccessPath cond) {
        if (cond instanceof AccessPathField) {
            AccessPathField other = (AccessPathField) cond;
            // see whether the abstract locations intersect.
            Set<AbstractLocation> a =
                    this.absLocs.size() < other.absLocs.size() ? this.absLocs
                            : other.absLocs;
            Set<AbstractLocation> b =
                    this.absLocs.size() < other.absLocs.size() ? other.absLocs
                            : this.absLocs;
            for (AbstractLocation al : a) {
                if (b.contains(al)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AccessPathField) {
            AccessPathField that = (AccessPathField) o;
            return this.absLocs.equals(that.absLocs);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return absLocs.hashCode();
    }

}
