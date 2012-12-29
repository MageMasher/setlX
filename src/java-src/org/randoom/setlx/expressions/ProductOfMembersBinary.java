package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;


// grammar rule:
// reduce
//     : factor ([..] | '*/' factor)*
//     ;
//
// implemented here as:
//       ======              ======
//      mNeutral           mCollection

public class ProductOfMembersBinary extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^multiplyMembersBinary";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    private final Expr mNeutral;
    private final Expr mCollection;

    public ProductOfMembersBinary(final Expr neutral, final Expr collection) {
        mNeutral    = neutral;
        mCollection = collection;
    }

    protected Value evaluate(final State state) throws SetlException {
        return mCollection.eval(state).productOfMembers(state, mNeutral.eval(state));
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
    ) {
        mCollection.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mNeutral.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mNeutral.appendString(sb, tabs);
        sb.append(" */ ");
        mCollection.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mNeutral.toTerm(state));
        result.addMember(mCollection.toTerm(state));
        return result;
    }

    public static ProductOfMembersBinary termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr neutral    = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            Expr collection = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new ProductOfMembersBinary(neutral, collection);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}
