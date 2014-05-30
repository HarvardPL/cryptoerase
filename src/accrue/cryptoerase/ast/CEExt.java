package accrue.cryptoerase.ast;

import polyglot.ast.Ext;

public interface CEExt extends Ext {
    /**
     * Returns true if this Node sets a condition (i.e.,
     * if execution of this node may trigger erasure).
     */
    boolean isConditionSet();

}
