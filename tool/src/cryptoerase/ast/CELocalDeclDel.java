package cryptoerase.ast;

import polyglot.ast.JLDel_c;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.types.CELocalInstance;

public class CELocalDeclDel extends JLDel_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        LocalDecl ld = (LocalDecl) jl().typeCheck(tc);
        CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(ld);
        if (ext.label() != null) {
            CELocalInstance li = (CELocalInstance) ld.localInstance();
            li.setDeclaredPolicy(ext.label()
                                    .policy(CESecurityPolicyFactory.singleton()));
        }
        return ld;
    }
}
