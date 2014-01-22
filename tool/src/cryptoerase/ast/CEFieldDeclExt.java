package cryptoerase.ast;

import polyglot.ast.FieldDecl;
import polyglot.util.SerialVersionUID;

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

}
