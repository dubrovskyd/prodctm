package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

import pro.documentum.model.jpa.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FieldQueryTest extends AbstractQueryTest {

    @Test
    public void testFieldEq() throws Exception {
        String q = str(jpa(DmUser.class, "userName = userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name=this.user_login_name"));
    }

    @Test
    public void testFieldNotEq() throws Exception {
        String q = str(jpa(DmUser.class, "userName <> userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name!=this.user_login_name"));
    }

    @Test
    public void testFieldGt() throws Exception {
        String q = str(jpa(DmUser.class, "userName > userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name>this.user_login_name"));
    }

    @Test
    public void testFieldGtEq() throws Exception {
        String q = str(jpa(DmUser.class, "userName >= userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name>=this.user_login_name"));
    }

    @Test
    public void testFieldLt() throws Exception {
        String q = str(jpa(DmUser.class, "userName < userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name<this.user_login_name"));
    }

    @Test
    public void testFieldLtEq() throws Exception {
        String q = str(jpa(DmUser.class, "userName <= userLoginName"));
        assertThat(q, endsWith("WHERE this.user_name<=this.user_login_name"));
    }

}
