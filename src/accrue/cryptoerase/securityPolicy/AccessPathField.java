package accrue.cryptoerase.securityPolicy;

import java.util.Collections;
import java.util.Set;

import polyglot.types.FieldInstance;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

/**
 * Represent a condition e.c, where c is a field of type Condition.
 */
public class AccessPathField extends AccessPath {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    /**
//     * The set of abstract locations that this condition may refer to.
//     */
//    protected Set<AbstractLocation> absLocs;

    /**
     * The field instance. We will use this as a proxy for the abstract locations to which this
     * condition may refer, since we don't want different policies for each field replica (i.e., field instance and abstract object).
     */
    protected Set<FieldInstance> fis;

    private String conditionString;

    public AccessPathField(FieldInstance fi, String conditionString,
            Position pos) {
        this(Collections.singleton(fi), conditionString, pos);
    }

    public AccessPathField(Set<FieldInstance> fis, String conditionString,
            Position pos) {
        super(pos);
        this.fis = fis;
        this.conditionString = conditionString;
    }

    @Override
    public String toString() {
        return conditionString;
    }
    
    public Set<FieldInstance> fieldInstance() {
    	return fis;
    }

    /**
     * Might it be the case that these this condition refers to the
     * same condition as cond?
     */
    @Override
    public boolean mayOverlap(AccessPath cond) {
        if (cond instanceof AccessPathField) {
            AccessPathField other = (AccessPathField) cond;
            // see whether the field instances overlap
            Set<FieldInstance> a =
                    this.fis.size() < other.fis.size() ? this.fis : other.fis;
            Set<FieldInstance> b =
                    this.fis.size() < other.fis.size() ? other.fis : this.fis;
            for (FieldInstance fi : a) {
                if (b.contains(fi)) {
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
            return this.fis.equals(that.fis);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return fis.hashCode();
    }

}
