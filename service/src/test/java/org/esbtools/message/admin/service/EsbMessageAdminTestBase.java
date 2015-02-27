package org.esbtools.message.admin.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.esbtools.message.admin.model.MetadataField;
import org.esbtools.message.admin.service.extractor.KeyExtractorUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class EsbMessageAdminTestBase {

    protected static EntityManagerFactory errorEmf;
    protected EntityManager errorEm;
    protected EsbMessageAdminServiceImpl service;

    @BeforeClass
    public static void createEntityManagerFactory() {
        errorEmf = Persistence.createEntityManagerFactory("EsbMessageAdminTestErrorPU");
    }

    @Before
    public void createEntityManager() {
        errorEm = errorEmf.createEntityManager();
        // Start a new transaction
        if (!errorEm.getTransaction().isActive()) {
            errorEm.getTransaction().begin();
        }
        service = new EsbMessageAdminServiceImpl();
        service.setEntityManager(errorEm);
        List<MetadataField> searchKeys = new ArrayList<MetadataField>();
        service.setKeyExtractor(new KeyExtractorUtil(searchKeys));
    }

    @After
    public void closeEntityManager() {
        if (errorEm.getTransaction().isActive()) {
            if (errorEm.getTransaction().getRollbackOnly()) {
                errorEm.getTransaction().rollback();
            } else {
                errorEm.getTransaction().commit();
            }
        }
        errorEm.close();
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        errorEmf.close();
    }

    protected EntityManager getEntityManager() {
        return errorEm;
    }

}