package pro.documentum.persistence.common.query.expression.functions;

import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.query.expression.DQLField;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLUpper extends DQLFieldFunc {

    public static final String FUNC = "UPPER";

    public static final String RIGHT_FUNC = "toUpperCase";

    private DQLUpper(final String field) {
        super(String.format("%s(%s)", FUNC, field));
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        DQLField field = process(invokeExpr, evaluator, FUNC, RIGHT_FUNC);
        if (field == null) {
            return null;
        }
        return new DQLUpper(field.getText());
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLUpper.evaluate(expression, evaluator);
            }
        };
    }

}
