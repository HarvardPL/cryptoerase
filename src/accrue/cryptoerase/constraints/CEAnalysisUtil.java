package accrue.cryptoerase.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Declaration;
import polyglot.types.Flags;
import polyglot.types.ProcedureInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import accrue.AccrueExtensionInfo;
import accrue.analysis.interprocanalysis.AnalysisContext;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.ExitMap;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.analysis.interprocanalysis.ExitMap.ExceptionKey;
import accrue.analysis.interprocvarcontext.FinalVarContext;
import accrue.analysis.interprocvarcontext.VarContext;
import accrue.analysis.pointer.HContext;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.ast.PolicyNode;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.infoflow.analysis.AbstractInfoFlowContext;
import accrue.infoflow.analysis.InfoFlowFactoryHelper;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsContext;
import accrue.infoflow.analysis.dataflow.InfoFlowLibrarySignature;

public class CEAnalysisUtil extends IFConsAnalysisUtil {

	public CEAnalysisUtil(WorkQueue<Unit> workQueue,
			AnalysisUnit currentProcCall, AccrueExtensionInfo extInfo) {
		super(workQueue, currentProcCall, extInfo);
	}

	@Override
	protected CEDataFlow createDataflow() {
		return new CEDataFlow(this, this.workQueue(), this.extInfo.scheduler()
				.currentJob(), this.extInfo.typeSystem(),
				this.extInfo.nodeFactory());
	}

	public CESecurityPolicy convert(PolicyNode policyNode) {
		InfoFlowFactoryHelper<SecurityPolicy, Unit> facHelper = (InfoFlowFactoryHelper<SecurityPolicy, Unit>) workQueue
				.factory();
		CESecurityPolicyFactory<Unit> secPolFac = (CESecurityPolicyFactory<Unit>) facHelper
				.securityPolicyFactory();
		return policyNode.policy(secPolFac);
	}

	public SecurityPolicy copyAndConstrainPC(IFConsContext context, Node node) {
		IFConsAnalysisFactory af = (IFConsAnalysisFactory) workQueue()
				.factory();
		return copyAndConstrain(af.pcmapToSecurityPolicy(context),
				"pc-" + node, node);
	}

	@Override
	protected FinalVarContext<SecurityPolicy> guessAnalysisForMissingCode(
			ProcedureInstance pi, AnalysisContext calleeContext,
			HContext receiver, VarContext<SecurityPolicy> input,
			SecurityPolicy receiverAbsVal, List<SecurityPolicy> argAbsVals,
			Set<ExitMap.Key> expectedExits, Node node) {
		return JoinAllInputs.singleton.process(pi, calleeContext, receiver,
				input, receiverAbsVal, argAbsVals, this, expectedExits, node);
	}

}
