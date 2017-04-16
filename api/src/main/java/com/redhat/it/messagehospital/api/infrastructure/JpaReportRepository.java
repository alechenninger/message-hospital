package com.redhat.it.messagehospital.api.infrastructure;

import com.redhat.it.messagehospital.api.domain.CorrelatedReports;
import com.redhat.it.messagehospital.api.domain.CorrelationId;
import com.redhat.it.messagehospital.api.domain.MessageType;
import com.redhat.it.messagehospital.api.domain.Report;
import com.redhat.it.messagehospital.api.domain.ReportId;
import com.redhat.it.messagehospital.api.domain.ReportRepository;
import com.redhat.it.messagehospital.api.domain.SystemName;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class JpaReportRepository implements ReportRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public ReportId nextReportId() {
    return new ReportId(UUID.randomUUID().toString());
  }

  @Override
  public CorrelatedReports reportsByCorrelationId(CorrelationId id) {
    return null;
  }

  @Override
  public Stream<Report> allReports() {
    CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
    CriteriaQuery<Report> query = criteria.createQuery(Report.class);
    Root<Report> root = query.from(Report.class);
    query.select(root);
    // TODO: iterate?
    return entityManager.createQuery(query).getResultList().stream();

  }

  @Override
  public Stream<Report> search(Set<SystemName> producers, Set<MessageType> types) {


    return Stream.empty();
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
