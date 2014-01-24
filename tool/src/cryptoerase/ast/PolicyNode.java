package cryptoerase.ast;

import polyglot.ast.Node;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.CESecurityPolicy;

public interface PolicyNode extends Node {
    <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory, AnalysisUtil autil);
}
