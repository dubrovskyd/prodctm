package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

import pro.documentum.model.jpa.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class LowerQueryTest extends AbstractQueryTest {

    @Test
    public void testUpper1() throws Exception {
        String q = str(jpa(DmUser.class, "LOWER(userName) = 'dmadmin'"));
        assertThat(q, endsWith("WHERE LOWER(this.user_name)='dmadmin'"));
    }

    @Test
    public void testUpper2() throws Exception {
        String q = str(jpa(DmUser.class, "userName.toLowerCase() = 'dmadmin'"));
        assertThat(q, endsWith("WHERE LOWER(this.user_name)='dmadmin'"));
    }

}
