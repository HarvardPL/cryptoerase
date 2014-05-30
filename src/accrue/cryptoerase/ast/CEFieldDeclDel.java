package accrue.cryptoerase.ast;

import accrue.cryptoerase.CESecurityPolicyFactory;
import accrue.cryptoerase.types.CEFieldInstance;
import polyglot.ast.FieldDecl;
import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class CEFieldDeclDel extends JLDel_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        FieldDecl fd = (FieldDecl) jl().typeCheck(tc);
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(fd);
        if (ext.label() != null) {
            CEFieldInstance fi = (CEFieldInstance) fd.fieldInstance();
            CESecurityPolicyFactory fac = CESecurityPolicyFactory.singleton();
            if (ext.label() instanceof PolicyErasure
                    || (ext.label() instanceof PolicyKey_c && ((PolicyKey_c) ext.label()).flowPolicy() instanceof PolicyErasure)) {
                throw new SemanticException("Can't have an erasure policy on a field.");
            }

            fi.setDeclaredPolicy(ext.label().policy(fac));
        }
        return fd;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(this.node());
        return ext.visitChildren(v);
    }
}
