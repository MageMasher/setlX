package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.AssignableExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the %= operator, on statement level.
 *
 * grammar rule:
 * assignmentOther
 *     : assignable ('%=' | [...] ) expr
 *     ;
 *
 * implemented here as:
 *       ==========                 ====
 *          lhs                     rhs
 */
public class ModuloAssignment extends StatementWithPrintableResult {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(ModuloAssignment.class);

    private final AssignableExpression lhs;
    private final Expr                 rhs;
    private       boolean              printAfterEval;

    /**
     * Create new ModuloAssignment.
     *
     * @param lhs Expression to assign to.
     * @param rhs Expression to evaluate.
     */
    public ModuloAssignment(final AssignableExpression lhs, final Expr rhs) {
        this.lhs            = lhs;
        this.rhs            = rhs;
        this.printAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterExecution() {
        printAfterEval = true;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value assigned = lhs.eval(state).moduloAssign(state, rhs.eval(state).clone());
        lhs.assignUncloned(state, assigned, FUNCTIONAL_CHARACTER);

        if (printAfterEval) {
            printResult(state, assigned);
        }

        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // first we evaluate lhs and rhs as usual
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        // then assign to lhs
        lhs.collectVariablesWhenAssigned(state, boundVariables, boundVariables, boundVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        lhs.appendString(state, sb, tabs);
        sb.append(" %= ");
        rhs.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing an ModuloAssignment into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static ModuloAssignment termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            if (lhs instanceof AssignableExpression) {
                return new ModuloAssignment((AssignableExpression) lhs, rhs);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

}

