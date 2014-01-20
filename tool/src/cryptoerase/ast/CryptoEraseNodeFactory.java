package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.util.Position;

public interface CryptoEraseNodeFactory extends NodeFactory {

    PolicyNode PolicyErasure(Position pos, PolicyNode p, Expr erasureCondition,
            PolicyNode q);

    PolicyNode PolicyLevel(Position pos, String levelName);

    Expr SecurityCast(Position pos, PolicyNode a, Expr b);

}
