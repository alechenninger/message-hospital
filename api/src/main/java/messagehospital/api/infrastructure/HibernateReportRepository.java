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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    Session session = entityManager.unwrap(Session.class);
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<Report> criteria = builder.createQuery(Report.class);

    Root<Report> root = criteria.from(Report.class);
    criteria.select(root);
    criteria.distinct(true);

    List<Predicate> predicates = new ArrayList<>();

    if (!consumers.isEmpty()) {
      predicates.add(root.get("consumer").in(consumers));
    }

    if (!types.isEmpty()) {
      predicates.add(root.get("message").get("type").in(types));
    }

    if (!headerCombos.isEmpty()) {
      Predicate[] headerPredicates = new Predicate[headerCombos.size()];
      int comboIndex = 0;

      for (Iterator<Map<String, String>> it = headerCombos.iterator(); it.hasNext(); comboIndex++) {
        Map<String, String> combo = it.next();
        Predicate[] comboPredicates = new Predicate[combo.size()];
        int headerIndex = 0;

        for (Iterator<Map.Entry<String, String>> iterator = combo.entrySet().iterator();
             iterator.hasNext();
             headerIndex++) {
          Map.Entry<String, String> entry = iterator.next();
          MapJoin<Report, String, String> headerJoin = root.joinMap("headers", JoinType.LEFT);
          comboPredicates[headerIndex] = builder.and(
              builder.equal(headerJoin.key(), entry.getKey()),
              builder.equal(headerJoin.value(), entry.getValue()));
        }

        headerPredicates[comboIndex] = builder.and(comboPredicates);
      }

      predicates.add(builder.or(headerPredicates));
    }

    criteria.where(predicates.toArray(new Predicate[predicates.size()]));

    Query<Report> query = session.createQuery(criteria);
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
