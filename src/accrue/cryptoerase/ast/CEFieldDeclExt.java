package accrue.cryptoerase.ast;

import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

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

//    public void updateDeclaredPolicy(AnalysisUtil autil)
//            throws SemanticException {
//        if (this.label() != null) {
//            FieldDecl fd = (FieldDecl) this.node();
//            CEFieldInstance fi = (CEFieldInstance) fd.fieldInstance();
//            fi.setDeclaredPolicy(this.label()
//                                     .policy(CESecurityPolicyFactory.singleton(),
//                                             autil));
//        }
//    }

}
