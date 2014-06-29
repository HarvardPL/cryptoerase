package accrue.cryptoerase.translate;

import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.translate.ext.ToExt_c;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;
import accrue.cryptoerase.ast.CESecurityCast;

public class SecurityCastToExt_c extends ToExt_c {
	private static final long serialVersionUID = 7940651559294348163L;

	@Override
	public NodeVisitor toExtEnter(ExtensionRewriter rw)
			throws SemanticException {
		CESecurityCast c = (CESecurityCast) node();
		return rw.bypass(c.policyNode());
	}
	
	@Override
	public Node toExt(ExtensionRewriter rw) throws SemanticException {
		CESecurityCast c = (CESecurityCast) node;
		return c.expr();
	}	
}
