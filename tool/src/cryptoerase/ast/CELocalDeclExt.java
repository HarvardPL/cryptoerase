package cryptoerase.ast;

import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

public class CELocalDeclExt extends CEExt_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private PolicyNode label;

    public void setLabel(PolicyNode p) {
        this.label = p;
    }

    public LocalDecl label(PolicyNode p) {
        LocalDecl n = (LocalDecl) this.node().copy();
        CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(n);
        ext.label = p;

        return n;
    }

    public PolicyNode label() {
        return this.label;
    }

    public Node visitChildren(NodeVisitor v) {
        Node newN = this.node().visitChildren(v);
        CELocalDeclExt newext = (CELocalDeclExt) CEExt_c.ext(newN);

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
                newext = (CELocalDeclExt) CEExt_c.ext(newN);
            }
            else {
                // the call to super.visitChildren(v) already
                // created a copy of the node (and thus of its extension).
            }
            newext.label = newLabel;
        }
        return newN;
    }

//    public void updateDeclaredPolicy(AnalysisUtil autil)
//            throws SemanticException {
//        if (this.label() != null) {
//            LocalDecl ld = (LocalDecl) this.node();
//            CELocalInstance li = (CELocalInstance) ld.localInstance();
//            li.setDeclaredPolicy(this.label()
//                                     .policy(CESecurityPolicyFactory.singleton(),
//                                             autil));
//        }
//    }

}
