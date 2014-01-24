package cryptoerase.possibleSetConditions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.CodeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;
import accrue.analysis.interprocanalysis.AbstractLocation;
import cryptoerase.ast.CEExt_c;
import cryptoerase.ast.CEProcedureCallExt;

public class SetConditionsVisitor extends NodeVisitor {
    protected final SetConditionsAnalysisUtil autil;
    protected final TypeSystem ts;

    protected final Set<AbstractLocation> setConds =
            new HashSet<AbstractLocation>();

    public SetConditionsVisitor(SetConditionsAnalysisUtil autil) {
        super();
        this.autil = autil;
        if (this.autil == null) throw new InternalCompilerError("null autil");
        this.ts = autil.typeSystem();
        if (this.ts == null) throw new InternalCompilerError("null ts");
    }

    public SetConditionsVisitor(TypeSystem ts) {
        super();
        if (ts == null) throw new InternalCompilerError("null ts");
        this.autil = null;
        this.ts = ts;
    }

    protected SetConditionsVisitor(SetConditionsVisitor v) {
        super();
        this.autil = v.autil;
        this.ts = v.ts;
    }

    public SetConditionsAbsVal performVisit(CodeNode n) {
        n.visit(this);
        return new SetConditionsAbsVal(this.setConds);
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {

        if (n instanceof Call) {
            process((Call) n);
        }
        else if (n instanceof New) {
            process((New) n);
        }
        else if (n instanceof ConstructorCall) {
            process((ConstructorCall) n);
        }
        else if (CEExt_c.ext(n).isConditionSet()) {
            // it's a set condition!
            Assign a = (Assign) n;
            if (a.left() instanceof Local) {
                // ignore local conditions, we're only interested in fields
            }
            else if (a.left() instanceof Field) {
                Field f = (Field) a.left();
                this.setConds.addAll(autil.abstractLocations(f));
            }
            else {
                throw new InternalCompilerError("Can't handle " + n);
            }
        }
        return n;
    }

    protected void process(Call n) {
        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        MethodInstance mi = n.methodInstance();
        SetConditionsAbsVal scs = autil.call(mi, n);
        this.setConds.addAll(scs.setConditions());
        ext.recordSetConditionsResults(this.autil.currentContext(),
                                       scs.setConditions());
    }

    protected void process(ConstructorCall n) {
        SetConditionsAbsVal scs =
                autil.constructorCall(n.constructorInstance(), n);
        Set<AbstractLocation> setConds = scs.setConditions();

        if (n.kind().equals(ConstructorCall.SUPER)) {
            SetConditionsAbsVal t = null;
            try {
                // Add in the results from all of the initializers
                for (SetConditionsAbsVal v : autil.analyzeInitializers(SetConditionsAbsVal.EMPTY)
                                                  .allItems()) {
                    t = (t == null) ? v : t.upperBound(v);
                }
            }
            catch (SemanticException e) {
                throw new InternalCompilerError(e);
            }

            if (!t.setConditions().isEmpty()) {
                setConds = new LinkedHashSet<AbstractLocation>();
                setConds.addAll(scs.setConditions());
                setConds.addAll(t.setConditions());
            }
        }

        this.setConds.addAll(setConds);

        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        ext.recordSetConditionsResults(this.autil.currentContext(), setConds);
    }

    protected void process(New n) {
        SetConditionsAbsVal scs = autil.call(n.constructorInstance(), n);
        this.setConds.addAll(scs.setConditions());
        CEProcedureCallExt ext = (CEProcedureCallExt) CEExt_c.ext(n);
        ext.recordSetConditionsResults(this.autil.currentContext(),
                                       scs.setConditions());
    }

    public Set<AbstractLocation> setConditions() {
        return this.setConds;
    }

}
