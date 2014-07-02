package accrue.cryptoerase.translate;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;

public class ClassBodyToExt_c extends polyglot.translate.ext.ClassDeclToExt_c {

	@Override
	public Node toExt(ExtensionRewriter rw) throws SemanticException {
		ClassBody cb = (ClassBody) node();
		
		return super.toExt(rw);
	}
	
}
