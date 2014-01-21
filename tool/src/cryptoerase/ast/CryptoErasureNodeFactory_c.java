package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.util.Position;
import accrue.infoflow.ast.InfoFlowNodeFactory_c;
import accrue.infoflow.ast.SecurityCast;
import accrue.infoflow.ext.InfoFlowExtFactory;

public class CryptoErasureNodeFactory_c extends InfoFlowNodeFactory_c implements
        CryptoEraseNodeFactory {

    public CryptoErasureNodeFactory_c(ExtFactory extFactory) {
        super(extFactory);
    }

    @Override
    public PolicyErasure PolicyErasure(Position pos, PolicyNode p,
            Expr erasureCondition, PolicyNode q) {
        PolicyErasure n = new PolicyErasure_c(pos, p, erasureCondition, q);
        n =
                (PolicyErasure) n.ext(((CEExtFactory) extFactory()).extPolicyNode());
        n = (PolicyErasure) n.del(delFactory().delNode()); // no delegates at the moment.
        return n;
    }

    @Override
    public PolicyNode PolicyLevel(Position pos, String levelName) {
        PolicyLevel n = new PolicyLevel_c(pos, levelName);
        n = (PolicyLevel) n.ext(((CEExtFactory) extFactory()).extPolicyNode());
        n = (PolicyLevel) n.del(delFactory().delNode()); // no delegates at the moment.
        return n;
    }

    @Override
    public Expr SecurityCast(Position pos, PolicyNode policy, Expr expr) {
        SecurityCast n = new CryptoEraseSecurityCast_c(pos, policy, expr);
        n =
                (SecurityCast) n.ext(((InfoFlowExtFactory) extFactory()).extSecurityCast());
        n = (SecurityCast) n.del(delFactory().delExpr()); // no delegates at the moment.
        return n;

    }

}
