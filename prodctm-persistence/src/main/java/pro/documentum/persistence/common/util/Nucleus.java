package pro.documentum.persistence.common.util;

import java.lang.reflect.Array;
import java.util.Collection;

import org.datanucleus.Configuration;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.persistence.common.IDocumentumCredentialsHolder;
import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.DfObjects;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Nucleus {

    public static final String LOGIN_PROPERTY = PropertyNames.PROPERTY_CONNECTION_USER_NAME;

    public static final String PASSWORD_PROPERTY = PropertyNames.PROPERTY_CONNECTION_PASSWORD;

    private Nucleus() {
        super();
    }

    public static IDfPersistentObject newObject(final IDfSession session,
            final Object id, final AbstractClassMetaData cmd,
            final StoreManager storeMgr) {
        try {
            return Sessions.inTransaction(session,
                    new IDfSessionInvoker<IDfPersistentObject>() {
                        @Override
                        public IDfPersistentObject invoke(
                                final IDfSession session) throws DfException {
                            return doNewObject(session, id, cmd, storeMgr);
                        }
                    });
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    private static IDfPersistentObject doNewObject(final IDfSession session,
            final Object id, final AbstractClassMetaData cmd,
            final StoreManager storeMgr) throws DfException {
        Table table = storeMgr.getStoreDataForClass(cmd.getFullClassName())
                .getTable();
        String objectId = Nucleus.getObjectId(id);
        return DfObjects.newObject(session, table.getName(), objectId);
    }

    public static void save(final IDfPersistentObject object) {
        try {
            Sessions.inTransaction(object.getSession(),
                    new IDfSessionInvoker<Void>() {
                        @Override
                        public Void invoke(final IDfSession session)
                            throws DfException {
                            doSave(object);
                            return null;
                        }
                    });
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    private static void doSave(final IDfPersistentObject object)
        throws DfException {
        Logger.debug("Trying to save object {0}", object.getObjectId());
        if (!object.isDirty()) {
            Logger.debug("Object {0} is not dirty, skipping",
                    object.getObjectId());
            return;
        }
        if (object instanceof IDfSysObject) {
            IDfSysObject sysObject = (IDfSysObject) object;
            Logger.debug("Object {0} is sysobject, saving lock, "
                    + "current lock owner is: {1}", object.getObjectId(),
                    sysObject.getLockOwner());
            sysObject.saveLock();
            return;
        }
        object.save();
    }

    public static String getObjectId(final Object id) {
        String objectId = null;
        if (IdentityUtils.isDatastoreIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForDatastoreIdentity(id);
        } else if (IdentityUtils.isSingleFieldIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForSingleFieldIdentity(id);
        }
        if (DfIdUtil.isNotObjectId(objectId)) {
            throw new NucleusObjectNotFoundException("Invalid objectId: "
                    + objectId);
        }
        return objectId;
    }

    public static IDfPersistentObject getObject(final IDfSession session,
            final Object id) {
        return getObject(session, getObjectId(id));
    }

    public static IDfPersistentObject getObject(final IDfSession session,
            final String objectId) {
        try {
            return session.getObject(DfId.valueOf(objectId));
        } catch (DfObjectNotFoundException ex) {
            throw DfExceptions.notFoundException(ex);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static Object newArray(final Class<?> arrayType,
            final Collection<?> values) {
        Class<?> componentClass = arrayType;
        if (componentClass.isArray()) {
            componentClass = arrayType.getComponentType();
        }
        Object array = Array.newInstance(componentClass, values.size());
        int i = 0;
        for (Object value : values) {
            Array.set(array, i, value);
            i++;
        }
        return array;
    }

    public static IDfLoginInfo extractLoginInfo(final ExecutionContext ec) {
        if (ec == null) {
            return null;
        }
        Object owner = ec.getOwner();
        IDfLoginInfo loginInfo = null;
        if (owner instanceof IDocumentumCredentialsHolder) {
            loginInfo = getLoginInfo((IDocumentumCredentialsHolder) owner);
        }
        if (loginInfo == null) {
            loginInfo = getLoginInfo(ec);
        }
        if (loginInfo == null) {
            loginInfo = getLoginInfo(ec.getNucleusContext().getConfiguration());
        }
        return loginInfo;
    }

    private static IDfLoginInfo getLoginInfo(
            final IDocumentumCredentialsHolder credentialsHolder) {
        String userName = credentialsHolder.getUserName();
        String password = credentialsHolder.getPassword();
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

    private static IDfLoginInfo getLoginInfo(final Configuration cnf) {
        String userName = cnf.getStringProperty(LOGIN_PROPERTY);
        String password = cnf.getStringProperty(PASSWORD_PROPERTY);
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

    private static IDfLoginInfo getLoginInfo(final ExecutionContext ec) {
        String userName = ec.getStringProperty(LOGIN_PROPERTY);
        String password = ec.getStringProperty(PASSWORD_PROPERTY);
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

}
