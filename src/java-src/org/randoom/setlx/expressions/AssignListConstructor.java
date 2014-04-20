package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.ExplicitList;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Implementation of expression creating an assignable list.
 *
 * grammar rules:
 * assignable
 *     : '[' explicitAssignList ']'
 *     | [..]
 *     ;
 *
 * explicitAssignList
 *     : assignable (',' assignable)*
 *     ;
 *
 * implemented here as:
 *           ==================
 *                  list
 */
public class AssignListConstructor extends AssignableExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(AssignListConstructor.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final ExplicitList list;

    /**
     * Constructor.
     *
     * @param constructor Constructor for explicit list of variables.
     */
    public AssignListConstructor(final ExplicitList constructor) {
        this.list = constructor;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final SetlList list = new SetlList();
        this.list.fillCollection(state, list);
        list.compress();
        return list;
    }

    @Override
    Value evaluateUnCloned(final State state) throws SetlException {
        return evaluate(state);
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        list.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public void collectVariablesWhenAssigned (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        list.collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public void assignUncloned(final State state, final Value v, final String context) throws SetlException {
        if (v instanceof IndexedCollectionValue) {
            list.assignUncloned(state, (IndexedCollectionValue) v, context);
        } else {
            throw new IncompatibleTypeException(
                "The value '" + v.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
    }

    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope, final String context) throws SetlException {
        if (v instanceof IndexedCollectionValue) {
            return list.assignUnclonedCheckUpTo(state, (IndexedCollectionValue) v, outerScope, context);
        } else {
            throw new IncompatibleTypeException(
                "The value '" + v.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("[");
        list.appendString(state, sb);
        sb.append("]");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList list = new SetlList();
        this.list.addToTerm(state, list);

        result.addMember(state, list);

        return result;
    }

    /**
     * Convert a term representing a AssignListConstructor into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static AssignListConstructor termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList     list = (SetlList) term.firstMember();
            final ExplicitList el   = ExplicitList.collectionValueToExplicitList(state, list);
            return new AssignListConstructor(el);
        }
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

