package com.redhat.it.qadmin2.api.infrastructure;

import com.redhat.it.qadmin2.api.domain.CorrelationId;
import com.redhat.it.qadmin2.api.domain.MessageType;
import com.redhat.it.qadmin2.api.domain.Report;
import com.redhat.it.qadmin2.api.domain.ReportId;
import com.redhat.it.qadmin2.api.domain.ReportRepository;
import com.redhat.it.qadmin2.api.domain.SystemName;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
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
  public Stream<Report> search(Set<SystemName> producers, Set<MessageType> types,
      Set<Map<String, String>> headerCombos, int index, int max) {
    Session session = entityManager.unwrap(Session.class);
    CriteriaBuilder criteria = session.getCriteriaBuilder();
    CriteriaQuery<Report> q = criteria.createQuery(Report.class);

    Root<Report> root = q.from(Report.class);
    q.select(root);
    q.distinct(true);

    List<Predicate> predicates = new ArrayList<>();

    if (!producers.isEmpty()) {
      predicates.add(root.get("producer").in(producers));
    }

    if (!types.isEmpty()) {
      predicates.add(root.get("messageType").in(types));
    }

    if (!headerCombos.isEmpty()) {
      Predicate[] headerPredicates = new Predicate[headerCombos.size()];
      int i = 0;

      for (Iterator<Map<String, String>> it = headerCombos.iterator(); it.hasNext(); i++) {
        Map<String, String> combo = it.next();

        for (Map.Entry<String, String> entry : combo.entrySet()) {
          MapJoin<Report, String, String> headerJoin = root.joinMap("headers", JoinType.LEFT);
          headerPredicates[i] = criteria.and(
              criteria.equal(headerJoin.key(), entry.getKey()),
              criteria.equal(headerJoin.value(), entry.getValue()));
        }
      }

      predicates.add(criteria.or(headerPredicates));
    }

    q.where(predicates.stream().toArray(Predicate[]::new));

    return session.createQuery(q).stream().peek(r -> log.info("result: {}", r));
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
