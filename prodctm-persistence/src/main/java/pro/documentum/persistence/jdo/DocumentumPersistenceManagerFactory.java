package pro.documentum.persistence.jdo;

import java.util.Map;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumPersistenceManagerFactory extends
        JDOPersistenceManagerFactory {

    private static final long serialVersionUID = 1947522885236246186L;

    public DocumentumPersistenceManagerFactory() {
        super();
    }

    public DocumentumPersistenceManagerFactory(
            final PersistenceUnitMetaData pumd, final Map<?, ?> overrideProps) {
        super(pumd, overrideProps);
    }

    public DocumentumPersistenceManagerFactory(final Map<?, ?> props) {
        super(props);
    }

    @Override
    protected JDOPersistenceManager newPM(
            final JDOPersistenceManagerFactory jdoPmf, final String userName,
            final String password) {
        return new DocumentumPersistenceManager(jdoPmf, userName, password);
    }

}
