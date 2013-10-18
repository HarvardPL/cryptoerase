package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

public class PolicyErasure_c extends Node_c implements PolicyNode {
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
}
