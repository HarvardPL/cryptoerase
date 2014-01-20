package cryptoerase.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.CryptoSecurityPolicy;

public class PolicyLevel_c extends Node_c implements PolicyNode {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private String levelName;

    public PolicyLevel_c(Position pos, String levelName) {
        super(pos);
        this.levelName = levelName;
    }

    @Override
    public <A extends Ordered<A>> CryptoSecurityPolicy policy(
            CESecurityPolicyFactory<A> factory, AnalysisUtil<A> autil) {
        return (CryptoSecurityPolicy) factory.parseSecurityString(levelName,
                                                                  this,
                                                                  autil);
    }

}
