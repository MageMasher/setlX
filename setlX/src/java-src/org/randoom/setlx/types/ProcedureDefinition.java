package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.ReturnException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.List;

// This class represents a function definition

/*
grammar rule:
procedureDefinition
    : 'procedure' '(' procedureParameters ')' '{' block '}'
    ;

implemented here as:
                      ===================         =====
                          mParameters          mStatements
*/

public class ProcedureDefinition extends Value {
    // functional character used in terms
    public  final static String  FUNCTIONAL_CHARACTER = "^procedure";
    // execute this function continuesly in debug mode until it returns. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sStepThroughFunction = false;
    // continue execution of this function in debug mode until it returns. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sFinishFunction      = false;

    protected final List<ParameterDef> mParameters;  // parameter list
    protected final Block              mStatements;  // statements in the body of the definition

    public ProcedureDefinition(final List<ParameterDef> parameters, final Block statements) {
        mParameters = parameters;
        mStatements = statements;
    }

    public ProcedureDefinition clone() {
        // this value can not be changed once set => no harm in returning the original
        return this;
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isProcedure() {
        return SetlBoolean.TRUE;
    }

    /* function call */

    public Value call(final List<Expr> exprs, final List<Value> args) throws SetlException {
        if (mParameters.size() != args.size()) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with a different number of parameters " +
                "(" + mParameters.size() + ")."
            );
        }

        // save old scope
        final VariableScope oldScope = VariableScope.getScope();
        // create new scope used for the function call
        VariableScope.setScope(oldScope.cloneFunctions());

        // put arguments into inner scope
        for (int i = 0; i < mParameters.size(); ++i) {
            mParameters.get(i).assign(args.get(i));
        }

        // results of call to procedure
              Value           result      = Om.OM;
        final WriteBackAgent  wba         = new WriteBackAgent(mParameters.size());
        final boolean         stepThrough = sStepThroughFunction;

        try {
            try {
                if (stepThrough) {
                    Environment.setDebugStepThroughFunction(false);
                    Environment.setDebugModeActive(false);
                }

                // execute, e.g. perform real procedure call
                mStatements.execute();
            } catch (ReturnException re) {
                // get result
                result = re.getValue();
            } // let all other exceptions through

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            for (int i = 0; i < mParameters.size(); ++i) {
                final ParameterDef param = mParameters.get(i);
                if (param.getType() == ParameterDef.READ_WRITE) {
                    // value of parameter after execution
                    final Value postValue = param.getValue();
                    // expression used to fill parameter before execution
                    final Expr  preExpr   = exprs.get(i);
                    /* if possible the WriteBackAgent will set the variable used in this
                       expression to its postExecution state in the outer environment    */
                    wba.add(preExpr, postValue);
                }
            }

        } finally { // make sure scope is always reset
            // restore old scope
            VariableScope.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            wba.writeBack();

            if (stepThrough || sFinishFunction) {
                Environment.setDebugModeActive(true);
                if (sFinishFunction) {
                    Environment.setDebugFinishFunction(false);
                }
            }
        }

        return result;
    }

    /* string and char operations */

    public String toString(final int tabs) {
        String result = "procedure (";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    public String toString() {
        return toString(0);
    }

    /* term operations */

    public Value toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(mParameters.size());
        for (final ParameterDef param: mParameters) {
            paramList.addMember(param.toTerm());
        }
        result.addMember(paramList);

        result.addMember(mStatements.toTerm());

        return result;
    }

    public static ProcedureDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
            return new ProcedureDefinition(parameters, block);
        }
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(final Value v){
        if (this == v) { // from using clone()
            return 0;
        } else if (v instanceof ProcedureDefinition) {
            final ProcedureDefinition other = (ProcedureDefinition) v;
            if (this instanceof PreDefinedFunction && other instanceof PreDefinedFunction) {
                final PreDefinedFunction _this  = (PreDefinedFunction) this;
                final PreDefinedFunction _other = (PreDefinedFunction) other;
                return _this.getName().compareTo(_other.getName());
            } else {
                int cmp = mParameters.toString().compareTo(other.mParameters.toString());
                if (cmp != 0) {
                    return cmp;
                }
                return cmp = mStatements.toString().compareTo(other.mStatements.toString());
            }
        } else if (v == Infinity.POSITIVE) {
            // only +Infinity is bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }
}

