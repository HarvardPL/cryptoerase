package accrue.cryptoerase.ast;

import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.translate.ext.ToExt_c;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

public class CEClassBodyDel extends JLDel_c {

	@Override
	public NodeVisitor extRewriteEnter(ExtensionRewriter rw)
			throws SemanticException {
		return ToExt_c.ext(node()).toExtEnter(rw);
	}
	
	@Override
	public Node extRewrite(ExtensionRewriter rw)
			throws SemanticException {
		return ToExt_c.ext(node()).toExt(rw);
	}
	
}
