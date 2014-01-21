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
import cryptoerase.ast.CEExtFactory_c;
import cryptoerase.ast.CryptoEraseNodeFactory;
import cryptoerase.ast.CryptoErasureNodeFactory_c;
import cryptoerase.types.CryptoEraseTypeSystem;
import cryptoerase.types.CryptoErasureTypeSystem_c;

public class CryptoErasureExtensionInfo extends InfoFlowExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new CEScheduler(this);
    }

    @Override
    public SecurityPolicyFactory createSecurityPolicyFactory(Goal g) {
        // return new HLSecurityPolicyFactory();
        return new CESecurityPolicyFactory();
    }

    @Override
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        reader = new EscapedUnicodeReader(reader);

        polyglot.lex.Lexer lexer =
                new cryptoerase.parse.Lexer_c(reader, source, eq);
        polyglot.parse.BaseParser parser =
                new cryptoerase.parse.Grm(lexer,
                                          (CryptoEraseTypeSystem) ts,
                                          (CryptoEraseNodeFactory) nf,
                                          eq);

        return new CupParser(parser, source, eq);
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new CryptoErasureNodeFactory_c(new CEExtFactory_c());
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new CryptoErasureTypeSystem_c();
    }

}
