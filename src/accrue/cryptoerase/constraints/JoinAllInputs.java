package accrue.cryptoerase.constraints;

import java.util.List;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import accrue.analysis.interprocanalysis.AnalysisContext;
import accrue.analysis.interprocanalysis.ExitMap;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.analysis.interprocanalysis.ExitMap.Key;
import accrue.analysis.interprocvarcontext.AnalysisUtilVarContext;
import accrue.analysis.interprocvarcontext.FinalVarContext;
import accrue.analysis.interprocvarcontext.VarContext;
import accrue.analysis.pointer.HContext;
import accrue.infoflow.analysis.AbstractInfoFlowAnalysisUtil;
import accrue.infoflow.analysis.AbstractInfoFlowContext;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.dataflow.InfoFlowLibrarySignature;

public class JoinAllInputs<A extends Ordered<A>> extends InfoFlowLibrarySignature<A> {
	public static final JoinAllInputs singleton = new JoinAllInputs();
	
	@Override
	public FinalVarContext<SecurityPolicy> process(ProcedureInstance pi, AnalysisContext calleeContext, HContext receiver,
                                            VarContext<SecurityPolicy> input, SecurityPolicy receiverDFI, List<SecurityPolicy> argDFIs, AnalysisUtilVarContext<A,SecurityPolicy> autil, Set<ExitMap.Key> expectedExits, Node node) {
		AbstractInfoFlowAnalysisUtil ifautil = (AbstractInfoFlowAnalysisUtil)autil;
		
		SecurityPolicy p = receiverDFI;
		// Fold in the arguments
        for (SecurityPolicy a : argDFIs) {
            p = p.upperBound(a);
        }
        
        // Fold in the pc
        p = ifautil.assignment(p, (AbstractInfoFlowContext)input, node);
        
        if (receiver != null) {
        	// XXX dragons
        	for (FieldInstance fi : receiver.type().toReference().fields()) {
        		if (!fi.flags().isStatic()) {
        			p = p.upperBound((SecurityPolicy) ifautil.getLocationAbsVal(input, ifautil.abstractLocations(receiver, fi)));
        		}
        	}
        }
        
        VarContext<SecurityPolicy> output = input.pushExprResult(p, "join-all-inputs for " + pi);
        
        if (receiver != null) {
        	for (FieldInstance fi : receiver.type().toReference().fields()) {
        		if (!fi.flags().isStatic()) {
        			output = ifautil.addLocations(output, ifautil.abstractLocations(receiver, fi), p);
        		}
        	}
        }
        
        VarContext<SecurityPolicy> exceptional = output.returnResult(p).clearExprResultStack();
        FinalVarContext ret = (FinalVarContext) ifautil.createFinalVarContext(output).removeNormalTermination();
        for (Key k : expectedExits) {
            if (ExitMap.KEY_NORM_TERM.equals(k)) {
                ret = (FinalVarContext) ret.normalTermination(output);
            }
            else if (k instanceof ExitMap.ExceptionKey) {
               // ret = (FinalVarContext) ret.addKey(k, exceptional);
            }
            else {
                throw new InternalCompilerError("Was not expecting expected exit.  " +  k + " Need to work on this signature. " + pi);
            }
        }
        
        return ret;
    }
	
}
