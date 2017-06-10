package messagehospital.api.testsupport;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.rules.ExternalResource;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class HibernateInMemoryDb extends ExternalResource {
  private final String packagesToScan;

  private JDBCDataSource dataSource;
  private EntityManager entityManager;
  private EntityManagerFactory entityManagerFactory;

  public HibernateInMemoryDb(String packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  public JDBCDataSource dataSource() {
    return dataSource;
  }

  public EntityManager entityManager() {
    return entityManager;
  }

  public EntityManagerFactory entityManagerFactory() {
    return entityManagerFactory;
  }

  public Reset resetRule() {
    return new Reset(this);
  }

  public void tx(TransactionScript script) throws Throwable {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    try {
      script.run();
      tx.commit();
    } catch (Throwable t) {
      tx.rollback();
      throw t;
    }
  }

  public <T> T tx(TransactionScriptWithResult<T> script) throws Throwable {
    EntityTransaction tx = entityManager.getTransaction();
    T answer;
    tx.begin();
    try {
      answer = script.run();
      tx.commit();
    } catch (Throwable t) {
      tx.rollback();
      throw t;
    }
    return answer;
  }

  @Override
  protected void before() throws Throwable {
    DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
    dataSource = new JDBCDataSource();
    dataSource.setUrl("jdbc:hsqldb:mem:test");
    persistenceUnitManager.setDefaultDataSource(dataSource);
    persistenceUnitManager.setPackagesToScan(packagesToScan);
    persistenceUnitManager.preparePersistenceUnitInfos();
    HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
    entityManagerFactory = provider.createContainerEntityManagerFactory(
        persistenceUnitManager.obtainDefaultPersistenceUnitInfo(),
        new HashMap<String, String>() {{
          put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
          put("hibernate.hbm2ddl.auto", "create-drop");
        }});
    entityManager = entityManagerFactory.createEntityManager();
  }

  @Override
  protected void after() {
    entityManagerFactory.close();
  }

  public interface TransactionScript {
    void run() throws Throwable;
  }

  public interface TransactionScriptWithResult<T> {
    T run() throws Throwable;
  }

  public static class Reset extends ExternalResource {
    private final HibernateInMemoryDb db;

    public Reset(HibernateInMemoryDb db) {
      this.db = db;
    }

    @Override
    protected void after() {
      Connection connection = null;
      try {
        connection = db.dataSource.getConnection();
        try {
          try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
            connection.commit();
          }
        } catch (SQLException e) {
          connection.rollback();
          e.printStackTrace();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
