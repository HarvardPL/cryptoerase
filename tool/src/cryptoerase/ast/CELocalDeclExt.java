package cryptoerase.ast;

import polyglot.ast.LocalDecl;
import polyglot.util.SerialVersionUID;

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
