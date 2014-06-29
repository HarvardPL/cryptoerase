package accrue.cryptoerase.ast;

import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.types.CEFieldInstance;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class CEFieldDeclExt extends CEExt_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private PolicyNode label;

    public void setLabel(PolicyNode p) {
        this.label = p;
    }

    public FieldDecl label(PolicyNode p) {
        FieldDecl n = (FieldDecl) this.node().copy();
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(n);
        ext.label = p;

        return n;
    }

    public PolicyNode label() {
        return this.label;
    }

    public Node visitChildren(NodeVisitor v) {
        Node newN = this.node().visitChildren(v);
        CEFieldDeclExt newext = (CEFieldDeclExt) CEExt_c.ext(newN);
        PolicyNode newLabel;
        if (this.label == null) {
            newLabel = null;
        }
        else {
            newLabel = v.visitEdge(this.node(), this.label);
        }
        if (newLabel != this.label) {
            // the label changed! update it
            if (newN == this.node()) {
                // we need to create a copy.
                newN = (Node) newN.copy();
            }
            newext = (CEFieldDeclExt) CEExt_c.ext(newN);
            newext.label = newLabel;
        }
        return newN;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        FieldDecl fd = (FieldDecl) superLang().typeCheck(node(), tc);
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(fd);
        if (ext.label() != null) {
            CEFieldInstance fi = (CEFieldInstance) fd.fieldInstance();
            CESecurityPolicyFactory fac = CESecurityPolicyFactory.singleton();
            if (fi.flags().isFinal() &&
            		(ext.label() instanceof PolicyErasure
                    || (ext.label() instanceof PolicyKey_c && ((PolicyKey_c) ext.label()).flowPolicy() instanceof PolicyErasure))) {
                throw new SemanticException("Can't have an erasure policy on a final field.");
            }

            fi.setDeclaredPolicy(ext.label().policy(fac));
        }
        return fd;
    }

}
