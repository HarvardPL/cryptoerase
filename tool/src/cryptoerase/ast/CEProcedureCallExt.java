package cryptoerase.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.AbstractLocation;
import accrue.analysis.interprocanalysis.AnalysisContext;

public class CEProcedureCallExt extends CEExt_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    Map<AnalysisContext, Set<AbstractLocation>> possiblySetFieldConditions =
            new HashMap<AnalysisContext, Set<AbstractLocation>>();

    public Set<AbstractLocation> possibleSetFieldConditions(AnalysisContext c) {
        Set<AbstractLocation> s = possiblySetFieldConditions.get(c);
        if (s == null) {
            s = Collections.emptySet();
        }
        return s;
    }

    public void recordSetConditionsResults(AnalysisContext c,
            Set<AbstractLocation> setConds) {
        this.possiblySetFieldConditions.put(c, setConds);

    }
}
