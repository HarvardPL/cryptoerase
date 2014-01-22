package cryptoerase.ast;

import polyglot.ast.AbstractDelFactory_c;
import polyglot.ast.JLDel;

public class CEDelFactory_c extends AbstractDelFactory_c {
    @Override
    protected JLDel delFieldDeclImpl() {
        return new CEFieldDeclDel();
    }

    @Override
    protected JLDel delLocalDeclImpl() {
        return new CELocalDeclDel();
    }

}
