package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchAbstract:
                    ==========================    ===================
                         SwitchCaseBranch         SwitchDefaultBranch
*/

public class Switch extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^switch";

    private final List<SwitchAbstractBranch> mBranchList;

    public Switch(final List<SwitchAbstractBranch> branchList) {
        mBranchList = branchList;
    }

    protected void exec() throws SetlException {
        for (final SwitchAbstractBranch br : mBranchList) {
            if (br.evalConditionToBool()) {
                br.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs) + "switch {" + Environment.getEndl();
        for (final SwitchAbstractBranch br : mBranchList) {
            result += br.toString(tabs + 1);
        }
        result += Environment.getLineStart(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER);

        final SetlList branchList = new SetlList(mBranchList.size());
        for (final SwitchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static Switch termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList                    branches    = (SetlList) term.firstMember();
            final List<SwitchAbstractBranch>  branchList  = new ArrayList<SwitchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(SwitchAbstractBranch.valueToSwitchAbstractBranch(v));
            }
            return new Switch(branchList);
        }
    }
}

