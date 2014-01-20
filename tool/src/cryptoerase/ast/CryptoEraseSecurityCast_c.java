package cryptoerase.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import accrue.infoflow.ast.SecurityCast;

public class CryptoEraseSecurityCast_c extends Expr_c implements SecurityCast {
    private static final long serialVersionUID = 1L;

    public CryptoEraseSecurityCast_c(Position pos, PolicyNode policyNode,
            Expr expr) {
        super(pos);
        this.policyNode = policyNode;
        this.expr = expr;
    }

    /**
     * The security policy for the expression.
     */
    protected PolicyNode policyNode;
    /**
     * Expression to be cast
     */
    protected Expr expr;

    /** Get the precedence of the expression. */
    @Override
    public Precedence precedence() {
        return Precedence.CAST;
    }

    @Override
    public String securityString() {
        return this.policyNode.toString();
    }

    /** Set the cast type of the expression. */
    @Override
    public SecurityCast securityString(String securityString) {
        throw new UnsupportedOperationException("Sorry");
    }

    /** Get the expression being cast. */
    @Override
    public Expr expr() {
        return this.expr;
    }

    /** Set the expression being cast. */
    @Override
    public SecurityCast expr(Expr expr) {
        CryptoEraseSecurityCast_c n = (CryptoEraseSecurityCast_c) copy();
        n.expr = expr;
        return n;
    }

    /** Reconstruct the expression. */
    protected CryptoEraseSecurityCast_c reconstruct(PolicyNode policyNode,
            Expr expr) {
        if (policyNode != this.policyNode || expr != this.expr) {
            CryptoEraseSecurityCast_c n = (CryptoEraseSecurityCast_c) copy();
            n.policyNode = policyNode;
            n.expr = expr;
            return n;
        }

        return this;
    }

    /** Visit the children of the expression. */
    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr expr = (Expr) visitChild(this.expr, v);
        return reconstruct(policyNode, expr);
    }

    /** Type check the expression. */
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return type(expr.type());
    }

    @Override
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        return child.type();
    }

    @Override
    public String toString() {
        return "<" + policyNode + "> " + expr;
    }

    /** Write the expression to an output file. */
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        w.write("<");
        w.write(this.securityString());
        w.write(">");
        w.allowBreak(2, " ");
        printSubExpr(expr, w, tr);
        w.end();
    }

    @Override
    public Term firstChild() {
        return expr;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(expr, this, EXIT);
        return succs;
    }

    @Override
    public List<Type> throwTypes(TypeSystem ts) {
        return Collections.<Type> emptyList();
    }

    @Override
    public boolean isConstant() {
        return expr().isConstant();
    }

    @Override
    public Object constantValue() {
        return expr().constantValue();
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((CryptoEraseNodeFactory) nf).SecurityCast(this.position,
                                                          this.policyNode,
                                                          this.expr);
    }

}
