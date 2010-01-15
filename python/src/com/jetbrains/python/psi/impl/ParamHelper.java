package com.jetbrains.python.psi.impl;

import com.jetbrains.python.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter-related things that should not belong directly to PyParameter.
 */
public class ParamHelper {

  /**
   * Runs a {@link ParamWalker ParamWalker} down the array of parameters, recursively descending into tuple parameters.
   * If the array is from PyParamaterList.getParameters(), parameters are visited in the order of textual appearance
   * @param params where to walk
   * @param walker the walker with callbacks.
   */
  public static void walkDownParamArray(PyParameter[] params, ParamWalker walker) {
    int last = params.length-1;
    int i = 0;
    for (PyParameter param : params) {
      PyTupleParameter t_param = param.getAsTuple();
      if (t_param != null) {
        PyParameter[] nested_params = t_param.getContents();
        PyTupleParameter tpar = (PyTupleParameter)param;
        walker.enterTupleParameter(tpar, (i==0), (i == last));
        walkDownParamArray(nested_params, walker);
        walker.leaveTupleParameter(tpar, (i==0), (i == last));
      }
      else {
        final PyNamedParameter namedParameter = param.getAsNamed();
        if (namedParameter != null) {
          walker.visitNamedParameter(namedParameter, (i==0), (i == last));
        }
        else {
          walker.visitSingleStarParameter((PySingleStarParameter) param, (i == 0), (i == last));
        }
      }
      i += 1;
    }
  }

  public interface ParamWalker {
    /**
     * Is called when a tuple parameter is encountered, before visiting any parameters nested in it.
     * @param param the parameter
     * @param first true iff it is the first in the list
     * @param last true it is the last in the list
     */
    void enterTupleParameter(PyTupleParameter param, boolean first, boolean last);

    /**
     * Is called when all nested parameters of a given tuple parameter are visited.
     * @param param the parameter
     * @param first true iff it is the first in the list
     * @param last true it is the last in the list
     */
    void leaveTupleParameter(PyTupleParameter param, boolean first, boolean last);

    /**
     * Is called when a named parameter is encountered.
     * @param param the parameter
     * @param first true iff it is the first in the list
     * @param last true it is the last in the list
     */
    void visitNamedParameter(PyNamedParameter param, boolean first, boolean last);

    void visitSingleStarParameter(PySingleStarParameter param, boolean first, boolean last);
  }

  public static abstract class ParamVisitor implements ParamWalker {
    public void enterTupleParameter(PyTupleParameter param, boolean first, boolean last) { }

    public void leaveTupleParameter(PyTupleParameter param, boolean first, boolean last) { }

    public void visitNamedParameter(PyNamedParameter param, boolean first, boolean last) { }

    public void visitSingleStarParameter(PySingleStarParameter param, boolean first, boolean last) { }
  }

  public static StringBuilder appendParameterList(PyParameterList plist, final StringBuilder target) {
    final String COMMA = ", ";
    target.append("(");
    walkDownParamArray(
      plist.getParameters(),
      new ParamWalker() {
        public void enterTupleParameter(PyTupleParameter param, boolean first, boolean last) {
          target.append("(");
        }

        public void leaveTupleParameter(PyTupleParameter param, boolean first, boolean last) {
          target.append(")");
          if (! last) target.append(COMMA);
        }

        public void visitNamedParameter(PyNamedParameter param, boolean first, boolean last) {
          target.append(param.getRepr(true));
          if (! last) target.append(COMMA);
        }

        public void visitSingleStarParameter(PySingleStarParameter param, boolean first, boolean last) {
          target.append('*');
          if (!last) target.append(COMMA);
        }
      }
    );
    target.append(")");
    return target;
  }

  public static List<PyNamedParameter> collectNamedParameters(PyParameterList plist) {
    final List<PyNamedParameter> result = new ArrayList<PyNamedParameter>(10); // a random 'enough'
    walkDownParamArray(
      plist.getParameters(),
      new ParamVisitor() {
        public void visitNamedParameter(PyNamedParameter param, boolean first, boolean last) {
          result.add(param);
        }
      }
    );
    return result;
  }

}
