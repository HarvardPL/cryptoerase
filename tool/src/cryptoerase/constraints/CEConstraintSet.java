package cryptoerase.constraints;

import java.util.Set;

import polyglot.util.InternalCompilerError;
import accrue.infoflow.analysis.constraints.Constraint;
import accrue.infoflow.analysis.constraints.ConstraintKind;
import accrue.infoflow.analysis.constraints.ConstraintSet;
import accrue.infoflow.analysis.constraints.ConstraintSolution;
import accrue.infoflow.analysis.constraints.ConstraintSolution.LeastSolution;
import accrue.infoflow.analysis.constraints.IFConsAnalysisFactory;
import cryptoerase.CESecurityPolicyFactory;
import cryptoerase.securityPolicy.AccessPath;
import cryptoerase.securityPolicy.CESecurityPolicy;
import cryptoerase.securityPolicy.ErasurePolicy;
import cryptoerase.securityPolicy.FlowPolicy;
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
        }

        @Override
        protected boolean satisfyConstraint(Constraint c) {
            if (c instanceof NoConditionConstraint) {
                return satisfyConstraint((NoConditionConstraint) c);
            }
            return super.satisfyConstraint(c);

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
