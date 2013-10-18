package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.ast.TypeNode;
import polyglot.util.Position;
import accrue.infoflow.ast.InfoFlowNodeFactory_c;

public class CryptoErasureNodeFactory_c extends InfoFlowNodeFactory_c implements
        CryptoEraseNodeFactory {

    public CryptoErasureNodeFactory_c(ExtFactory extFactory) {
        super(extFactory);
    }

    @Override
    public PolicyNode PolicyErasure(Position pos, PolicyNode p,
            Expr erasureCondition, PolicyNode q) {
        return new PolicyErasure_c(pos, p, erasureCondition, q);
    }

    @Override
    public PolicyNode PolicyLevel(Position pos, String levelName) {
        return new PolicyLevel_c(pos, levelName);
    }

    @Override
    public polyglot.ast.AmbTypeNode LabeledTypeNode(Position pos, TypeNode a,
            PolicyNode b) {
    }

}
