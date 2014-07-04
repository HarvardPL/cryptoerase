package accrue.cryptoerase.ast;

import accrue.cryptoerase.translate.FieldDeclToExt_c;
import accrue.cryptoerase.translate.LocalDeclToExt_c;
import accrue.cryptoerase.translate.SecurityCastToExt_c;
import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import polyglot.translate.ext.ToExtFactory_c;

public class CEToExtFactory_c extends ToExtFactory_c implements CEExtFactory {
	
	public CEToExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }
	
	public CEToExtFactory_c() {
		super();
	}

	@Override
	public Ext extSecurityCast() {
		return new SecurityCastToExt_c();
	}

	@Override
	public Ext extOutputExpr() {
		return null;
	}

	@Override
	public Ext extSuppress() {
		return null;
	}

	@Override
	public Ext extPolicyNode() {
		return null;
	}

	@Override
	protected Ext extFieldDeclImpl() {
		return new FieldDeclToExt_c();
	}

	@Override
	protected Ext extLocalDeclImpl() {
		return new LocalDeclToExt_c();
	}
}
