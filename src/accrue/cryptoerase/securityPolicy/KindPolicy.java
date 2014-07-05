package accrue.cryptoerase.securityPolicy;

import java.io.Serializable;

import polyglot.util.CodeWriter;
import accrue.infoflow.analysis.SecurityPolicy;

public abstract class KindPolicy implements SecurityPolicy, Serializable {
    public static KindPolicy OTHER = new Other();

    private static class Other extends KindPolicy {

        @Override
        public void prettyPrint(CodeWriter cw) {
            cw.write("Other");
        }

        @Override
        public boolean isBottom() {
            return false;
        }

        @Override
        public boolean leq(SecurityPolicy o) {
            // we are at the botton of the ordering.
            return true;
        }

        @Override
        public SecurityPolicy upperBound(SecurityPolicy o) {
            return o;
        }

        @Override
        public SecurityPolicy widen(SecurityPolicy o) {
            return upperBound(o);
        }

    }

}
