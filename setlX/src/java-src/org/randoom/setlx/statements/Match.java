package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' anyExpr ')' '{' ('case' exprList ':' block | 'case' '[' listOfVariables '|' variable ']' ':' block | 'case' '{' listOfVariables '|' variable '}' ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchMatchAbstract:
                  ====          =========================      =====================================================   =====================================================    ===================
                  mExpr              MatchCaseBranch                         MatchSplitListBranch                                     MatchSplitSetBranch                       MatchDefaultBranch
*/

public class Match extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^match";

    private final Expr                        mExpr;
    private final List<MatchAbstractBranch>   mBranchList;

    public Match(final Expr expr, final List<MatchAbstractBranch> branchList) {
        mExpr       = expr;
        mBranchList = branchList;
    }

    protected void exec() throws SetlException {
        final Value term = mExpr.eval().toTerm();
        for (final MatchAbstractBranch br : mBranchList) {
            final MatchResult result = br.matches(term);
            if (result.isMatch()) {
                // put all matching variables into current scope
                result.setAllBindings();
                // execute statements
                br.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs) + "match (" + mExpr.toString(tabs) + ") {" + Environment.getEndl();
        for (final MatchAbstractBranch br : mBranchList) {
            result += br.toString(tabs + 1);
        }
        result += Environment.getLineStart(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(mExpr.toTerm());

        final SetlList branchList = new SetlList(mBranchList.size());
        for (MatchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static Match termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr                        expr        = TermConverter.valueToExpr(term.firstMember());
            final SetlList                    branches    = (SetlList) term.lastMember();
            final List<MatchAbstractBranch>   branchList  = new ArrayList<MatchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(MatchAbstractBranch.valueToMatchAbstractBranch(v));
            }
            return new Match(expr, branchList);
        }
    }
}

