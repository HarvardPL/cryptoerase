package accrue.cryptoerase.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.ArrayInit;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
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
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.ast.CEExt;
import accrue.cryptoerase.ast.CEExt_c;
import accrue.cryptoerase.ast.CELocalDeclExt;
import accrue.cryptoerase.ast.CEProcedureCallExt;
import accrue.cryptoerase.ast.CESecurityCast;
import accrue.cryptoerase.securityPolicy.AccessPath;
import accrue.cryptoerase.securityPolicy.AccessPathField;
import accrue.cryptoerase.securityPolicy.AccessPathLocal;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.cryptoerase.securityPolicy.ErasurePolicy;
import accrue.cryptoerase.securityPolicy.FlowPolicy;
import accrue.cryptoerase.types.CEFieldInstance;
import accrue.cryptoerase.types.CETypeSystem;
import accrue.infoflow.analysis.AbstractInfoFlowContext;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.EdgeKeyedPCMap;
import accrue.infoflow.analysis.constraints.IFConsAnalysisUtil;
import accrue.infoflow.analysis.constraints.IFConsContext;
import accrue.infoflow.analysis.constraints.IFConsDataFlow;
import accrue.infoflow.analysis.constraints.IFConsSecurityPolicy;
import accrue.infoflow.analysis.constraints.SecurityPolicyConstant;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import accrue.infoflow.ast.SecurityCast;

// This is my extension to the existing dataflow 
public class CEDataFlow extends IFConsDataFlow {

	public CEDataFlow(IFConsAnalysisUtil autil, WorkQueue<Unit> wq, Job job,
            TypeSystem ts, NodeFactory nf) {
        super(autil, wq, job, ts, nf);
        this.factory =
                (CEConstraintsAnalysisFactory) autil().workQueue().factory();
    }

    CEConstraintsAnalysisFactory factory;
    
    @Override
    public Map<EdgeKey, VarContext<SecurityPolicy>> flowSecurityCast(
            SecurityCast n, VarContext<SecurityPolicy> dfIn,
            FlowGraph<VarContext<SecurityPolicy>> graph,
            Peer<VarContext<SecurityPolicy>> peer) {
        CESecurityCast cast_c = (CESecurityCast) n;

        SecurityPolicy e =
                ((CEAnalysisUtil) autil()).convert(cast_c.policyNode());

        // create new variable, and constrain it appropriately
        SecurityPolicyVariable var = getMemoizedVariable(peer, "security-cast");
        factory.addConstraint(ConstraintKind.SOURCE,
                              IFConsSecurityPolicy.constant(e, factory),
                              var,
                              peer.node().position());
        
		// Make sure that the policy is well formed
		if (e instanceof CESecurityPolicy) {
			CESecurityPolicy ce = (CESecurityPolicy) e;
			for (AccessPath condition : ce.flowPol().conditions()) {
				SecurityPolicy condPol = conditionPolicy((IFConsContext) dfIn, condition);
				factory.addConstraint(new WellFormedConstraint(ce, condition, condPol, n.position()));
			}
		}

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
        
        VarContext<SecurityPolicy> df = ret.values().iterator().next();
        SecurityPolicy localDeclVar =
                df.getLocalAbsVal(n.name(), n.type().type());
        factory.addConstraint(factory.pcmapToSecurityPolicy((IFConsContext) df), localDeclVar, n.position());
        
        if (ext.label() != null) {
            CEAnalysisUtil ceautil = (CEAnalysisUtil) this.autil();
            CESecurityPolicy declPolicy = ceautil.convert(ext.label());

            // add an equality constraint by adding two constraints...
            factory.addConstraint(IFConsSecurityPolicy.constant(declPolicy,
                                                                factory),
                                  localDeclVar,
                                  peer.node().position());

            factory.addConstraint(localDeclVar,
                                  IFConsSecurityPolicy.constant(declPolicy,
                                                                factory),
                                  peer.node().position());
            
            factory.addConstraint(new NoTopConstraint(declPolicy, peer.node().position()));

			// Make sure that the policy is well formed
			for (AccessPath condition : declPolicy.flowPol()
					.conditions()) {
				SecurityPolicy condPol = conditionPolicy((IFConsContext) df, condition);
				factory.addConstraint(new WellFormedConstraint(declPolicy, condition, condPol, n.position()));
			}
        } else {
        	if (localDeclVar instanceof SecurityPolicyConstant) {
        		factory.addConstraint(new NoTopConstraint((CESecurityPolicy) ((SecurityPolicyConstant) localDeclVar).constant(), peer.node().position()));
            	} else {
        		factory.addConstraint(new NoTopConstraint((SecurityPolicyVariable) localDeclVar, peer.node().position()));
            }
        }
        
        
        return ret;
    }
    
    @Override
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowNewArray(NewArray n,
			VarContext<SecurityPolicy> dfIn,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {
    	Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowNewArray(n, dfIn, graph, peer);
    	
    	SecurityPolicy arrayElement =
                autil().getLocationAbsVal(dfIn, autil().abstractLocationsForArray(n));
    	
    	// Array contents are not erasable
    	factory.addConstraint(new NoTopConstraint((SecurityPolicyVariable) arrayElement, peer.node().position()));
    	factory.addConstraint(new NoConditionConstraint((SecurityPolicyVariable) arrayElement, peer.node().position()));
    	
    	return ret;
	}

	@Override
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowArrayInit(ArrayInit n,
			VarContext<SecurityPolicy> dfIn,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {
		Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowArrayInit(n, dfIn, graph, peer);
    	
    	SecurityPolicy arrayElement =
                autil().getLocationAbsVal(dfIn, autil().abstractLocationsForArray(n));
    	
    	// Array contents are not erasable
    	factory.addConstraint(new NoTopConstraint((SecurityPolicyVariable) arrayElement, peer.node().position()));
    	factory.addConstraint(new NoConditionConstraint((SecurityPolicyVariable) arrayElement, peer.node().position()));
    	
    	return ret;
	}

	@Override
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowLocalAssign(
			LocalAssign n, VarContext<SecurityPolicy> dfIn_,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {
    	Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowLocalAssign(n, dfIn_, graph, peer);
    	
    	VarContext<SecurityPolicy> df = ret.values().iterator().next();
        SecurityPolicyVariable localVar =
                (SecurityPolicyVariable) df.getLocalAbsVal(n.left().name(), n.left().type());
        
        factory.addConstraint(new NoTopConstraint(localVar, peer.node().position()));
    	
    	return ret;
	}

    @Override
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowLocal(Local n,
			VarContext<SecurityPolicy> dfIn,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {
        dfIn = copyAndConstrain((IFConsContext)dfIn, peer, "local-no-top-condition");
    	
    	Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowLocal(n, dfIn, graph, peer);

    	VarContext<SecurityPolicy> df = ret.values().iterator().next();
        SecurityPolicyVariable localVar = (SecurityPolicyVariable) df.getLocalAbsVal(n.name(), n.type());
    	
        factory.addConstraint(new NoTopConstraint(localVar, peer.node().position()));
    	
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
            SecurityPolicy keyPol = argPolicies.get(1);
            SecurityPolicy plaintextDataPol;
            if (n.methodInstance().name().equals("encryptStrings")) {
            	plaintextDataPol = argPolicies.get(0);
            } else {
            	plaintextDataPol = autil().getLocationAbsVal(dfIn,
                                              autil().abstractLocationsForArray(n.arguments()
                                                                                 .get(1)));
            }

            Map<EdgeKey, VarContext<SecurityPolicy>> res =
                    super.flowCall(n, dfIn, graph, peer);
            SecurityPolicy encResultPol =
                    autil().getLocationAbsVal(res.get(FlowGraph.EDGE_KEY_OTHER),
                                              autil().abstractLocationsForArray(n));
            addEncryptionConstraint(dfIn,
                                    (IFConsSecurityPolicy) keyPol,
                                    (IFConsSecurityPolicy) plaintextDataPol,
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
            SecurityPolicy keyPol = argPolicies.get(1);
            SecurityPolicy ciphertextDataPol = autil().getLocationAbsVal(dfIn,
                    autil().abstractLocationsForArray(n.arguments().get(1)));
            Map<EdgeKey, VarContext<SecurityPolicy>> res =
                    super.flowCall(n, dfIn, graph, peer);
            
            IFConsContext dfOut = (IFConsContext) res.get(FlowGraph.EDGE_KEY_OTHER);
            
            SecurityPolicy decResultPol;
            if (n.methodInstance().name().equals("decryptStrings")) {
            	decResultPol = dfOut.peekExprResult();
            } else {
                decResultPol = autil().getLocationAbsVal(dfOut, autil().abstractLocationsForArray(n));
            }
            addDecryptionConstraint(dfIn,
                                    (IFConsSecurityPolicy) keyPol,
                                    (IFConsSecurityPolicy) ciphertextDataPol,
                                    (IFConsSecurityPolicy) decResultPol,
                                    n.position());
            return res;
        }
        
        if (isTriggerCall(n)) {
        	// we're setting a condition!

        	// Set conditions should not appear in the current context
        	AccessPath cond =
                    CESecurityPolicyFactory.singleton()
                                           .exprToAccessPath((Expr)n.target());
            dfIn = requireNoConstraint((IFConsContext) dfIn,  cond, peer);
            
            // Get the pc
        	SecurityPolicy pcPol = ((CEAnalysisUtil)autil()).copyAndConstrainPC((IFConsContext) dfIn, n);
        	// Level of condition before the call
        	SecurityPolicy condPol = exprConditionPolicy((IFConsContext) dfIn, (Expr) n.target());
        	
        	// pc <= level of condition
        	factory.addConstraint(pcPol, condPol, n.position());
            
            dfIn = requireWellFormed((IFConsContext) dfIn, cond, condPol, peer);
            
        	Map<EdgeKey, VarContext<SecurityPolicy>> res =
                    super.flowCall(n, dfIn, graph, peer);
        	
        	
            return res;
        }

        return super.flowCall(n, dfIn, graph, peer);
    }
    
    private SecurityPolicy exprConditionPolicy(IFConsContext ctxt, Expr e) {
    	AccessPath condition = CESecurityPolicyFactory.singleton().exprToAccessPath(e);
    	return conditionPolicy(ctxt, condition);
    }
    
    private SecurityPolicy conditionPolicy(IFConsContext ctxt, AccessPath condition) {
    	if (condition instanceof AccessPathField) {
			return fieldInstancePolicy((CEFieldInstance) ((AccessPathField) condition).fieldInstance().iterator().next());
		} else {
			LocalInstance li = ((AccessPathLocal) condition).localInstance();
			return ctxt.getLocalAbsVal(li.name(), li.type());
		}
    }
    
    private VarContext<SecurityPolicy> requireWellFormed(
			IFConsContext dfIn, AccessPath condition, SecurityPolicy condPol,
			Peer peer) {
    	dfIn = copyAndConstrain((IFConsContext) dfIn, peer, "well-formed-check");
    	Position pos = peer.node().position();
    	
        for (SecurityPolicy lp : dfIn.locals().values()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) lp;
            factory.addConstraint(new WellFormedConstraint(v, condition, condPol, pos));
        }
        // and the PC
        for (SecurityPolicy p : dfIn.pcmap().getPolicies()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) p;
            factory.addConstraint(new WellFormedConstraint(v, condition, condPol, pos));
        }

        // and the expression stack
        Stack<SecurityPolicy> es = dfIn.exprResultStack();
        while (es != null && !es.isEmpty()) {
            SecurityPolicyVariable v = (SecurityPolicyVariable) es.peek();
            es = es.pop();
            factory.addConstraint(new WellFormedConstraint(v, condition, condPol, pos));
        }
        return dfIn;
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
        if (ciphertextPol == null || decResultPol == null) {
        	throw new InternalCompilerError("NULL? cipher = " + ciphertextPol + " result = " + decResultPol);
        }
        factory.addConstraint(ciphertextPol, decResultPol, pos);
    }

    private boolean isEncryptionCall(Call n) {
        return n.name().startsWith("encrypt")
                && n.target().type().toClass().name().equals("CryptoLibrary");
    }

    private boolean isDecryptionCall(Call n) {
        return n.name().startsWith("decrypt")
                && n.target().type().toClass().name().equals("CryptoLibrary");
    }
    
    private boolean isTriggerCall(Call n) {
    	return CEExt_c.ext(n).isConditionSet();
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
	public Map<EdgeKey, VarContext<SecurityPolicy>> flowFieldDecl(FieldDecl n,
			VarContext<SecurityPolicy> dfIn_,
			FlowGraph<VarContext<SecurityPolicy>> graph,
			Peer<VarContext<SecurityPolicy>> peer) {
		Map<EdgeKey, VarContext<SecurityPolicy>> ret = super.flowFieldDecl(n,
				dfIn_, graph, peer);

		CEFieldInstance cefi = (CEFieldInstance) n.fieldInstance();
		SecurityPolicy pol = fieldInstancePolicy(cefi);
		
		// Final fields are not erasable
		if (cefi.flags().isFinal()) {
			if (pol instanceof SecurityPolicyVariable) {
				factory.addConstraint(new NoConditionConstraint(
						(SecurityPolicyVariable) pol, n.position()));
				factory.addConstraint(new NoTopConstraint((SecurityPolicyVariable)pol, n.position()));
			} else {
				CESecurityPolicy cePol = (CESecurityPolicy) ((SecurityPolicyConstant) pol).constant();
				
				factory.addConstraint(new NoConditionConstraint(cePol, n.position()));
				factory.addConstraint(new NoTopConstraint(cePol, n.position()));
			}
		}
		// Fields may only have explicit erasure policies
		if (cefi.declaredPolicy() == null) {
			factory.addConstraint(new NoConditionConstraint((SecurityPolicyVariable) pol, n.position()));
		} else {
			// Make sure that the policy is well formed
			for (AccessPath condition : cefi.declaredPolicy().flowPol().conditions()) {
				SecurityPolicy condPol = conditionPolicy((IFConsContext) ret.get(FlowGraph.EDGE_KEY_OTHER), condition);
				factory.addConstraint(new WellFormedConstraint(cefi.declaredPolicy(), condition, condPol, n.position()));
			}
		}
		
		return ret;
	}

	private void addWellFormednessConstraints(ErasurePolicy ep, IFConsContext ctxt, Position pos) {
		SecurityPolicy conditionPolicy;
		if (ep.condition() instanceof AccessPathField) {
			FieldInstance fi = ((AccessPathField) ep.condition()).fieldInstance().iterator().next();
			conditionPolicy = factory.getFieldInstanceVar(fi);
		} else {
			LocalInstance li = ((AccessPathLocal) ep.condition()).localInstance();
			conditionPolicy = ctxt.getLocalAbsVal(li.name(), li.type());
		}
		factory.addConstraint(conditionPolicy, ep.initialPolicy(), pos);
		factory.addConstraint(conditionPolicy, ep.finalPolicy(), pos);
		if (ep.initialPolicy() instanceof ErasurePolicy) {
			addWellFormednessConstraints((ErasurePolicy) ep.initialPolicy(), ctxt, pos);
		}
		if (ep.finalPolicy() instanceof ErasurePolicy) {
			addWellFormednessConstraints((ErasurePolicy) ep.finalPolicy(), ctxt, pos);
		}
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
