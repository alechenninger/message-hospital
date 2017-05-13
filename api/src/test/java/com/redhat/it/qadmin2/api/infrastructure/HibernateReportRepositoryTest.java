package com.redhat.it.qadmin2.api.infrastructure;

import static org.junit.Assert.assertEquals;

import com.redhat.it.qadmin2.api.domain.CorrelationId;
import com.redhat.it.qadmin2.api.domain.MessageType;
import com.redhat.it.qadmin2.api.domain.Report;
import com.redhat.it.qadmin2.api.domain.SystemName;
import com.redhat.it.qadmin2.api.testsupport.HibernateInMemoryDb;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class HibernateReportRepositoryTest {

  @ClassRule
  public static HibernateInMemoryDb db = new HibernateInMemoryDb("com.redhat.it.qadmin2.api");

  @Rule
  public HibernateInMemoryDb.Reset reset = db.resetRule();

  @Test
  public void searchesReportsByHeaders() {
    EntityManager entityManager = db.entityManager();
    HibernateReportRepository repository = new HibernateReportRepository(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();

    Report report = new Report(
        repository.nextReportId(),
        new CorrelationId("123"),
        Instant.now(),
        "resubmit://foo",
        new SystemName("sysA"),
        "application/xml",
        "<xml></xml>",
        new HashMap<String, String>() {{
          put("header1", "value1");
          put("header2", "value2");
        }},
        new SystemName("sysProducer"),
        new MessageType("user"),
        "should this just be system",
        new HashSet<>(Arrays.asList("IOException")),
        "error msg",
        "error detail"
    );

    repository.save(report);

    tx.commit();
    tx.begin();

    List<Report> resultList = repository.search(
        Collections.emptySet(), Collections.emptySet(), Collections.singleton(
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        0, 10).collect(Collectors.toList());

    tx.commit();

    assertEquals(1, resultList.size());
    assertEquals(report.id(), resultList.get(0).id());
  }
}
