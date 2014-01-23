package cryptoerase.constraints;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import polyglot.types.FieldInstance;
import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import accrue.infoflow.analysis.constraints.ConstraintSet;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;

public class CEConstraintsAnalysisFactory extends IFConsAnalysisFactory {

    public CEConstraintsAnalysisFactory(AccrueExtensionInfo extInfo,
            SecurityPolicyFactory<Unit> securityPolicyFactory) {
        super(extInfo, securityPolicyFactory);
    }

    @Override
    public AnalysisUtil<Unit> analysisUtil(WorkQueue<Unit> wq, AnalysisUnit pc) {
        return new CEAnalysisUtil(wq, pc, extInfo);
    }

    /**
     * Variables to infer policies for field instances.
     */
    private Map<FieldInstance, SecurityPolicyVariable> fieldInstanceVars =
            new HashMap<FieldInstance, SecurityPolicyVariable>();

    protected SecurityPolicyVariable getFieldInstanceVar(FieldInstance fi) {
        if (fieldInstanceVars.containsKey(fi)) {
            return fieldInstanceVars.get(fi);
        }
        SecurityPolicyVariable v =
                this.createVariable(fi.container() + "." + fi.name(), fi.name());
        fieldInstanceVars.put(fi, v);
        return v;
    }

    protected Set<FieldInstance> allFieldInstancesWithVars() {
        return fieldInstanceVars.keySet();
    }

    @Override
    protected ConstraintSet createConstraintSet() {
        return new CEConstraintSet(this);
    }

}
