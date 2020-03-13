package com.vedrax.math;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FunctionType {
    MANUAL_ENTRY("entry",false),
    ABS("abs",false),
    ADD("add",true),
    CBRT("cbrt",false),
    CEIL("ceil",false),
    CUBE("cube",false),
    DIVIDE("divide",true),
    POW("pow",true),
    EXP("exp",false),
    EXPM1("expm1",false),
    FIX("fix",false),
    FLOOR("floor",false),
    GCD("gcd",true),
    HYPOT("hypot",true),
    LCM("lcm",true),
    LOG("log",false),
    LOG10("log10",false),
    LOG1P("log1p",false),
    LOG2("log2",false),
    MOD("mod",true),
    MULTIPLY("multiply",true),
    NORM("norm",false),
    NTH_ROOT("nthRoot",false),
    NTH_ROOTS("nthRoots",false),
    ROUND("round",false),
    SIGN("sign",false),
    SQRT("sqrt",false),
    SQUARE("square",false),
    SUBTRACT("subtract",true),
    UNARY_MINUS("unaryMinus",false),
    UNARY_PLUS("unaryPlus",false),
    XGCD("xgcd",true),
    COMBINATIONS("combinations",true),
    COMPARE("compare",true),
    COMPARE_NATURAL("compareNatural",true),
    EQUAL("equal",true),
    LARGER("larger",true),
    LARGER_EQ("largerEq",true),
    SMALLER_EQ("smallerEq",true),
    UNEQUAL("unequal",true),
    MAD("mad",true),
    MAX("max",true),
    MEAN("mean",true),
    MEDIAN("median",true),
    MIN("min",true),
    STD("std",true),
    SUM("sum",true),
    VARIANCE("variance",true);

    @Getter
    private String name;

    @Getter
    private boolean multiple;
}
