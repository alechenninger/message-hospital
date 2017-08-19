package messagehospital.api.infrastructure;

import messagehospital.api.domain.CorrelationId;
import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.ReportId;
import messagehospital.api.domain.ReportRepository;
import messagehospital.api.domain.ServiceName;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Repository
public class HibernateReportRepository implements ReportRepository {
  private final EntityManager entityManager;

  private static final Logger log = LoggerFactory.getLogger(HibernateReportRepository.class);

  @Autowired
  public HibernateReportRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public ReportId nextReportId() {
    return new ReportId(UUID.randomUUID().toString());
  }

  @Override
  public Stream<Report> reportsByCorrelationId(CorrelationId id) {
    Session session = entityManager.unwrap(Session.class);
    return session.createQuery("select r from Report r where r.correlationId = :id", Report.class)
        .setParameter("id", id)
        .stream();
  }

  @Override
  public Stream<Report> allReports() {
    Session session = entityManager.unwrap(Session.class);
    return session.createQuery("select r from Report r", Report.class).stream();
  }

  @Override
  public Stream<Report> search(Set<ServiceName> consumers, Set<MessageType> types,
      Set<Map<String, String>> headerCombos, int index, int max) {
    if (max == 0) {
      return Stream.empty();
    }

    StringBuilder qstr = new StringBuilder();
    qstr.append("select distinct r from Report r ");

    StringJoiner joins = new StringJoiner(" ");
    StringJoiner ands = new StringJoiner(" and ");
    List<Consumer<Query<Report>>> queryConfigs = new ArrayList<>();

    if (!headerCombos.isEmpty()) {
      int headerIndex = 0;
       StringJoiner comboOr = new StringJoiner(" or ");

      for (Map<String, String> headerCombo : headerCombos) {
        StringJoiner comboAnd = new StringJoiner(" and ");

        for (Iterator<Map.Entry<String, String>> iterator = headerCombo.entrySet().iterator();
             iterator.hasNext();
             headerIndex++) {
          Map.Entry<String, String> e = iterator.next();

          joins.add("left join r.message.headers h" + headerIndex);

          comboAnd.add("KEY(h" + headerIndex + ") = '" + e.getKey() + "' and " +
              "h" + headerIndex + " = '" + e.getValue() + "'");
        }

        comboOr.add("(" + comboAnd.toString() + ")");
      }

      ands.add("(" + comboOr + ")");
    }

    if (!consumers.isEmpty()) {
      ands.add("r.consumer in :consumers");
      queryConfigs.add(q -> q.setParameter("consumers", consumers));
    }

    if (!types.isEmpty()) {
      ands.add("r.message.type in :types");
      queryConfigs.add(q -> q.setParameter("types", types));
    }

    Session session = entityManager.unwrap(Session.class);
    Query<Report> query = session.createQuery(
        qstr.append(joins).append(" where ").append(ands).toString(),
        Report.class);
    queryConfigs.forEach(p -> p.accept(query));
    query.setMaxResults(max);
    query.setFirstResult(index);

    return query.stream();
  }

  @Override
  public Set<String> headerNamesByType(MessageType type) {
    return null;
  }

  @Override
  public void save(Report report) {
    entityManager.persist(report);
  }

  @Override
  public void removeReportsOlderThan(Instant time) {

  }
}
