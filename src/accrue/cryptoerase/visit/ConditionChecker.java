package accrue.cryptoerase.visit;

import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.ast.PolicyErasure;
import accrue.cryptoerase.securityPolicy.AccessPath;
import accrue.cryptoerase.securityPolicy.AccessPathField;
import accrue.cryptoerase.types.CETypeSystem;
import polyglot.ast.ArrayInit;
import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.Job;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;

public class ConditionChecker extends ErrorHandlingVisitor {

	public ConditionChecker(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	@Override
    protected NodeVisitor enterCall(Node n) throws SemanticException {
		if (n instanceof PolicyErasure) {
			/* This should never happen because conditions that are fields must be static final,
			 * but check just in case...
			 */
			PolicyErasure pe = (PolicyErasure) n;
			AccessPath ap = CESecurityPolicyFactory.singleton().exprToAccessPath(pe.erasureCondition());
			if (ap instanceof AccessPathField) {
				for (FieldInstance fi : ((AccessPathField) ap).fieldInstance()) {
					if (!fi.flags().isFinal()) {
						throw new SemanticException("Conditions should only be accessed through final fields");
					}
				}
			}
		}
		
		if (n instanceof LocalDecl) {
			LocalDecl ld = (LocalDecl) n;
			if (ts.typeEquals(ld.type().type(), ((CETypeSystem) ts).Condition())) {
				Expr init = ld.init();
				if (!ld.flags().isFinal()) {
					throw new SemanticException("Conditions must be final");
				}
				if (!(init instanceof New)) {
					throw new SemanticException("Conditions may only be initialized by construction.", n.position());
				}
			} else if (ts.typeEquals(ld.type().type(), ((CETypeSystem) ts).Condition().arrayOf())) {
				throw new SemanticException("May not store conditions in an array");
			}
		}
		
		if (n instanceof FieldDecl) {
			FieldDecl fd = (FieldDecl) n;
			if (ts.typeEquals(fd.type().type(), ((CETypeSystem) ts).Condition())) {
				if (!fd.flags().isStatic()) {
					throw new SemanticException("Conditions must be either locals or static fields");
				}
				if (!fd.flags().isFinal()) {
					throw new SemanticException("Conditions must be final");
				}
				Expr init = fd.init();
				if (!(init instanceof New)) {
					throw new SemanticException("Conditions may only be initialized by construction.", n.position());
				}
			} else if (ts.typeEquals(fd.type().type(), ((CETypeSystem) ts).Condition().arrayOf())) {
				throw new SemanticException("May not store conditions in an array");
			}
		}
		
		if (n instanceof Assign) {
			Assign a = (Assign) n;
			if (ts.typeEquals(a.type(), ((CETypeSystem) ts).Condition())) {
				throw new SemanticException("Conditions may not be assigned");
			}
		}
		
		if (n instanceof Formal) {
			Formal f = (Formal) n;
			if (ts.typeEquals(f.type().type(), ((CETypeSystem) ts).Condition())) {
				throw new SemanticException("May not pass conditions as arguments");
			}
		}
		
		return this;
	}

}
