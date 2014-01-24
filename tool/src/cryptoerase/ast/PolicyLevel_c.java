package cryptoerase.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.CESecurityPolicy;

public class PolicyLevel_c extends Node_c implements PolicyLevel {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private String levelName;

    public PolicyLevel_c(Position pos, String levelName) {
        super(pos);
        this.levelName = levelName;
    }

    @Override
    public <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory, AnalysisUtil autil) {
        return (CESecurityPolicy) factory.parseSecurityString(levelName, this);
    }

    @Override
    public String toString() {
        return this.levelName;
    }
}
