package messagehospital.api.infrastructure;

import static org.junit.Assert.assertEquals;

import messagehospital.api.domain.CorrelationId;
import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.SystemName;
import messagehospital.api.testsupport.HibernateInMemoryDb;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HibernateReportRepositoryTest {

  @ClassRule
  public static HibernateInMemoryDb db = new HibernateInMemoryDb("messagehospital.api");

  @Rule
  public HibernateInMemoryDb.Reset reset = db.resetRule();

  EntityManager entityManager = db.entityManager();
  HibernateReportRepository repository = new HibernateReportRepository(entityManager);

  @Test
  public void searchesReportsByHeadersInCombination() throws Throwable {
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

    List<Report> results = db.tx(() -> {
      repository.save(report);

      return repository.search(
          Collections.emptySet(), Collections.emptySet(), Collections.singleton(
              new HashMap<String, String>() {{
                put("header1", "value1");
                put("header2", "value2");
              }}),
          0, 10).collect(Collectors.toList());
    });

    assertEquals(1, results.size());
    assertEquals(report.id(), results.get(0).id());
  }

  @Test
  public void searchReportRequiresAllHeadersInCombinationToMatch() throws Throwable {
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

    List<Report> results = db.tx(() -> {
      repository.save(report);

      return repository.search(
          Collections.emptySet(), Collections.emptySet(), Collections.singleton(
              new HashMap<String, String>() {{
                put("header1", "value1");
                put("header2", "not the right value");
              }}),
          0, 10).collect(Collectors.toList());
    });

    assertEquals(0, results.size());
  }

  @Test
  public void searchesReportsForMultipleHeaderCombinations() throws Throwable {
    Report report1 = new Report(
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

    Report report2 = new Report(
        repository.nextReportId(),
        new CorrelationId("123"),
        Instant.now(),
        "resubmit://foo",
        new SystemName("sysA"),
        "application/xml",
        "<xml></xml>",
        new HashMap<String, String>() {{
          put("header3", "value3");
          put("header4", "value4");
        }},
        new SystemName("sysProducer"),
        new MessageType("user"),
        "should this just be system",
        new HashSet<>(Arrays.asList("IOException")),
        "error msg",
        "error detail"
    );

    List<Report> results = db.tx(() -> {
      repository.save(report1);
      repository.save(report2);

      return repository.search(
          Collections.emptySet(), Collections.emptySet(), Stream.of(
              new HashMap<String, String>() {{
                put("header1", "value1");
                put("header2", "value2");
              }},
              new HashMap<String, String>() {{
                put("header3", "value3");
                put("header4", "value4");
              }}).collect(Collectors.toSet()),
          0, 10).collect(Collectors.toList());
    });

    assertEquals(2, results.size());
    assertEquals(
        Stream.of(report1.id(), report2.id()).collect(Collectors.toSet()),
        results.stream().map(Report::id).collect(Collectors.toSet()));
  }
}
