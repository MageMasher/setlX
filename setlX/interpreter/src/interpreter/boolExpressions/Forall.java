package interpreter.boolExpressions;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

import java.util.List;

public class Forall extends Expr {
    private Iterator mIterator;
    private BoolExpr mBoolExpr;

    private class Exec implements IteratorExecutionContainer {
        private BoolExpr    mBoolExpr;
        public  SetlBoolean mResult;
        public  Environment mEnv;

        public Exec (BoolExpr boolExpr) {
            mBoolExpr = boolExpr;
            mResult   = SetlBoolean.TRUE;
            mEnv      = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mBoolExpr.eval();
            if (mResult == SetlBoolean.FALSE) {
                mEnv = Environment.getEnv();        // save state in which mBoolExpr is false
                throw new BreakException("forall"); // stop iteration
            }
        }
    }

    public Forall(Iterator iterator, BoolExpr boolExpr) {
        mIterator = iterator;
        mBoolExpr = boolExpr;
    }

    public SetlBoolean evaluate() throws SetlException {
        Exec e = new Exec(mBoolExpr);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.FALSE) {
            // retore state in which mBoolExpr is false
            Environment.setEnv(e.mEnv);
        }

        return e.mResult;
    }

    public String toString(int tabs) {
        return "forall (" + mIterator.toString(tabs) + " | " + mBoolExpr.toString(tabs) + ")";
    }
}
