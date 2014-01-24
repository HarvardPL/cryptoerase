package cryptoerase.ast;

import polyglot.ast.FieldDecl;
import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.types.CEFieldInstance;

public class CEFieldDeclDel extends JLDel_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        FieldDecl fd = (FieldDecl) jl().typeCheck(tc);
        CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(fd);
        if (ext.label() != null) {
            CEFieldInstance fi = (CEFieldInstance) fd.fieldInstance();
            CESecurityPolicyFactory fac = CESecurityPolicyFactory.singleton();
            fi.setDeclaredPolicy(ext.label().policy(fac, null));
        }
        return fd;
    }
}
