package cryptoerase.ast;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.util.Position;

public interface CryptoEraseNodeFactory extends NodeFactory {

    PolicyNode PolicyErasure(Position pos, PolicyNode p, Expr erasureCondition,
            PolicyNode q);

    PolicyNode PolicyLevel(Position pos, String levelName);

    AmbTypeNode LabeledTypeNode(Position pos, TypeNode a, PolicyNode b);

}
