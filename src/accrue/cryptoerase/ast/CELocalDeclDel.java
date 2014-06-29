package accrue.cryptoerase.ast;

import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.translate.ext.ToExt_c;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class CELocalDeclDel extends JLDel_c {
	@Override
	public Node visitChildren(NodeVisitor v) {
		return CEExt_c.ext(node()).visitChildren(v);
	}

	@Override
	public NodeVisitor extRewriteEnter(ExtensionRewriter rw)
			throws SemanticException {
		return ToExt_c.ext(node()).toExtEnter(rw);
	}

	@Override
	public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
		return ToExt_c.ext(node()).toExt(rw);
	}
}
