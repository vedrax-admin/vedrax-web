package com.vedrax.math;

import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.vedrax.math.Util.*;

/**
 * Math API implementation
 */
public class MathJS {

  protected static String FILE_NAME = "math.min.js";

  private final static Logger LOG = Logger.getLogger(MathJS.class.getName());

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

    //evaluateScope(inputs);
    return evaluate("math.evaluate('" + expr + "', " + initScope(inputs) + ");");
  }

  private String initScope(Map<String, Object> inputs) {
    if (CollectionUtils.isEmpty(inputs)) {
      return "{}";
    }

    return inputs.keySet().stream()
      .map(key -> key + ":" + inputs.get(key))
      .collect(Collectors.joining(", ", "{", "}"));
  }

  /**
   * Helper method for evaluate an expression
   *
   * @param expression the provided expression
   * @return the result of the expression
   */
  private String evaluate(String expression) {
    try {
      LOG.warning("EXPRESSION MATH JS: " + expression);
      String result = String.valueOf(engine.eval(expression));
      LOG.warning("RESULT EXPRESSION MATH JS: " + result);
      return result;
    } catch (Exception e) {
      throw new IllegalArgumentException("Expression [" + expression + "] not valid");
    }
  }

}
