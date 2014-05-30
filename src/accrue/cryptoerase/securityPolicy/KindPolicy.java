package accrue.cryptoerase.securityPolicy;

import polyglot.util.CodeWriter;
import accrue.infoflow.analysis.SecurityPolicy;

public abstract class KindPolicy implements SecurityPolicy {
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
            // we are at the top of the ordering.
            return this == o;
        }

        @Override
        public SecurityPolicy upperBound(SecurityPolicy o) {
            return this;
        }

        @Override
        public SecurityPolicy widen(SecurityPolicy o) {
            return upperBound(o);
        }

    }

}
