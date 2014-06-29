package accrue.cryptoerase.ast;

import polyglot.ast.JLDel_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.visit.TypeChecker;

public class CEAssignDel extends JLDel_c {

	@Override
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		return CEExt_c.ext(node()).typeCheck(tc);
	}

}
