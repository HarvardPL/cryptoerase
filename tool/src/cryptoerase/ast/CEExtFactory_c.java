package cryptoerase.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import accrue.analysis.ext.AnalysisExtFactory;
import accrue.infoflow.ext.InfoFlowExtFactory;

public class CEExtFactory_c extends AbstractExtFactory_c implements
        CEExtFactory {
    @Override
    public final Ext extSecurityCast() {
        Ext e = extSecurityCastImpl();

        if (nextExtFactory() != null) {
            Ext e2;
            if (nextExtFactory() instanceof InfoFlowExtFactory) {
                e2 = ((InfoFlowExtFactory) nextExtFactory()).extSecurityCast();
            }
            else {
                e2 = nextExtFactory().extExpr();
            }
            e = composeExts(e, e2);
        }
        return postExtSecurityCast(e);
    }

    @Override
    public final Ext extOutputExpr() {
        Ext e = extOutputExprImpl();

        if (nextExtFactory() != null) {
            Ext e2;
            if (nextExtFactory() instanceof InfoFlowExtFactory) {
                e2 = ((InfoFlowExtFactory) nextExtFactory()).extOutputExpr();
            }
            else {
                e2 = nextExtFactory().extExpr();
            }
            e = composeExts(e, e2);
        }
        return postExtOutputExpr(e);
    }

    /**
     * Create a new security cast node extension
     */
    protected Ext extSecurityCastImpl() {
        return extExprImpl();
    }

    /**
     * Create a new output expression node  extension
     */
    protected Ext extOutputExprImpl() {
        return extExprImpl();
    }

    /**
     * Call this after constructing a security cast extension
     * 
     * @param ext
     *            extension to post process
     * @return extension after post processing
     */
    protected Ext postExtSecurityCast(Ext ext) {
        return postExtExpr(ext);
    }

    /**
     * Call this after constructing a security cast extension
     * 
     * @param ext
     *            extension to post process
     * @return extension after post processing
     */
    protected Ext postExtOutputExpr(Ext ext) {
        return postExtExpr(ext);
    }

    @Override
    public final Ext extPolicyNode() {
        Ext e = extPolicyNodeImpl();

        if (nextExtFactory() != null) {
            Ext e2;
            if (nextExtFactory() instanceof CEExtFactory) {
                e2 = ((CEExtFactory) nextExtFactory()).extPolicyNode();
            }
            else {
                e2 = nextExtFactory().extNode();
            }
            e = composeExts(e, e2);
        }
        return postExtPolicyNode(e);
    }

    protected Ext extPolicyNodeImpl() {
        return extNodeImpl();
    }

    protected Ext postExtPolicyNode(Ext ext) {
        return postExtNode(ext);
    }

    @Override
    public Ext extSuppress() {
        Ext e = extSuppressImpl();

        if (nextExtFactory() != null
                && nextExtFactory() instanceof AnalysisExtFactory) {
            Ext e2 = ((AnalysisExtFactory) nextExtFactory()).extSuppress();
            e = composeExts(e, e2);
        }
        return postExtSuppress(e);
    }

    protected Ext extSuppressImpl() {
        return extExprImpl();
    }

    protected Ext postExtSuppress(Ext e) {
        return e;
    }

    @Override
    protected Ext extFieldDeclImpl() {
        return new CEFieldDeclExt();
    }

    @Override
    protected Ext extLocalDeclImpl() {
        return new CELocalDeclExt();
    }

    @Override
    protected Ext extAssignImpl() {
        return new CEAssignExt();
    }

    @Override
    protected Ext extCallImpl() {
        return new CEProcedureCallExt();
    }

    @Override
    protected Ext extConstructorCallImpl() {
        return new CEProcedureCallExt();
    }

    @Override
    protected Ext extNewImpl() {
        return new CEProcedureCallExt();
    }

    @Override
    protected Ext extNodeImpl() {
        return new CEExt_c();
    }

}
