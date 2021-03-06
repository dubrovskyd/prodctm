package pro.documentum.persistence.common.query.expression.literals.nulls;

import pro.documentum.persistence.common.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNullString extends DQLLiteral<String> {

    DQLNullString() {
        super(null, DQLNull.NULLSTRING);
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
