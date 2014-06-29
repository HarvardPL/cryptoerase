package accrue.cryptoerase.translate;

import accrue.cryptoerase.ast.CEExt_c;
import accrue.cryptoerase.ast.CELocalDeclExt;
import accrue.cryptoerase.ast.CESecurityCast;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.translate.ext.ToExt_c;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

public class LocalDeclToExt_c extends polyglot.translate.ext.LocalDeclToExt_c {
	private static final long serialVersionUID = 1203996497988724614L;

	@Override
	public NodeVisitor toExtEnter(ExtensionRewriter rw)
			throws SemanticException {
		CELocalDeclExt ext = (CELocalDeclExt) CEExt_c.ext(node());
		return rw.bypass(ext.label());
	}
}
