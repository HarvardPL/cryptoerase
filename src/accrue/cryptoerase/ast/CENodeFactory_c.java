package accrue.cryptoerase.ast;

import polyglot.ast.DelFactory;
import polyglot.ast.Expr;
import polyglot.ast.ExtFactory;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import accrue.infoflow.ast.InfoFlowNodeFactory_c;
import accrue.infoflow.ast.SecurityCast;
import accrue.infoflow.ext.InfoFlowExtFactory;

public class CENodeFactory_c extends InfoFlowNodeFactory_c implements
        CENodeFactory {

    public CENodeFactory_c(ExtFactory extFactory) {
        super(extFactory);
    }

    public CENodeFactory_c(ExtFactory extFactory, DelFactory delFactory) {
        super(extFactory, delFactory);
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
        SecurityCast n = new CESecurityCast_c(pos, policy, expr);
        n =
                (SecurityCast) n.ext(((InfoFlowExtFactory) extFactory()).extSecurityCast());
        n = (SecurityCast) n.del(delFactory().delExpr()); // no delegates at the moment.
        return n;

    }

    @Override
    public FieldDecl FieldDecl(Position pos, Flags flags, TypeNode type,
            PolicyNode label, Id name, Expr init) {
        FieldDecl n = FieldDecl(pos, flags, type, name, init);
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(n);
        ext.setLabel(label);
        return n;
    }

    @Override
    public LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type,
            PolicyNode label, Id name, Expr init) {
        LocalDecl n = LocalDecl(pos, flags, type, name, init);
        CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(n);
        ext.setLabel(label);
        return n;
    }

    @Override
    public PolicyNode PubKeyPolicy(Position pos, PolicyNode keyBound,
            PolicyNode flowPol) {
        PolicyKey_c n = new PolicyKey_c(pos, true, keyBound, flowPol);
        n =
                (PolicyKey_c) n.ext(((CEExtFactory) extFactory()).extPolicyNode());
        n = (PolicyKey_c) n.del(delFactory().delNode()); // no delegates at the moment.
        return n;
    }

    @Override
    public PolicyNode PrivKeyPolicy(Position pos, PolicyNode keyBound,
            PolicyNode flowPol) {
        PolicyKey_c n = new PolicyKey_c(pos, false, keyBound, flowPol);
        n =
                (PolicyKey_c) n.ext(((CEExtFactory) extFactory()).extPolicyNode());
        n = (PolicyKey_c) n.del(delFactory().delNode()); // no delegates at the moment.
        return n;
    }

}
