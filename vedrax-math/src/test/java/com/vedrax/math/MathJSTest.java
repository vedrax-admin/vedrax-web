package com.vedrax.math;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MathJSTest {

  @Before
  public void setUp() {
  }

  @Test
  public void givenEvaluationWithScope_whenEval_thenGetResult() {

    MathJS mathJS = new MathJS();

    Map<String, Object> scope = new HashMap<>();
    scope.put("a", 1);
    scope.put("b", 3);

    String result = mathJS.eval("sum(a,b)*2", scope);

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("8.0");

    scope.put("a", Arrays.asList("1", "2", "3"));

    result = mathJS.eval("sum(a)", scope);

    assertThat(result).isEqualTo("6.0");
  }

  @Test
  public void givenEvaluationWithoutScope_whenEval_thenGetResult() {
    MathJS mathJS = new MathJS();

    mathJS.eval("1+1", null);
  }

  @Test(expected = NullPointerException.class)
  public void givenNoEvaluation_whenEval_thenThrowsException() {
    MathJS mathJS = new MathJS();
    mathJS.eval(null, null);
  }

}
