package com.vedrax.math;

import org.apache.commons.lang3.Validate;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

import static com.vedrax.math.Util.*;

/**
 * Math API implementation
 */
public class MathJS {

  protected static String FILE_NAME = "math.min.js";

  protected ScriptEngine engine;

  public MathJS() {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      engine = manager.getEngineByName("nashorn");
      engine.eval(readResourceToString(FILE_NAME));
    } catch (Exception ex) {
      throw new IllegalArgumentException("math.min.js cannot be accessed");
    }
  }

  /**
   * Method for evaluating an math expression
   *
   * @param expr   the math expression to be evaluated
   * @param inputs the scope of the expression
   * @return the result of the expression
   */
  public String eval(String expr, Map<String, Object> inputs) {
    Validate.notNull(expr, "expression must be provided");

    evaluateScope(inputs);
    return evaluate("math.evaluate('" + expr + "', scope);");
  }

  /**
   * Helper method for adding scope to context
   *
   * @param inputs the provided scope
   */
  private void evaluateScope(Map<String, Object> inputs) {
    inputs = inputs == null ? new HashMap<>() : inputs;

    evaluate("var scope = {};");

    inputs.forEach((key, value) -> evaluate("scope." + key + "=" + convertObjectToString(value) + ";"));
  }

  /**
   * Helper method for evaluate an expression
   *
   * @param expression the provided expression
   * @return the result of the expression
   */
  private String evaluate(String expression) {
    try {
      return String.valueOf(engine.eval(expression));
    } catch (Exception e) {
      throw new IllegalArgumentException("Expression [" + expression + "] not valid");
    }
  }

}
