package cryptflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.visit.FlowGraph;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.FlowGraph.ExceptionEdgeKey;
import polyglot.visit.FlowGraph.Peer;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.analysis.interprocvarcontext.VarContext;
import accrue.analysis.pointer.HContext;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.*;

// This is my extension to the existing dataflow 
public class CFDataFlow extends IFConsDataFlow {

	public CFDataFlow(IFConsAnalysisUtil autil, WorkQueue<Unit> wq, Job job,
			TypeSystem ts, NodeFactory nf) {
		super(autil, wq, job, ts, nf);
	}

	@Override
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowCall(Call n,
			VarContext<SecurityPolicy> dfIn_,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {

		System.err.println("-----Performing data flow for call " + n);

		// pulling out method information
		MethodInstance mi = n.methodInstance();
		System.err.println("  method name is " + mi.name());
		System.err.println("  defined in type " + mi.container());

		// This part is dealing with key generation
		// TODO: is this the right name?
		// TODO: change the return 
		Type keyGeneratorType;
		try {
			keyGeneratorType = typeSystem().typeForName("java.security.KeyPairGenerator");
		} catch (SemanticException e) {
			throw new InternalCompilerError("Missing java.security.KeyPairGenerator!!!", e);
		}
		if ("generateKeyPair".equals(mi.name()) && typeSystem().isSubtype(mi.container(),
						keyGeneratorType)) {
            // now make sure that the fields of the newly created object have the appropriate
            // security levels on them.
		    Set<HContext> pointsto = autil().pointsTo(n, autil().currentContext(), autil().extensionInfo());
            System.err.println("Generate key and points to is " + pointsto);
		    for (HContext obj : pointsto) {
		        // obj is an object that may be returned by generateKey.
                SecurityPolicyConstant privKey = new SecurityPolicyConstant((IFConsAnalysisFactory)this.autil().workQueue().factory(), CryptoSecurityPolicyFactory.PRIVKEY);
                SecurityPolicyConstant pubKey = new SecurityPolicyConstant((IFConsAnalysisFactory)this.autil().workQueue().factory(), CryptoSecurityPolicyFactory.PUBKEY);

                FieldInstance fi = obj.type().toReference().fieldNamed("privateKey");
                dfIn_ = autil().addLocations(dfIn_, autil().abstractLocations(obj, fi), privKey);                
                
                fi = obj.type().toReference().fieldNamed("publicKey");
                dfIn_ = autil().addLocations(dfIn_, autil().abstractLocations(obj, fi), pubKey);
		    }
		    // TODO: Add constraints to indicate that the return value of the function call
			// points to an object with a public and a private key.
			Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowCall(n, dfIn_, graph, peer);
			
			return ret;
		}

		// This part is dealing with encryption
		Type cryptoWrapperType;
		try {
			cryptoWrapperType = typeSystem().typeForName("cryptflow.AnnaCrypto");
		} catch (SemanticException e) {
			throw new InternalCompilerError("Missing AnnaCrypto!!!", e);
		}
		if ("cipherEncrypt".equals(mi.name()) && typeSystem().isSubtype(mi.container(),	cryptoWrapperType)){
			// pop off the arguments and receiver from the expression stack
			dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
			// push on the result of the function call, i.e., L
            SecurityPolicyConstant low = new SecurityPolicyConstant((IFConsAnalysisFactory)this.autil().workQueue().factory(), CryptoSecurityPolicyFactory.LOW);
			dfIn_ = dfIn_.pushExprResult(low, n);
			return itemToMapWithExceptionResults(dfIn_, peer.succEdgeKeys(), autil().bottomSecurityPolicy());
		}
		
		// This part is dealing with decryption 
		if ("cipherDecrypt".equals(mi.name()) && typeSystem().isSubtype(mi.container(), 
				cryptoWrapperType)) {
			List<SecurityPolicy> args = dfIn_.peekExprResults(3);
			dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
			dfIn_ = dfIn_.pushExprResult(args.get(2), n); // CHECK THIS IS THE RIGHT INDEX!!!
			return itemToMapWithExceptionResults(dfIn_, peer.succEdgeKeys(), autil().bottomSecurityPolicy());
		}
		
		// This is just the dummy catch all case
		return super.flowCall(n, dfIn_, graph, peer);
	}
	
    protected static Map<EdgeKey, VarContext<SecurityPolicy>> itemToMapWithExceptionResults(VarContext<SecurityPolicy> i, Set<EdgeKey> edgeKeys, SecurityPolicy bottom) {
        Map<EdgeKey, VarContext<SecurityPolicy>> m = new HashMap<EdgeKey, VarContext<SecurityPolicy>>();
        VarContext<SecurityPolicy> exceptional = null;
        for (EdgeKey ek : edgeKeys) {
            if (ek instanceof ExceptionEdgeKey) {
                if (exceptional == null) {
                    exceptional = i.clearExprResultStack().returnResult(bottom); 
                }
                m.put(ek, exceptional);
            }
            else {
                m.put(ek, i);
            }
        }
        return m;
    }

}
