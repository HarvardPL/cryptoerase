package accrue.cryptoerase.ast;

import polyglot.ast.Expr;

public interface PolicyErasure extends PolicyNode {

    PolicyNode initialPolicy();

    Expr erasureCondition();

    PolicyNode finalPolicy();

}
