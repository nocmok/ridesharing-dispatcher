package com.nocmok.orp.postgres.storage.util;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private List<Term> terms;

    public Filter() {
        this.terms = new ArrayList<>();
    }

    public Filter and(Condition condition) {
        if (!terms.isEmpty()) {
            terms.add(BinaryOpTerm.AND);
        }
        terms.add(new ConditionTerm(condition));
        return this;
    }

    public String getWhereStatement() {
        var result = new StringBuilder();
        terms.forEach(term -> result.append(term.getLiteral()));
        return result.toString();
    }

    private interface Term {
        String getLiteral();
    }

    private interface BinaryOpTerm extends Term {
        BinaryOpTerm AND = () -> "AND";
    }

    private static class ConditionTerm implements Term {
        private Condition condition;

        public ConditionTerm(Condition condition) {
            this.condition = condition;
        }

        @Override public String getLiteral() {
            return condition.getConditionStatement();
        }
    }
}
