package cryptoerase.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
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
import cryptoerase.ast.CEProcedureCallExt;
import cryptoerase.ast.CESecurityCast_c;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.AccessPathField;
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
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowNew(New n,
            VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        // get the set of access paths that may be set, and put in conditions for those.
        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        AccessPath setCondition =
                new AccessPathField(ext.possibleSetFieldInstances(autil().currentContext()),
                                    "[synthetic]",
                                    n.position());

        dfIn = requireNoConstraint((IFConsContext) dfIn, setCondition, peer);

        return super.flowNew(n, dfIn, graph, peer);
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowConstructorCall(
            ConstructorCall n, VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        // get the set of access paths that may be set, and put in conditions for those.
        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        AccessPath setCondition =
                new AccessPathField(ext.possibleSetFieldInstances(autil().currentContext()),
                                    "[synthetic]",
                                    n.position());

        dfIn = requireNoConstraint((IFConsContext) dfIn, setCondition, peer);
        return super.flowConstructorCall(n, dfIn, graph, peer);
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowCall(Call n,
            VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        // get the set of access paths that may be set, and put in conditions for those.
        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        AccessPath setCondition =
                new AccessPathField(ext.possibleSetFieldInstances(autil().currentContext()),
                                    "[synthetic]",
                                    n.position());

        dfIn = requireNoConstraint((IFConsContext) dfIn, setCondition, peer);

        if (isEncryptionCall(n)) {
            // it's an encryption! Add encryption constraints.
            // We'll assume, in a completely hacky way, that
            // the call takes two arguments, the first is the key 
            // and the second is the plaintext.
            List<SecurityPolicy> argPolicies = dfIn.peekExprResults(2);
            SecurityPolicy plaintextPol = argPolicies.get(0);
            SecurityPolicy keyPol = argPolicies.get(1);
            Map<EdgeKey, VarContext<SecurityPolicy>> res =
                    super.flowCall(n, dfIn, graph, peer);
            SecurityPolicy encResultPol =
                    res.get(FlowGraph.EDGE_KEY_OTHER).peekExprResult();
            addEncryptionConstraint(dfIn,
                                    (IFConsSecurityPolicy) keyPol,
                                    (IFConsSecurityPolicy) plaintextPol,
                                    (IFConsSecurityPolicy) encResultPol,
                                    n.position());
            return res;
        }

        if (isDecryptionCall(n)) {
            // it's a decryption! Add decryption constraints.
            // We'll assume, in a completely hacky way, that
            // the call takes two arguments, the first is the key 
            // and the second is the ciphertext.
            List<SecurityPolicy> argPolicies = dfIn.peekExprResults(2);
            SecurityPolicy ciphertextPol = argPolicies.get(0);
            SecurityPolicy keyPol = argPolicies.get(1);
            Map<EdgeKey, VarContext<SecurityPolicy>> res =
                    super.flowCall(n, dfIn, graph, peer);
            SecurityPolicy decResultPol =
                    res.get(FlowGraph.EDGE_KEY_OTHER).peekExprResult();
            addDecryptionConstraint(dfIn,
                                    (IFConsSecurityPolicy) keyPol,
                                    (IFConsSecurityPolicy) ciphertextPol,
                                    (IFConsSecurityPolicy) decResultPol,
                                    n.position());
            return res;
        }

        return super.flowCall(n, dfIn, graph, peer);
    }

    private void addEncryptionConstraint(VarContext<SecurityPolicy> preCall,
            IFConsSecurityPolicy keyPol, IFConsSecurityPolicy plaintextPol,
            IFConsSecurityPolicy encResultPol, Position pos) {
        // keyPol must be a PUBKEY(pk){p} with plaintextPol <= pk and p <= encResultPol 
        // (we assume that the constraint for the Call will take care of the pc requirement)
        factory.addConstraint(new EncryptionConstraint(keyPol,
                                                       plaintextPol,
                                                       encResultPol,
                                                       pos));

    }

    private void addDecryptionConstraint(VarContext<SecurityPolicy> dfIn,
            IFConsSecurityPolicy keyPol, IFConsSecurityPolicy ciphertextPol,
            IFConsSecurityPolicy decResultPol, Position pos) {
        // keyPol must be a PRIVKEY(pk){p} with pk  <= decResultPol and p <= decResultPol 
        // also need ciphertextPol <= decResultPol
        // (we assume that the constraint for the Call will take care of the pc requirement)
        factory.addConstraint(new DecryptionConstraint(keyPol,
                                                       decResultPol,
                                                       pos));

        // ciphertextPol <= decResultPol
        factory.addConstraint(ciphertextPol, decResultPol, pos);

    }

    private boolean isEncryptionCall(Call n) {
        return n.name().equals("encrypt")
                && n.target().type().toClass().name().equals("CryptoLibrary");
    }

    private boolean isDecryptionCall(Call n) {
        return n.name().equals("decrypt")
                && n.target().type().toClass().name().equals("CryptoLibrary");
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowLocalAssign(
            LocalAssign n, VarContext<SecurityPolicy> dfIn_,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        CEExt ext = CEExt_c.ext(n);
        if (ext.isConditionSet()) {
            // we're setting a local condition!
            AccessPath setCondition =
                    CESecurityPolicyFactory.singleton()
                                           .exprToAccessPath((Local) n.left());
            dfIn_ =
                    requireNoConstraint((IFConsContext) dfIn_,
                                        setCondition,
                                        peer);
        }
        return super.flowLocalAssign(n, dfIn_, graph, peer);
    }

    private IFConsContext requireNoConstraint(IFConsContext dfIn,
            AccessPath setCondition, Peer<VarContext<SecurityPolicy>> peer) {
        // make sure that the condition does not occur in the context at all.
        dfIn = copyAndConstrain(dfIn, peer, "set-local-condition");
        for (SecurityPolicy lp : dfIn.locals().values()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) lp;
            factory.addConstraint(new NoConditionConstraint(v,
                                                            setCondition,
                                                            peer.node()
                                                                .position()));
        }
        // and the PC
        for (SecurityPolicy p : dfIn.pcmap().getPolicies()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) p;
            factory.addConstraint(new NoConditionConstraint(v,
                                                            setCondition,
                                                            peer.node()
                                                                .position()));
        }

        // and the expression stack
        Stack<SecurityPolicy> es = dfIn.exprResultStack();
        while (es != null && !es.isEmpty()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) es.peek();
            es = es.pop();
            factory.addConstraint(new NoConditionConstraint(v,
                                                            setCondition,
                                                            peer.node()
                                                                .position()));
        }
        return dfIn;
    }

    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowFieldAssign(
            FieldAssign n, VarContext<SecurityPolicy> dfIn_,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        Map<EdgeKey, VarContext<SecurityPolicy>> ret =
                super.flowFieldAssign(n, dfIn_, graph, peer);
        return ret;
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
