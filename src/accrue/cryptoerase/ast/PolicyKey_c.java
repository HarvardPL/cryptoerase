package accrue.cryptoerase.ast;

import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;

public class PolicyKey_c extends Node_c implements PolicyNode {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private PolicyNode keyPolicy; // the policy that is an upper bound on what this key can do.
    private PolicyNode p; // the policy for this
    private boolean isPublicKey;

    public PolicyKey_c(Position pos, boolean isPublicKey, PolicyNode keyPolicy,
            PolicyNode p) {
        super(pos);
        this.isPublicKey = isPublicKey;
        this.keyPolicy = keyPolicy;
        this.p = p;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        PolicyNode keyPolicy = (PolicyNode) visitChild(this.keyPolicy, v);
        PolicyNode p = (PolicyNode) visitChild(this.p, v);
        return reconstruct(keyPolicy, p);
    }

    private Node reconstruct(PolicyNode keyPolicy, PolicyNode p) {
        if (this.keyPolicy == keyPolicy && this.p == p) {
            return this;
        }
        PolicyKey_c n = (PolicyKey_c) copy();
        n.keyPolicy = keyPolicy;
        n.p = p;
        return n;
    }

    @Override
    public <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory) {
        CESecurityPolicy kP = this.keyPolicy.policy(factory);
        CESecurityPolicy pP = this.p.policy(factory);

        if (isPublicKey) {
            return factory.pubKeyPolicy(kP.flowPol(), pP.flowPol());
        }
        else {
            return factory.privKeyPolicy(kP.flowPol(), pP.flowPol());

        }

    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(isPublicKey ? "PUBKEY" : "PRIVKEY");
        sb.append("(");
        sb.append(this.keyPolicy.toString());
        sb.append("){");
        sb.append(this.p.toString());
        sb.append("}");
        return sb.toString();
    }

    public PolicyNode flowPolicy() {
        return p;
    }

}
