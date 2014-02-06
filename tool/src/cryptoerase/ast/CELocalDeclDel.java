package cryptoerase.ast;

import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

public class CELocalDeclDel extends JLDel_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node visitChildren(NodeVisitor v) {
        CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(this.node());
        return ext.visitChildren(v);
    }

}
