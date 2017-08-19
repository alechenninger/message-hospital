package messagehospital.api.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import messagehospital.api.domain.CorrelationId;
import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.ServiceName;
import messagehospital.api.testsupport.HibernateInMemoryDb;

import org.hamcrest.Matchers;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "unchecked"})
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
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> {
      repository.save(report);
    });

    List<Report> results = repository.search(
        Collections.emptySet(), Collections.emptySet(), Collections.singleton(
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        0, 10).collect(Collectors.toList());

    assertEquals(1, results.size());
    assertEquals(report.id(), results.get(0).id());
  }

  @Test
  public void searchReportRequiresAllHeadersInCombinationToMatch() throws Throwable {
    Report report = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> repository.save(report));

    List<Report> results = repository.search(
        Collections.emptySet(),
        Collections.emptySet(),
        Collections.singleton(new HashMap<String, String>() {{
          put("header1", "value1");
          put("header2", "not the right value");
        }}),
        0, 10)
        .collect(Collectors.toList());

    assertEquals(0, results.size());
  }

  @Test
  public void searchesReportsForMultipleHeaderCombinations() throws Throwable {
    Report report1 = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    Report report2 = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header3", "value3");
              put("header4", "value4");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

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

  @Test
  public void findsReportsByMatchingConsumer() throws Throwable {
    Report report = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> repository.save(report));

    List<Report> bySysA = repository.search(
        Sets.newHashSet(new ServiceName("sysA")),
        Collections.emptySet(),
        Collections.emptySet(),
        0, 10)
        .collect(Collectors.toList());

    assertEquals(1, bySysA.size());
  }

  @Test
  public void doesNotReturnReportsWhichDoNotMatchAnyConsumers() throws Throwable {
    Report report = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> repository.save(report));

    List<Report> bySysBOrC = repository.search(
        Sets.newHashSet(new ServiceName("sysB"), new ServiceName("sysC")),
        Collections.emptySet(),
        Collections.emptySet(),
        0, 10)
        .collect(Collectors.toList());

    assertEquals(0, bySysBOrC.size());
  }

  @Test
  public void searchesReportsByMessageType() throws Throwable {
    Report report = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> repository.save(report));

    List<Report> bySysA = repository.search(
        Collections.emptySet(),
        Sets.newHashSet(new MessageType("user")),
        Collections.emptySet(),
        0, 10)
        .collect(Collectors.toList());

    assertEquals(1, bySysA.size());
  }

  @Test
  public void searchesReportsByAllOfMessageTypeConsumerServicesAndHeaders() throws Throwable {
    Report report = new Report(
        repository.nextReportId(),
        Instant.now(),
        new ServiceName("sysA"),
        new Report.Message(
            new MessageType("user"),
            new Report.Message.Data(
                "application/xml",
                "<message></message>".getBytes()),
            new HashMap<String, String>() {{
              put("header1", "value1");
              put("header2", "value2");
            }}),
        new Report.Exception(
            Arrays.asList("IOException"),
            "error msg",
            "error detail"));

    db.tx(() -> repository.save(report));

    List<Report> bySysA = repository.search(
        Sets.newHashSet(new ServiceName("sysA")),
        Sets.newHashSet(new MessageType("user")),
        Sets.newHashSet(ImmutableMap.of("header1", "value1", "header2", "value2")),
        0, 10)
        .collect(Collectors.toList());

    assertEquals(1, bySysA.size());
  }

  @Test
  public void retrievesHeaderNamesByMessageType() throws Throwable {
    db.tx(() -> repository.saveAll(Stream.of(
        new Report(
            repository.nextReportId(),
            Instant.now(),
            new ServiceName("sysA"),
            new Report.Message(
                new MessageType("user"),
                new Report.Message.Data(
                    "application/xml",
                    "<message></message>".getBytes()),
                new HashMap<String, String>() {{
                  put("header1", "value1");
                  put("header2", "value2");
                }}),
            new Report.Exception(
                Arrays.asList("IOException"),
                "error msg",
                "error detail")),
        new Report(
            repository.nextReportId(),
            Instant.now(),
            new ServiceName("sysA"),
            new Report.Message(
                new MessageType("user"),
                new Report.Message.Data(
                    "application/xml",
                    "<message></message>".getBytes()),
                new HashMap<String, String>() {{
                  put("header1", "value1");
                  put("header3", "value3");
                }}),
            new Report.Exception(
                Arrays.asList("IOException"),
                "error msg",
                "error detail")),
        new Report(
            repository.nextReportId(),
            Instant.now(),
            new ServiceName("sysA"),
            new Report.Message(
                new MessageType("order"),
                new Report.Message.Data(
                    "application/xml",
                    "<message></message>".getBytes()),
                new HashMap<String, String>() {{
                  put("header4", "value1");
                  put("header5", "value3");
                }}),
            new Report.Exception(
                Arrays.asList("IOException"),
                "error msg",
                "error detail"))
        )));

    try (Stream<String> headerNames = repository.headerNamesByType(new MessageType("user"))) {
      assertThat(
          headerNames.collect(Collectors.toList()),
          Matchers.containsInAnyOrder("header1", "header2", "header3"));
    }
  }
}
