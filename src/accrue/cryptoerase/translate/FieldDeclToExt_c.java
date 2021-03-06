package accrue.cryptoerase.translate;

import accrue.cryptoerase.ast.CEExt_c;
import accrue.cryptoerase.ast.CEFieldDeclExt;
import accrue.cryptoerase.securityPolicy.CESecurityPolicy;
import accrue.cryptoerase.securityPolicy.ErasurePolicy;
import accrue.cryptoerase.types.CEFieldInstance;
import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

public class FieldDeclToExt_c extends polyglot.translate.ext.FieldDeclToExt_c {
	private static final long serialVersionUID = -3824564681188606502L;
	
	@Override
	public NodeVisitor toExtEnter(ExtensionRewriter rw)
			throws SemanticException {
		CEFieldDeclExt ext = (CEFieldDeclExt) CEExt_c.ext(node());
		return rw.bypass(ext.label());
	}
	
}
