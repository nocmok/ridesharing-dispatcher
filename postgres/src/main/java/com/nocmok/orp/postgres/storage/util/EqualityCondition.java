package com.nocmok.orp.postgres.storage.util;

public class EqualityCondition implements Condition {

    private final Object leftExpression;
    private final Object rightExpression;

    public EqualityCondition(Object leftExpression, Object rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public static EqualityCondition eq(Object leftExpression, Object rightExpression) {
        return new EqualityCondition(leftExpression, rightExpression);
    }

    @Override public String getConditionStatement() {
        return "(" + leftExpression + ") = (" + rightExpression + ")";
    }
}
