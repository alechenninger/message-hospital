package com.redhat.it.messagehospital.api.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CorrelatedReports {
  private CorrelationId id;

  private List<Report> reports;

  private SystemName producer;

  private Set<MessageType> type;

  protected CorrelatedReports() {}

  public CorrelatedReports(CorrelationId id) {
    this.id = id;
  }

  public CorrelationId id() {
    return id;
  }

  public List<Report> reports() {
    return reports;
  }

  public SystemName producer() {
    return producer;
  }

  public Set<MessageType> types() {
    return reports.stream().map(Report::messageType).collect(Collectors.toSet());
  }

  public Map<String, String> headers() {
    return reports().stream()
        .flatMap(r -> r.headers().entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
