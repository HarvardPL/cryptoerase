package cryptflow;

import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.Ordered;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.SecurityPolicyFactory;

public class CryptoSecurityPolicyFactory<A extends Ordered<A>> extends SecurityPolicyFactory<A> {
	public static SecurityPolicy LOW =  new CryptoSecurityPolicy("L");
    public static SecurityPolicy HIGH = new CryptoSecurityPolicy("H");
    public static SecurityPolicy PUBKEY = new CryptoSecurityPolicy("PUBKEY");
    public static SecurityPolicy PRIVKEY = new CryptoSecurityPolicy("PRIVKEY");
    public static SecurityPolicy ERROR = new CryptoSecurityPolicy("ERROR");

    /**
     * Only accepts "H" or "L"
     * <p>
     * {@inheritDoc}
     */
    @Override
    public SecurityPolicy parseSecurityString(String securityString, Node source, AnalysisUtil<A> autil) {
        if ("H".equals(securityString)) return HIGH;
        if ("L".equals(securityString)) return LOW;
        if ("PUBKEY".equals(securityString)) return PUBKEY;
        if ("PRIVKEY".equals(securityString)) return PRIVKEY;
        throw new InternalCompilerError("Illegal security string: " + securityString, source.position());
    }

    @Override
    public SecurityPolicy bottom() {
        return LOW;
    }

    /**
     * Security policy on a two element lattice
     */
    protected static class CryptoSecurityPolicy implements SecurityPolicy {
        
        /**
         * Name of the security policy
         */
        private final String name;

		/**
		 * Create a new policy
		 * 
		 * @param name
		 *            policy name
		 * @return 
		 */
        public CryptoSecurityPolicy(String name) {
            this.name = name;
            if (name == null) {
            	throw new IllegalArgumentException("Name must be non null");
            }
        }
        @Override
		public boolean leq(SecurityPolicy p) {
        	CryptoSecurityPolicy that = (CryptoSecurityPolicy) p;
        	if (this == p) return true;
        	if (this == LOW && p == HIGH) return true;
        	return false;
        }
        
        @Override
		public boolean isBottom() {
            return this == LOW;
        }
        @Override
		public SecurityPolicy upperBound(SecurityPolicy p) {
        	CryptoSecurityPolicy that = (CryptoSecurityPolicy) p;
        	if (this == p) return this;
        	if (this == HIGH && p == LOW) return HIGH;
        	if (this == LOW && p == HIGH) return HIGH;
        	return ERROR;
        	
        }
        @Override
		public SecurityPolicy widen(SecurityPolicy that) {
            return upperBound(that);
        }
        @Override
        public int hashCode() {
        	return name.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
        @Override
        public String toString() {
            return name;
        }
        @Override
		public void prettyPrint(CodeWriter cw) {
            cw.write(this.toString());            
        }
    }

}
