package cryptoerase.possibleSetConditions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.CodeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Special;
import polyglot.main.Report;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import accrue.AccrueExtensionInfo;
import accrue.analysis.ext.AccrueExt_c;
import accrue.analysis.ext.ExtConstructorDecl;
import accrue.analysis.interprocanalysis.AbstractLocation;
import accrue.analysis.interprocanalysis.AnalysisContext;
import accrue.analysis.interprocanalysis.AnalysisUnit;
import accrue.analysis.interprocanalysis.AnalysisUtil;
import accrue.analysis.interprocanalysis.ExitMap;
import accrue.analysis.interprocanalysis.WorkQueue;
import accrue.analysis.pointer.HContext;

public class SetConditionsAnalysisUtil extends
        AnalysisUtil<SetConditionsAbsVal> {

    public SetConditionsAnalysisUtil(WorkQueue<SetConditionsAbsVal> workQueue,
            AnalysisUnit currentAnalysisUnit, AccrueExtensionInfo extInfo) {
        super(workQueue, currentAnalysisUnit, extInfo);
    }

    @Override
    public ExitMap<SetConditionsAbsVal> analyze(CodeNode n,
            SetConditionsAbsVal before) throws SemanticException {
        SetConditionsVisitor v = new SetConditionsVisitor(this);
        return createExitMap(v.performVisit(n));
    }

    @Override
    public ExitMap<SetConditionsAbsVal> analyzeInitializers(
            SetConditionsAbsVal input) throws SemanticException {
        if (Report.should_report(this.workQueue.factory().reportTopics(), 3))
            Report.report(1, "Analyzing initializers in " + currentClass()
                    + " for " + analysisName());

        this.instanceInitializersChecked = true;
        List<CodeNode> toProcess =
                workQueue.registrar().getInstanceInitializers(currentClass());

        ExitMap<SetConditionsAbsVal> res = workQueue().emptyExitMap();
        SetConditionsAbsVal nt = input;
        if (Report.should_report(this.workQueue.factory().reportTopics(), 2))
            Report.report(1, "Processing " + toProcess.size()
                    + " instance initializers");

        for (CodeNode i : toProcess) {
            if (Report.should_report(this.workQueue.factory().reportTopics(), 3))
                Report.report(2, "Processing an instance initializer " + i);

            if (i.codeInstance().flags().isStatic()) {
                throw new InternalCompilerError("Should only have instance initializers here");
            }
            // analyze the initializer             
            AnalysisUnit calleeAUnit =
                    new AnalysisUnit(i,
                                     this.currentContext()
                                         .constructorContext((ConstructorInstance) this.currentAnalysisUnit()
                                                                                       .procedureDecl()
                                                                                       .procedureInstance()));
            res =
                    res.upperBound(this.workQueue.getAnalysisResult(calleeAUnit,
                                                                    nt,
                                                                    this));
            // Add the input back in here because PreciseExVisitor ignores it but
            // we still need to chain together the exception analyses of all of the 
            // initializers
            nt = nt.upperBound(res.normalTermination());
            res = res.removeNormalTermination();
        }

        return res.normalTermination(nt);
    }

    public SetConditionsAbsVal call(MethodInstance mi, Call n) {

        boolean isStaticCall = mi.flags().isStatic();
        if (!isStaticCall && n.target() instanceof Special) {
            Special sp = (Special) n.target();
            if (sp.kind() == Special.SUPER) {
                // not a dynamic dispatch!
                isStaticCall = true;
            }
        }
        if (isStaticCall) {
            return callNonVirtual(mi,
                                  workQueue.factory()
                                           .createAnalysisContext(this,
                                                                  mi,
                                                                  null,
                                                                  n));
        }
        else {
            // this is a virtual call.
            // find out all the method bodies this may call, and join them all together.
            Expr receiver = (Expr) n.target();
            Set<HContext> pointsTo = pointsTo(receiver);

            if (pointsTo == null) {
                throw new InternalCompilerError("No points to info for the receiver: "
                        + n);
            }

            // analyze all method implementations that may potentially be called, and
            // take an upper bound of the result.
            Set<AbstractLocation> setConds = new HashSet<AbstractLocation>();
            for (HContext o : pointsTo) {
                // analyze the implementing method
                Type receiverType = o.type();
                MethodInstance omi = null;
                if (receiverType != null) {
                    omi =
                            extInfo.typeSystem()
                                   .findImplementingMethod(receiverType.toClass(),
                                                           mi);
                }
                else {
                    //System.err.println(" *** no receiver for virtual method invocation to " + mi + " " + n.position());
                    throw new InternalCompilerError("no receiver for virtual method invocation to "
                                                            + mi,
                                                    n.position());
                }
                if (omi == null) {
                    // no implementing method!
                    // we'll see if we have a signature for mi
                    omi = mi;
                }
                SetConditionsAbsVal dfi =
                        callNonVirtual(omi,
                                       workQueue.factory()
                                                .createAnalysisContext(this,
                                                                       omi,
                                                                       o,
                                                                       n));
                setConds.addAll(dfi.setConditions());
            }

            if (pointsTo.isEmpty()) {
                // XXX UNCOMMENT THESE
                //System.err.println(" *** Receiver doesn't point to anything! May be a bug in the program? ");
                //System.err.println("     " + n + " " + mi.container()+"."+ mi.name() + "  at " + n.position());
                // let's just do the best we can...
                SetConditionsAbsVal dfi =
                        this.guessAnalysisForMissingCode(mi, null);
                setConds.addAll(dfi.setConditions());
            }

            return new SetConditionsAbsVal(setConds);
        }
    }

    /**
     * Process a constructor call from a new expression
     * @param ci
     * @param n
     * @return
     */
    public SetConditionsAbsVal call(ConstructorInstance ci, New n) {
        Set<HContext> pointsTo = pointsTo(n);
        if (pointsTo.size() > 1) {
            throw new InternalCompilerError("Invocation to constructor does not have unique pointsto: "
                                                    + pointsTo,
                                            n.position());
        }
        if (pointsTo.isEmpty()) {
            System.err.println(" n is " + n);
            throw new InternalCompilerError("Constructor invocation doesn't point to anything!",
                                            n.position());

        }
        return callNonVirtual(ci,
                              workQueue.factory()
                                       .createAnalysisContext(this,
                                                              ci,
                                                              pointsTo.iterator()
                                                                      .next(),
                                                              n));
    }

    /**
     * Process a constructor call to this() or super(), i.e., from within a constructor.
     * @param ci
     * @return
     */
    public SetConditionsAbsVal constructorCall(ConstructorInstance ci,
            ConstructorCall n) {
        ConstructorDecl cd =
                (ConstructorDecl) this.currentAnalysisUnit().codeNode();
        ExtConstructorDecl ext = (ExtConstructorDecl) AccrueExt_c.ext(cd);
        Set<HContext> pointsTo =
                pointsTo(ext.getThisNode(), this.currentContext(), this.extInfo);
        if (pointsTo.isEmpty()) {
            System.err.println("ci " + ci);
            System.err.println("n " + n);
            System.err.println("cd " + cd);
            System.err.println("ext " + ext);
            System.err.println("pointsTo " + pointsTo);
            System.err.println("ext.getThisNode() " + ext.getThisNode());
            System.err.println("this.currentContext() " + this.currentContext());
            System.err.println("this.extInfo " + this.extInfo);

            throw new InternalCompilerError("Constructor invocation doesn't point to anything!",
                                            n.position());

        }
        else {
            Set<AbstractLocation> setConds = new HashSet<AbstractLocation>();
            for (HContext o : pointsTo) {
                SetConditionsAbsVal dfi =
                        callNonVirtual(ci,
                                       workQueue.factory()
                                                .createAnalysisContext(this,
                                                                       ci,
                                                                       o,
                                                                       n));
                setConds.addAll(dfi.setConditions());
            }
            return new SetConditionsAbsVal(setConds);
        }
    }

    /**
     * A non-virtual call to a method instance
     * @return
     */
    private SetConditionsAbsVal callNonVirtual(ProcedureInstance pi,
            AnalysisContext calleeContext) {
        // get the implementing method body to add the argument names...
        ProcedureDecl pd = (ProcedureDecl) workQueue.registrar().getCode(pi);
        AnalysisUnit callee = null;

        SetConditionsAbsVal res;
        if (pd == null || pd.body() == null) {
            recordMissingCode(pi);
            return guessAnalysisForMissingCode(pi, calleeContext);
        }
        else {

            callee = new AnalysisUnit(pd, calleeContext);

            try {
                ExitMap<SetConditionsAbsVal> r =
                        this.workQueue.getAnalysisResult(callee,
                                                         SetConditionsAbsVal.EMPTY,
                                                         this);
                res = r.normalTermination();
            }
            catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected semantic exception",
                                                e);
            }

//            if (maybeRecursiveCall(callee)) {
//                r = this.handleRecursiveCall(r, PostDomDFI.EMPTY, callee);                
//            }

        }

        return res;

    }

    @SuppressWarnings("unchecked")
    private SetConditionsAbsVal guessAnalysisForMissingCode(
            ProcedureInstance pi, AnalysisContext calleeContext) {
        return SetConditionsAbsVal.EMPTY;
    }

}
