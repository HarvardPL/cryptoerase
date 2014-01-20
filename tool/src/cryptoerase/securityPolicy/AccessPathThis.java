package cryptoerase.securityPolicy;

import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

/**
 * Represents a final access path rooted at "this".
 * @see jif.types.label.AccessPath
 */
public class AccessPathThis extends AccessPathRoot {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private ClassType ct;

    /**
     * 
     * @param ct may be null.
     */
    public AccessPathThis(ClassType ct, Position pos) {
        super(pos);
        this.ct = ct;
    }

    @Override
    public boolean isCanonical() {
        return true;
    }

    @Override
    public boolean isNeverNull() {
        return true;
    }

    @Override
    public AccessPath subst(AccessPathRoot r, AccessPath e) {
        if (r instanceof AccessPathThis) {
            if (this.equals(r)) {
                return e;
            }
        }
        return this;
    }

    @Override
    public String toString() {
        String name = "<not-typechecked>";
        if (Report.should_report(Report.debug, 2)) {
            if (ct != null) name = ct.fullName();
            return "this(of " + name + ")";
        }
        if (Report.should_report(Report.debug, 1)) {
            if (ct != null) name = ct.name();
            return "this(of " + name + ")";
        }
        return "this";
    }

    @Override
    public String exprString() {
        return "this";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AccessPathThis) {
            AccessPathThis that = (AccessPathThis) o;
            if (this.ct == that.ct || this.ct == null || that.ct == null)
                return true;
            // return true if this.ct <= that.ct or that.ct <= this.ct
            return this.ct.isSubtype(that.ct) || that.ct.isSubtype(this.ct);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return -572309;
    }

    @Override
    public Type type() {
        return ct;
    }

    @Override
    public void verify(Context A) throws SemanticException {
        if (ct == null) {
            ct = A.currentClass();
        }
        else {
            if (!A.currentClass().isSubtype(ct)) {
                throw new InternalCompilerError("Unexpected class type for "
                        + "access path this: wanted a supertype of "
                        + A.currentClass());
            }
        }
    }
}
