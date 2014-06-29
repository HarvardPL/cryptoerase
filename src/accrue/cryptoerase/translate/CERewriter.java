package accrue.cryptoerase.translate;

import polyglot.ast.Node;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

public class CERewriter extends ExtensionRewriter {

	public CERewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
	}
	
}
