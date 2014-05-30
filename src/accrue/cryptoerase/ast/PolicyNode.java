package accrue.cryptoerase.ast;

import polyglot.ast.Node;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;

public interface PolicyNode extends Node {
    <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory);
}
