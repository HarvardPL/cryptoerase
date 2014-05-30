package accrue.cryptoerase.ast;

import polyglot.ast.Ext;
import polyglot.ast.Ext_c;
import polyglot.ast.Node;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;

public class CEExt_c extends Ext_c implements CEExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static CEExt ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof CEExt)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No CEExt extension object for node "
                                                    + n
                                                    + " ("
                                                    + n.getClass()
                                                    + ")",
                                            n.position());
        }
        return (CEExt) e;
    }

    public CEExt_c(Ext ext) {
        super(ext);
    }

    public CEExt_c() {
        super();
    }

    @Override
    public boolean isConditionSet() {
        return false;
    }

}
