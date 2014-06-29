package accrue.cryptoerase.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ProcedureCall;
import polyglot.types.FieldInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.AbstractLocation;
import accrue.analysis.interprocanalysis.AnalysisContext;
import accrue.cryptoerase.types.CETypeSystem;

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

    public Set<FieldInstance> possibleSetFieldInstances(AnalysisContext c) {
        Set<AbstractLocation> setLocs = this.possibleSetFieldConditions(c);
        Set<FieldInstance> setFieldInstances =
                new LinkedHashSet<FieldInstance>();
        for (AbstractLocation loc : setLocs) {
            if (loc.fi == null) {
                throw new InternalCompilerError("Need field instance!");
            }
            setFieldInstances.add(loc.fi);

        }
        return setFieldInstances;
    }

    public void recordSetConditionsResults(AnalysisContext c,
            Set<AbstractLocation> setConds) {
        this.possiblySetFieldConditions.put(c, setConds);
    }
}
