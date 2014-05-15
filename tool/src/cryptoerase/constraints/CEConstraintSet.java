package cryptoerase.constraints;

import java.util.Set;

import polyglot.util.InternalCompilerError;
import accrue.infoflow.analysis.SecurityPolicy;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.ConstraintSet;
import accrue.infoflow.analysis.constraints.ConstraintSolution;
import accrue.infoflow.analysis.constraints.ConstraintSolution.LeastSolution;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import accrue.infoflow.analysis.constraints.SecurityPolicyVariable;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.securityPolicy.ErasurePolicy;
import cryptoerase.securityPolicy.FlowPolicy;
import cryptoerase.securityPolicy.KeyKind;
import cryptoerase.securityPolicy.KindPolicy;
import cryptoerase.securityPolicy.LevelPolicy;

public class CEConstraintSet extends ConstraintSet {
    public CEConstraintSet(IFConsAnalysisFactory factory) {
        super(factory);
    }

    @Override
    protected ConstraintSolution createLeastSolution(Set<ConstraintKind> kinds) {
        return new CELeastSolution(factory, this, kinds);
    }

    static class CELeastSolution extends LeastSolution {
        protected final CESecurityPolicyFactory secPolFactory;

        public CELeastSolution(IFConsAnalysisFactory factory,
                ConstraintSet constraints, Set<ConstraintKind> kinds) {
            super(factory, constraints, kinds);
            this.secPolFactory =
                    (CESecurityPolicyFactory) factory.securityPolicyFactory();

            this.abortOnFirstUnsatisfiedConstraint = false;
        }

        @Override
        protected boolean satisfyConstraint(Constraint c) {
            if (c instanceof NoConditionConstraint) {
                return satisfyConstraint((NoConditionConstraint) c);
            }
            if (c instanceof EncryptionConstraint) {
                return satisfyConstraint((EncryptionConstraint) c);
            }
            if (c instanceof DecryptionConstraint) {
                return satisfyConstraint((DecryptionConstraint) c);
            }
            return super.satisfyConstraint(c);

        }

        protected boolean satisfyConstraint(EncryptionConstraint c) {
            CESecurityPolicy keyPol = (CESecurityPolicy) subst(c.keyPol());
            CESecurityPolicy plaintextPol =
                    (CESecurityPolicy) subst(c.plaintextPol());
            CESecurityPolicy encResultPol =
                    (CESecurityPolicy) subst(c.encResultPol());

            // require PUBKEY(pk){p} with plaintextPol <= pk and p <= encResultPol

            if (!(keyPol.kindPol() instanceof KeyKind)) {
                // it's not a key kind, and we can't satisfy it, since we don't know what
                // policy pk to use :(
                addError("Couldn't satisfy encryption constraint " + c,
                         "Wasn't a key: " + keyPol);
                return false;
            }
            KeyKind kk = (KeyKind) keyPol.kindPol();
            if (!kk.isPublicKey()) {
                // it's a private key. Wrong kind of key!
                addError("Couldn't satisfy encryption constraint " + c,
                         "Wasn't a public key: " + keyPol);
                return false;
            }

            FlowPolicy keyBound = kk.keyBound();
            if (!plaintextPol.isBottom()
                    && !plaintextPol.flowPol().leq(keyBound)) {
                // currently plaintextPol <= pk is not satisfied.
                // but we can't raise pk, as it is invariant
                addError("Couldn't satisfy encryption constraint " + c,
                         "Didn't have plaintextPol <= keyBound: "
                                 + plaintextPol.flowPol() + " <= " + keyBound);
                return false;
            }

            if (!keyPol.isBottom()
                    && !keyPol.flowPol().leq(encResultPol.flowPol())) {
                // currently p <= encResultPol is not satisfied.
                // raise the variables in encResultPol
                Set<SecurityPolicyVariable> vars = c.encResultPol().variables();
                if (vars.isEmpty()) {
                    // no variables to set!
                    // We can't satisfy this constraint     
                    addError("Couldn't satisfy encryption constraint " + c,
                             keyPol.flowPol() + " <= " + (encResultPol));
                    return false;
                }
                for (SecurityPolicyVariable var : vars) {
                    SecurityPolicy bound =
                            subst(var).upperBound(CESecurityPolicy.create(KindPolicy.OTHER,
                                                                          keyPol.flowPol()));
                    if (!subst(var).equals(bound)) {
                        soln.put(var, bound);
                        // add every constraint that may now be invalidated by changing var
                        this.enqueueConstraints(constraints.getInvalidIfIncreased(var));
                    }
                }

                // check that it is now satisfied.
                CESecurityPolicy newKeyPol =
                        (CESecurityPolicy) subst(c.keyPol());
                CESecurityPolicy newEncResultPol =
                        (CESecurityPolicy) subst(c.encResultPol());

                boolean sat =
                        newKeyPol.flowPol().leq(newEncResultPol.flowPol());
                if (!sat) {
                    addError("Couldn't satisfy encryption constraint " + c,
                             keyPol.flowPol() + " <= " + (encResultPol));
                }
                return sat;

            }

            return true;

        }

        protected boolean satisfyConstraint(DecryptionConstraint c) {
            CESecurityPolicy keyPol = (CESecurityPolicy) subst(c.keyPol());
            CESecurityPolicy decResultPol =
                    (CESecurityPolicy) subst(c.decResultPol());

            // keyPol must be a PRIVKEY(pk){p} with pk  <= decResultPol and p <= decResultPol 

            if (!(keyPol.kindPol() instanceof KeyKind)) {
                // it's not a key kind, and we can't satisfy it, since we don't know what
                // policy pk to use :(
                addError("Couldn't satisfy decryption constraint " + c,
                         "Wasn't a key: " + keyPol);
                return false;
            }
            KeyKind kk = (KeyKind) keyPol.kindPol();
            if (kk.isPublicKey()) {
                // it's a public key. Wrong kind of key!
                addError("Couldn't satisfy decryption constraint " + c,
                         "Wasn't a public key: " + keyPol);
                return false;
            }

            FlowPolicy keyBound = kk.keyBound();

            if (!keyBound.leq(decResultPol.flowPol())) {
                // currently pk <= decResultPol is not satisfied.
                // raise the variables in encResultPol
                raiseVars(c.decResultPol().variables(),
                          CESecurityPolicy.create(KindPolicy.OTHER,
                                                  kk.keyBound()),
                          c);

                // check that it is now satisfied.
                CESecurityPolicy newKeyPol =
                        (CESecurityPolicy) subst(c.keyPol());
                KeyKind newKk = (KeyKind) newKeyPol.kindPol();
                FlowPolicy newKeyBound = newKk.keyBound();

                CESecurityPolicy newDecResultPol =
                        (CESecurityPolicy) subst(c.decResultPol());

                boolean sat = newKeyBound.leq(newDecResultPol.flowPol());
                if (!sat) {
                    addError("Couldn't satisfy decryption constraint " + c,
                             keyPol.flowPol() + " <= " + (newDecResultPol));
                    return false;
                }
            }

            if (!keyPol.flowPol().leq(decResultPol.flowPol())) {
                // currently p <= decResultPol is not satisfied.
                // raise the variables in encResultPol
                raiseVars(c.decResultPol().variables(),
                          CESecurityPolicy.create(KindPolicy.OTHER,
                                                  keyPol.flowPol()),
                          c);

                // check that it is now satisfied.
                CESecurityPolicy newKeyPol =
                        (CESecurityPolicy) subst(c.keyPol());
                CESecurityPolicy newDecResultPol =
                        (CESecurityPolicy) subst(c.decResultPol());

                boolean sat =
                        newKeyPol.flowPol().leq(newDecResultPol.flowPol());
                if (!sat) {
                    addError("Couldn't satisfy decryption constraint " + c,
                             keyPol.flowPol() + " <= " + (newDecResultPol));
                    return false;
                }
            }

            return true;

        }

        protected boolean satisfyConstraint(NoConditionConstraint c) {
            if (c.var() == null) {
                if (!c.satisfies(c.polToCheck())) {
                    addError("Couldn't satisfy " + c,
                             "Declared policy shouldn't have an erasure condition.");

                    return false;
                }
                else {
                    return true;
                }
            }
            CESecurityPolicy current = (CESecurityPolicy) subst(c.var());

            CESecurityPolicy fixed = removeConditions(current, c.condition());

            if (fixed.equals(current)) {
                // was already satisfied!
                return true;
            }

            soln.put(c.var(), fixed);
            // add every constraint that may now be invalidated by changing var
            this.enqueueConstraints(constraints.getInvalidIfIncreased(c.var()));

            // check that it is now satisfied.
            if (!c.satisfies(fixed)) {
                addError("Couldn't satisfy " + c, "I have no idea why.");
                return false;
            }
            return true;

        }

        private CESecurityPolicy removeConditions(CESecurityPolicy cp,
                AccessPath condition) {
            if (cp == CESecurityPolicy.ERROR || cp == CESecurityPolicy.BOTTOM) {
                return cp;
            }
            return cp.flowPol(removeConditions(cp.flowPol(), condition));
        }

        private FlowPolicy removeConditions(FlowPolicy p, AccessPath condition) {
            if (p instanceof LevelPolicy) {
                return p;
            }
            if (p instanceof ErasurePolicy) {
                ErasurePolicy ep = (ErasurePolicy) p;
                if (condition == null || ep.condition().mayOverlap(condition)) {
                    // p shouldn't contain a condition!
                    // Get rid of it by finding an upper bound of the sides
                    return removeConditions(ep.initialPolicy()
                                              .upperBound(ep.finalPolicy()),
                                            condition);
                }
                FlowPolicy newInit =
                        removeConditions(ep.initialPolicy(), condition);
                FlowPolicy newFinal =
                        removeConditions(ep.finalPolicy(), condition);
                if (newInit.equals(ep.initialPolicy())
                        && newFinal.equals(ep.finalPolicy())) {
                    // nothing changed
                    return p;
                }
                return this.secPolFactory.erasurePolicy(newInit,
                                                        ep.condition(),
                                                        newFinal);
            }
            throw new InternalCompilerError("Don't know how to deal with " + p
                    + " " + (p == null ? "" : p.getClass()));

        }
    }

}
