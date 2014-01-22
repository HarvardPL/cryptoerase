package cryptoerase.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.LocalDecl;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.FlowGraph;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.FlowGraph.ExceptionEdgeKey;
import polyglot.visit.FlowGraph.Peer;
import accrue.analysis.interprocanalysis.AbstractLocation;
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
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;
import accrue.infoflow.analysis.constraints.SecurityPolicyConstant;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import accrue.infoflow.ast.SecurityCast;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.ast.CEExt_c;
import cryptoerase.ast.CELocalDeclExt;
import cryptoerase.ast.CESecurityCast_c;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.types.CEFieldInstance;

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
        CESecurityCast_c cast_c = (CESecurityCast_c) n;

        SecurityPolicy e =
                ((CEAnalysisUtil) autil()).convert(cast_c.policyNode());
        IFConsAnalysisFactory factory =
                (IFConsAnalysisFactory) autil().workQueue().factory();

        // create new variable, and constrain it appropriately
        SecurityPolicyVariable var = getMemoizedVariable(peer, "security-cast");
        factory.addConstraint(ConstraintKind.SOURCE,
                              IFConsSecurityPolicy.constant(e, factory),
                              var,
                              peer.node().position());

        dfIn = dfIn.popAndPushExprResults(1, var, n);

        return mapForItemWithError(dfIn, peer);
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowLocalDecl(LocalDecl n,
            VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        Map<EdgeKey, VarContext<SecurityPolicy>> ret =
                super.flowLocalDecl(n, dfIn, graph, peer);
        CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(n);
        if (ext.label() != null) {
            IFConsAnalysisFactory factory =
                    (IFConsAnalysisFactory) autil().workQueue().factory();

            VarContext<SecurityPolicy> df = ret.values().iterator().next();

            CEAnalysisUtil ceautil = (CEAnalysisUtil) this.autil();
            CESecurityPolicy declPolicy = ceautil.convert(ext.label());

            SecurityPolicy localDeclVar =
                    df.getLocalAbsVal(n.name(), n.type().type());

            // add an equality constraint by adding two constraints...
            factory.addConstraint(IFConsSecurityPolicy.constant(declPolicy,
                                                                factory),
                                  localDeclVar,
                                  peer.node().position());

            factory.addConstraint(localDeclVar,
                                  IFConsSecurityPolicy.constant(declPolicy,
                                                                factory),
                                  peer.node().position());

        }

        return ret;
    }

    @Override
    protected SecurityPolicy loadField(VarContext<SecurityPolicy> dfIn, Field n) {
        CEFieldInstance fi = (CEFieldInstance) n.fieldInstance();
        if (fi.declaredPolicy() != null) {
            IFConsAnalysisFactory factory =
                    (IFConsAnalysisFactory) autil().workQueue().factory();

            return IFConsSecurityPolicy.constant(fi.declaredPolicy(), factory);
        }
        return autil().getLocationAbsVal(dfIn, autil().abstractLocations(n));
    }

    @Override
    protected VarContext<SecurityPolicy> storeField(
            VarContext<SecurityPolicy> df, FieldInstance fi,
            Set<AbstractLocation> locs, SecurityPolicy pol, Position pos) {
        CEFieldInstance cefi = (CEFieldInstance) fi;
        if (cefi.declaredPolicy() != null) {
            // just record a constraint
            IFConsAnalysisFactory factory =
                    (IFConsAnalysisFactory) autil().workQueue().factory();
            factory.addConstraint(pol,
                                  IFConsSecurityPolicy.constant(cefi.declaredPolicy(),
                                                                factory),
                                  pos);

            return df;
        }
        return autil().addLocations(df, locs, pol);
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
