package accrue.cryptoerase.ast;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import accrue.infoflow.ext.InfoFlowExtFactory_c;

public class CEAccrueExtFactory_c extends InfoFlowExtFactory_c implements
        CEExtFactory {

    public CEAccrueExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    public CEAccrueExtFactory_c() {
        super();
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
        return new ExtPolicyNode();
    }

    protected Ext postExtPolicyNode(Ext ext) {
        return postExtNode(ext);
    }
    
	protected Ext postExtTrigger(Ext ext) {
		return postExtEval(ext);
	}
	
}
