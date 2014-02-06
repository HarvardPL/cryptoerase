package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import accrue.analysis.interprocanalysis.Ordered;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.CESecurityPolicy;

public class PolicyErasure_c extends Node_c implements PolicyErasure {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private PolicyNode p;
    private Expr erasureCondition;
    private PolicyNode q;

    public PolicyErasure_c(Position pos, PolicyNode p, Expr erasureCondition,
            PolicyNode q) {
        super(pos);
        this.p = p;
        this.erasureCondition = erasureCondition;
        this.q = q;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        PolicyNode p = (PolicyNode) visitChild(this.p, v);
        Expr erasureCondition = (Expr) visitChild(this.erasureCondition, v);
        PolicyNode q = (PolicyNode) visitChild(this.q, v);
        return reconstruct(p, erasureCondition, q);
    }

    @Override
    public boolean isDisambiguated() {
        if (!erasureCondition.isDisambiguated() || !super.isDisambiguated()) {
            return false;
        }
        return true;
    }

    private Node reconstruct(PolicyNode p, Expr ec, PolicyNode q) {
        if (this.p == p && this.erasureCondition == ec && this.q == q) {
            return this;
        }
        PolicyErasure_c n = (PolicyErasure_c) copy();
        n.p = p;
        n.erasureCondition = ec;
        n.q = q;
        return n;
    }

    @Override
    public <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory) {
        CESecurityPolicy initialPol = this.p.policy(factory);
        AccessPath cond = factory.exprToAccessPath(this.erasureCondition);
        CESecurityPolicy finalPol = this.q.policy(factory);
        return factory.otherPolicy(factory.erasurePolicy(initialPol.flowPol(),
                                                         cond,
                                                         finalPol.flowPol()));
    }

    @Override
    public PolicyNode initialPolicy() {
        return this.p;
    }

    @Override
    public Expr erasureCondition() {
        return this.erasureCondition;
    }

    @Override
    public PolicyNode finalPolicy() {
        return this.q;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.p.toString());
        sb.append(" /");
        sb.append(this.erasureCondition.toString());
        sb.append(" ");
        sb.append(this.q.toString());
        return sb.toString();
    }

}
