package cryptoerase.possibleSetConditions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import accrue.analysis.interprocanalysis.AbstractLocation;
import accrue.analysis.interprocanalysis.Ordered;

public class SetConditionsAbsVal implements Ordered<SetConditionsAbsVal> {
    private final Set<AbstractLocation> setConditions;
    public static final SetConditionsAbsVal EMPTY =
            new SetConditionsAbsVal(Collections.EMPTY_SET);

    public SetConditionsAbsVal(Set<AbstractLocation> setConditions) {
        this.setConditions = setConditions;
    }

    @Override
    public boolean leq(SetConditionsAbsVal o) {
        return o.setConditions().containsAll(this.setConditions());
    }

    @Override
    public SetConditionsAbsVal upperBound(SetConditionsAbsVal o) {
        if (o == null) return this;
        Set<AbstractLocation> sc =
                new HashSet<AbstractLocation>(this.setConditions());
        sc.addAll(o.setConditions());
        return new SetConditionsAbsVal(sc);
    }

    @Override
    public String toString() {
        return setConditions.toString();
    }

    public Set<AbstractLocation> setConditions() {
        return setConditions;
    }

    @Override
    public int hashCode() {
        return this.setConditions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SetConditionsAbsVal)) return false;
        SetConditionsAbsVal other = (SetConditionsAbsVal) obj;
        if (setConditions == null) {
            if (other.setConditions != null) return false;
        }
        else if (!setConditions.equals(other.setConditions)) return false;
        return true;
    }

    @Override
    public SetConditionsAbsVal widen(SetConditionsAbsVal o) {
        return upperBound(o);
    }

}
