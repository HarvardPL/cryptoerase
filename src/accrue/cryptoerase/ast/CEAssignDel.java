package accrue.cryptoerase.ast;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import accrue.cryptoerase.types.CETypeSystem;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.JLDel;
import polyglot.ast.JLDel_c;
import polyglot.ast.Lang;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.ConstantChecker;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class CEAssignDel extends JLDel_c {

	@Override
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		Assign a = (Assign) jl().typeCheck(tc);
		
		Type t = a.left().type();
        Type s = a.right().type();

        CETypeSystem ts = (CETypeSystem) tc.typeSystem();
        
        if (t.typeEquals(ts.Condition()) || s.typeEquals(ts.Condition())) {
        	throw new SemanticException("conditions may not be assigned", a.position());
        }
        
        return a;
	}

}
