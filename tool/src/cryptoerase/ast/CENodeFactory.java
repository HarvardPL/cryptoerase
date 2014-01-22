package cryptoerase.ast;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.util.Position;

public interface CENodeFactory extends NodeFactory {

    PolicyNode PolicyErasure(Position pos, PolicyNode p, Expr erasureCondition,
            PolicyNode q);

    PolicyNode PolicyLevel(Position pos, String levelName);

    Expr SecurityCast(Position pos, PolicyNode a, Expr b);

    FieldDecl FieldDecl(Position pos, Flags flags, TypeNode type,
            PolicyNode label, Id name, Expr init);

    LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type,
            PolicyNode label, Id name, Expr init);

}
