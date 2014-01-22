package cryptoerase;

import java.io.Reader;

import polyglot.ast.NodeFactory;
import polyglot.frontend.CupParser;
import polyglot.frontend.FileSource;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.lex.EscapedUnicodeReader;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;
import cryptoerase.ast.CEDelFactory_c;
import cryptoerase.ast.CEExtFactory_c;
import cryptoerase.ast.CENodeFactory;
import cryptoerase.ast.CENodeFactory_c;
import cryptoerase.types.CETypeSystem;
import cryptoerase.types.CETypeSystem_c;

public class CryptoErasureExtensionInfo extends InfoFlowExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new CEScheduler(this);
    }

    @Override
    public SecurityPolicyFactory createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return CESecurityPolicyFactory.singleton();
    }

    @Override
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        reader = new EscapedUnicodeReader(reader);

        polyglot.lex.Lexer lexer =
                new cryptoerase.parse.Lexer_c(reader, source, eq);
        polyglot.parse.BaseParser parser =
                new cryptoerase.parse.Grm(lexer,
                                          (CETypeSystem) ts,
                                          (CENodeFactory) nf,
                                          eq);

        return new CupParser(parser, source, eq);
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new CENodeFactory_c(new CEExtFactory_c(), new CEDelFactory_c());
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new CETypeSystem_c();
    }

}
