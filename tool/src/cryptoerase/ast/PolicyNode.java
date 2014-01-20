package cryptoerase.ast;

import polyglot.ast.Node;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.CryptoSecurityPolicy;

public interface PolicyNode extends Node {
    <A extends Ordered<A>> CryptoSecurityPolicy policy(
            CESecurityPolicyFactory<A> factory, AnalysisUtil<A> autil);
}
