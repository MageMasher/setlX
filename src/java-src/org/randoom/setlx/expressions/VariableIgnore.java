package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/*
grammar rules:
assignable
    : variable   | idList      | '_'
    ;

value
    : list | set | atomicValue | '_'
    ;

this class implements an ignored variable inside an idList or expression:
                                 ===
*/

public class VariableIgnore extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    public  final static String         FUNCTIONAL_CHARACTER = "^variableIgnore";
    // precedence level in SetlX-grammar
    private final static int            PRECEDENCE           = 9999;

    public  final static VariableIgnore VI                   = new VariableIgnore();

    private VariableIgnore() { }

    protected IgnoreDummy evaluate(final State state) throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) { /* nothing to collect */ }

    // sets this expression to the given value
    public void assignUncloned(final State state, final Value v) {
        // or maybe it just does nothing
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v' */
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope) {
        return true;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("_");
    }

    /* term operations */

    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static VariableIgnore termToExpr(final Term term) {
        return VI;
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}
