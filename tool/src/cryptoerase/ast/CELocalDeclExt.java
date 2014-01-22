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
}
