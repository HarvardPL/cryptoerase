package cryptoerase.constraints;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.FlowGraph;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.FlowGraph.ExceptionEdgeKey;
import polyglot.visit.FlowGraph.Peer;
import accrue.analysis.interprocanalysis.AbstractLocation;
import accrue.analysis.interprocanalysis.Unit;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.analysis.interprocvarcontext.Stack;
import accrue.analysis.interprocvarcontext.VarContext;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsContext;
import accrue.infoflow.analysis.constraints.IFConsDataFlow;
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;
import accrue.infoflow.analysis.constraints.SecurityPolicyConstant;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import accrue.infoflow.ast.SecurityCast;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.ast.CEExt;
import cryptoerase.ast.CEExt_c;
import cryptoerase.ast.CELocalDeclExt;
import cryptoerase.ast.CESecurityCast_c;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.types.CEFieldInstance;

// This is my extension to the existing dataflow 
public class CEDataFlow extends IFConsDataFlow {

    public CEDataFlow(IFConsAnalysisUtil autil, WorkQueue<Unit> wq, Job job,
            TypeSystem ts, NodeFactory nf) {
        super(autil, wq, job, ts, nf);
        this.factory =
                (CEConstraintsAnalysisFactory) autil().workQueue().factory();
    }

    CEConstraintsAnalysisFactory factory;

//    @Override
//    public Map<EdgeKey, VarContext<SecurityPolicy>> flowCall(Call n,
//            VarContext<SecurityPolicy> dfIn_,
//            FlowGraph<VarContext<SecurityPolicy>> graph,
//            Peer<VarContext<SecurityPolicy>> peer) {
//
//        System.err.println("-----Performing data flow for call " + n);
//
//        // pulling out method information
//        MethodInstance mi = n.methodInstance();
//        System.err.println("  method name is " + mi.name());
//        System.err.println("  defined in type " + mi.container());
//
//        // This part is dealing with key generation
//        // TODO: is this the right name?
//        // TODO: change the return 
//        Type keyGeneratorType;
//        try {
//            keyGeneratorType =
//                    typeSystem().typeForName("java.security.KeyPairGenerator");
//        }
//        catch (SemanticException e) {
//            throw new InternalCompilerError("Missing java.security.KeyPairGenerator!!!",
//                                            e);
//        }
//        if ("generateKeyPair".equals(mi.name())
//                && typeSystem().isSubtype(mi.container(), keyGeneratorType)) {
//            // now make sure that the fields of the newly created object have the appropriate
//            // security levels on them.
//            Set<HContext> pointsto =
//                    AnalysisUtil.pointsTo(n,
//                                          autil().currentContext(),
//                                          autil().extensionInfo());
//            System.err.println("Generate key and points to is " + pointsto);
//            for (HContext obj : pointsto) {
//                // obj is an object that may be returned by generateKey.
//                SecurityPolicyConstant privKey =
//                        new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
//                                                                               .workQueue()
//                                                                               .factory(),
//                                                   CESecurityPolicyFactory.PRIVKEY);
//                SecurityPolicyConstant pubKey =
//                        new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
//                                                                               .workQueue()
//                                                                               .factory(),
//                                                   CESecurityPolicyFactory.PUBKEY);
//
//                FieldInstance fi =
//                        obj.type().toReference().fieldNamed("privateKey");
//                dfIn_ =
//                        autil().addLocations(dfIn_,
//                                             autil().abstractLocations(obj, fi),
//                                             privKey);
//
//                fi = obj.type().toReference().fieldNamed("publicKey");
//                dfIn_ =
//                        autil().addLocations(dfIn_,
//                                             autil().abstractLocations(obj, fi),
//                                             pubKey);
//            }
//            // TODO: Add constraints to indicate that the return value of the function call
//            // points to an object with a public and a private key.
//            Map<EdgeKey, VarContext<SecurityPolicy>> ret =
//                    super.flowCall(n, dfIn_, graph, peer);
//
//            return ret;
//        }
//
//        // This part is dealing with encryption
//        Type cryptoWrapperType;
//        try {
//            cryptoWrapperType =
//                    typeSystem().typeForName("cryptflow.AnnaCrypto");
//        }
//        catch (SemanticException e) {
//            throw new InternalCompilerError("Missing AnnaCrypto!!!", e);
//        }
//        if ("cipherEncrypt".equals(mi.name())
//                && typeSystem().isSubtype(mi.container(), cryptoWrapperType)) {
//            // pop off the arguments and receiver from the expression stack
//            dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
//            // push on the result of the function call, i.e., L
//            SecurityPolicyConstant low =
//                    new SecurityPolicyConstant((IFConsAnalysisFactory) this.autil()
//                                                                           .workQueue()
//                                                                           .factory(),
//                                               CESecurityPolicyFactory.LOW);
//            dfIn_ = dfIn_.pushExprResult(low, n);
//            return itemToMapWithExceptionResults(dfIn_,
//                                                 peer.succEdgeKeys(),
//                                                 autil().bottomSecurityPolicy());
//        }
//
//        // This part is dealing with decryption 
//        if ("cipherDecrypt".equals(mi.name())
//                && typeSystem().isSubtype(mi.container(), cryptoWrapperType)) {
//            List<SecurityPolicy> args = dfIn_.peekExprResults(3);
//            dfIn_ = dfIn_.popExprResults(mi.formalTypes().size() + 1);
//            dfIn_ = dfIn_.pushExprResult(args.get(2), n); // CHECK THIS IS THE RIGHT INDEX!!!
//            return itemToMapWithExceptionResults(dfIn_,
//                                                 peer.succEdgeKeys(),
//                                                 autil().bottomSecurityPolicy());
//        }
//
//        // This is just the dummy catch all case
//        return super.flowCall(n, dfIn_, graph, peer);
//    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowSecurityCast(
            SecurityCast n, VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        CESecurityCast_c cast_c = (CESecurityCast_c) n;

        SecurityPolicy e =
                ((CEAnalysisUtil) autil()).convert(cast_c.policyNode());

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
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowFieldDecl(FieldDecl n,
            VarContext<SecurityPolicy> dfIn_,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        Map<EdgeKey, VarContext<SecurityPolicy>> ret =
                super.flowFieldDecl(n, dfIn_, graph, peer);

        SecurityPolicy pol =
                fieldInstancePolicy((CEFieldInstance) n.fieldInstance());

        // add a constraint to require a field decl to not have any erasure policies
        // (unless the field name ends in TESTOUTPUT, which is a hack to let us see
        // the result of the solution easily.) 
        if (!n.fieldInstance().name().endsWith("TESTOUTPUT")) {
            if (pol instanceof SecurityPolicyVariable) {
                factory.addConstraint(new NoConditionConstraint((SecurityPolicyVariable) pol,
                                                                n.position()));
            }
            else {
                factory.addConstraint(new NoConditionConstraint((CESecurityPolicy) ((SecurityPolicyConstant) pol).constant(),
                                                                n.position()));

            }
        }

        return ret;
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
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowLocalAssign(
            LocalAssign n, VarContext<SecurityPolicy> dfIn_,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        CEExt ext = CEExt_c.ext(n);
        if (ext.isConditionSet()) {
            // we're setting a local condition!
            // make sure that the local condition does not occur in the context at all.
            dfIn_ =
                    copyAndConstrain((IFConsContext) dfIn_,
                                     peer,
                                     "set-local-condition");
            AccessPath condition =
                    CESecurityPolicyFactory.singleton()
                                           .exprToAccessPath(n.left());
            for (SecurityPolicy lp : dfIn_.locals().values()) {
                SecurityPolicyVariable v = (SecurityPolicyVariable) lp;
                factory.addConstraint(new NoConditionConstraint(v,
                                                                condition,
                                                                n.position()));
            }
            // and the PC
            IFConsContext dfIn = (IFConsContext) dfIn_;
            for (SecurityPolicy p : dfIn.pcmap().getPolicies()) {
                SecurityPolicyVariable v = (SecurityPolicyVariable) p;
                factory.addConstraint(new NoConditionConstraint(v,
                                                                condition,
                                                                n.position()));
            }

            // and the expression stack
            Stack<SecurityPolicy> es = dfIn.exprResultStack();
            while (es != null && !es.isEmpty()) {
                SecurityPolicyVariable v = (SecurityPolicyVariable) es.peek();
                es = es.pop();
                factory.addConstraint(new NoConditionConstraint(v,
                                                                condition,
                                                                n.position()));
            }

        }
        return super.flowLocalAssign(n, dfIn_, graph, peer);
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowFieldAssign(
            FieldAssign n, VarContext<SecurityPolicy> dfIn_,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        Map<EdgeKey, VarContext<SecurityPolicy>> ret =
                super.flowFieldAssign(n, dfIn_, graph, peer);
        return ret;
        //!@!
    }

    @Override
    protected SecurityPolicy loadField(VarContext<SecurityPolicy> dfIn, Field n) {
        CEFieldInstance fi = (CEFieldInstance) n.fieldInstance();
        return fieldInstancePolicy(fi);
    }

    protected SecurityPolicy fieldInstancePolicy(CEFieldInstance fi) {
        if (fi.declaredPolicy() != null) {
            return IFConsSecurityPolicy.constant(fi.declaredPolicy(), factory);
        }
        else {
            // no declared policy, use a variable for the field instance
            SecurityPolicyVariable fieldVar = factory.getFieldInstanceVar(fi);
            return fieldVar;
        }
    }

    @Override
    protected VarContext<SecurityPolicy> storeField(
            VarContext<SecurityPolicy> df, FieldInstance fi,
            Set<AbstractLocation> locs, SecurityPolicy pol, Position pos) {
        CEFieldInstance cefi = (CEFieldInstance) fi;

        factory.addConstraint(pol, fieldInstancePolicy(cefi), pos);

        return df;
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
