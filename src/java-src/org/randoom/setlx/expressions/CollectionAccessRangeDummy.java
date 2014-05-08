package org.randoom.setlx.expressions;

import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * this class implements a range token inside the parameters of a CollectionAccess.
 *
 * grammar rules:
 * collectionAccessParams
 *     : expr '..' expr?
 *     | [...]
 *     ;
 *
 *            ====
 */
public class CollectionAccessRangeDummy extends Expr {
    private final static String                     FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CollectionAccessRangeDummy.class);
    // precedence level in SetlX-grammar
    private final static int                        PRECEDENCE           = 9999;

    /**
     * Singleton VariableIgnore expression.
     */
    public  final static CollectionAccessRangeDummy CARD                 = new CollectionAccessRangeDummy();

    private CollectionAccessRangeDummy() {}

    @Override
    protected RangeDummy evaluate(final State state) {
        return RangeDummy.RD;
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" .. ");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    /**
     * Convert a term representing a CollectionAccessRangeDummy expression into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param term  Term to convert.
     * @return      Resulting expression of this conversion.
     */
    public static CollectionAccessRangeDummy termToExpr(final State state, final Term term) {
        return CARD;
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

