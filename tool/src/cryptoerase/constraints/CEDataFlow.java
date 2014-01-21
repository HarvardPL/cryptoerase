package cryptoerase.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.FlowGraph;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.FlowGraph.ExceptionEdgeKey;
import polyglot.visit.FlowGraph.Peer;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.analysis.interprocvarcontext.VarContext;
import accrue.analysis.pointer.HContext;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsDataFlow;
import accrue.infoflow.analysis.constraints.SecurityPolicyConstant;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import accrue.infoflow.ast.SecurityCast;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.ast.CryptoEraseSecurityCast_c;

// This is my extension to the existing dataflow 
public class CEDataFlow extends IFConsDataFlow {

    public CEDataFlow(IFConsAnalysisUtil autil, WorkQueue<Unit> wq, Job job,
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
            keyGeneratorType =
                    typeSystem().typeForName("java.security.KeyPairGenerator");
        }
        catch (SemanticException e) {
            throw new InternalCompilerError("Missing java.security.KeyPairGenerator!!!",
                                            e);
        }
        if ("generateKeyPair".equals(mi.name())
                && typeSystem().isSubtype(mi.container(), keyGeneratorType)) {
            ;
            autil();
            // now make sure that the fields of the newly created object have the appropriate
            // security levels on them.
            Set<HContext> pointsto =
                    AnalysisUtil.pointsTo(n,
                                          autil().currentContext(),
                                          autil().extensionInfo());
            System.err.println("Generate key and points to is " + pointsto);
            for (HContext obj : pointsto) {
                // obj is an object that may be returned by generateKey.
                SecurityPolicyConstant privKey =
                        new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
                                                                               .workQueue()
                                                                               .factory(),
                                                   CESecurityPolicyFactory.PRIVKEY);
                SecurityPolicyConstant pubKey =
                        new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
                                                                               .workQueue()
                                                                               .factory(),
                                                   CESecurityPolicyFactory.PUBKEY);

                FieldInstance fi =
                        obj.type().toReference().fieldNamed("privateKey");
                dfIn_ =
                        autil().addLocations(dfIn_,
                                             autil().abstractLocations(obj, fi),
                                             privKey);

                fi = obj.type().toReference().fieldNamed("publicKey");
                dfIn_ =
                        autil().addLocations(dfIn_,
                                             autil().abstractLocations(obj, fi),
                                             pubKey);
            }
            // TODO: Add constraints to indicate that the return value of the function call
            // points to an object with a public and a private key.
            Map<EdgeKey, VarContext<SecurityPolicy>> ret =
                    super.flowCall(n, dfIn_, graph, peer);

            return ret;
        }

        // This part is dealing with encryption
        Type cryptoWrapperType;
        try {
            cryptoWrapperType =
                    typeSystem().typeForName("cryptflow.AnnaCrypto");
        }
        catch (SemanticException e) {
            throw new InternalCompilerError("Missing AnnaCrypto!!!", e);
        }
        if ("cipherEncrypt".equals(mi.name())
                && typeSystem().isSubtype(mi.container(), cryptoWrapperType)) {
            // pop off the arguments and receiver from the expression stack
            dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
            // push on the result of the function call, i.e., L
            SecurityPolicyConstant low =
                    new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
                                                                           .workQueue()
                                                                           .factory(),
                                               CESecurityPolicyFactory.LOW);
            dfIn_ = dfIn_.pushExprResult(low, n);
            return itemToMapWithExceptionResults(dfIn_,
                                                 peer.succEdgeKeys(),
                                                 autil().bottomSecurityPolicy());
        }

        // This part is dealing with decryption 
        if ("cipherDecrypt".equals(mi.name())
                && typeSystem().isSubtype(mi.container(), cryptoWrapperType)) {
            List<SecurityPolicy> args = dfIn_.peekExprResults(3);
            dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
            dfIn_ = dfIn_.pushExprResult(args.get(2), n); // CHECK THIS IS THE RIGHT INDEX!!!
            return itemToMapWithExceptionResults(dfIn_,
                                                 peer.succEdgeKeys(),
                                                 autil().bottomSecurityPolicy());
        }

        // This is just the dummy catch all case
        return super.flowCall(n, dfIn_, graph, peer);
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowSecurityCast(
            SecurityCast n, VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        CryptoEraseSecurityCast_c cast_c = (CryptoEraseSecurityCast_c) n;

        SecurityPolicy e =
                ((CEAnalysisUtil) autil()).convert(cast_c.policyNode());
        IFConsAnalysisFactory factory =
                (IFConsAnalysisFactory) autil().workQueue().factory();

        // create new variable, and constrain it appropriately
        SecurityPolicyVariable var = getMemoizedVariable(peer, "security-cast");
        factory.addConstraint(ConstraintKind.SOURCE, e, var, peer.node()
                                                                 .position());

        dfIn = dfIn.popAndPushExprResults(1, var, n);

        return mapForItemWithError(dfIn, peer);
    }

    protected static Map<EdgeKey, VarContext<SecurityPolicy>> itemToMapWithExceptionResults(
            VarContext<SecurityPolicy> i, Set<EdgeKey> edgeKeys,
            SecurityPolicy bottom) {
        Map<EdgeKey, VarContext<SecurityPolicy>> m =
                new HashMap<EdgeKey, VarContext<SecurityPolicy>>();
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
