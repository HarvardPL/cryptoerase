package accrue.cryptoerase.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.cryptoerase.securityPolicy.FlowPolicy;

public class PolicyLevel_c extends Node_c implements PolicyLevel {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private String levelName;

    public PolicyLevel_c(Position pos, String levelName) {
        super(pos);
        this.levelName = levelName;
    }

    @Override
    public <A extends Ordered<A>> CESecurityPolicy policy(
            CESecurityPolicyFactory<A> factory) {
        FlowPolicy fp =
                (FlowPolicy) factory.parseSecurityString(levelName, this);
        return factory.otherPolicy(fp);
    }

    @Override
    public String toString() {
        return this.levelName;
    }
}
