package accrue.cryptoerase;

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
import accrue.cryptoerase.ast.CEAccrueExtFactory_c;
import accrue.cryptoerase.ast.CEDelFactory_c;
import accrue.cryptoerase.ast.CEExtFactory_c;
import accrue.cryptoerase.ast.CENodeFactory;
import accrue.cryptoerase.ast.CENodeFactory_c;
import accrue.cryptoerase.types.CETypeSystem;
import accrue.cryptoerase.types.CETypeSystem_c;
import accrue.infoflow.InfoFlowExtensionInfo;
import accrue.infoflow.analysis.SecurityPolicyFactory;

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
                new accrue.cryptoerase.parse.Lexer_c(reader, source, eq);
        polyglot.parse.BaseParser parser =
                new accrue.cryptoerase.parse.Grm(lexer,
                                                 (CETypeSystem) ts,
                                                 (CENodeFactory) nf,
                                                 eq);

        return new CupParser(parser, source, eq);
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new CENodeFactory_c(new CEAccrueExtFactory_c(new CEExtFactory_c()),
                                   new CEDelFactory_c());
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new CETypeSystem_c();
    }

}
