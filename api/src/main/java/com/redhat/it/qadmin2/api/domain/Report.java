package com.redhat.it.qadmin2.api.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Immutable
//@Table(indexes = {@Index(columnList = "correlation_id", unique = false)})
public class Report {

  @EmbeddedId
  private ReportId id;

  @Embedded
  @AttributeOverride(name = "rawId", column = @Column(name = "correlationId"))
  private CorrelationId correlationId;

  private Instant timestamp;

  /** As in message broker and queue. */
  private String resubmitUri;

  @Embedded
  @AttributeOverride(name = "name", column = @Column(name = "producerSystem"))
  private SystemName producer;

  @Embedded
  @AttributeOverride(name = "name", column = @Column(name = "system"))
  private SystemName system;

  @Embedded
  private MessageType messageType;

  private String service;

  private String dataFormat;

  private String data;

  /**
   * As in exception causes and hierarchy.ww
   */
  @ElementCollection
  private Set<String> errorTypes;

  private String errorMessage;

  private String errorDetail;

  @ElementCollection
  private Map<String, String> headers;

  protected Report() {}

  public Report(ReportId id, CorrelationId correlationId, Instant timestamp, String resubmitUri, SystemName system,
      String dataFormat, String data, Map<String, String> headers, SystemName producer, MessageType messageType, String service, Set<String> errorTypes, String errorMessage, String errorDetail) {
    this.correlationId = correlationId;
    this.id = id;
    this.timestamp = timestamp;
    this.resubmitUri = resubmitUri;
    this.system = system;
    this.dataFormat = dataFormat;
    this.data = data;
    this.headers = headers == null || headers.isEmpty()
        ? Collections.emptyMap()
        : Collections.unmodifiableMap(new HashMap<>(headers));
    this.producer = producer;
    this.messageType = messageType;
    this.service = service;
    this.errorTypes = errorTypes;
    this.errorMessage = errorMessage;
    this.errorDetail = errorDetail;
  }

  public ReportId id() {
    return id;
  }

  public CorrelationId correlationId() {
    return correlationId;
  }

  public String resubmitUri() {
    return resubmitUri;
  }

  public SystemName producer() {
    return producer;
  }

  public MessageType messageType() {
    return messageType;
  }

  public String service() {
    return service;
  }

  public String dataFormat() {
    return dataFormat;
  }

  public Set<String> errorTypes() {
    return errorTypes;
  }

  public String errorMessage() {
    return errorMessage;
  }

  public String errorDetail() {
    return errorDetail;
  }

  public Instant timestamp() {
    return timestamp;
  }

  public SystemName system() {
    return system;
  }

  public String data() {
    return data;
  }

  public Map<String, String> headers() {
    return headers;
  }

  @Override
  public String toString() {
    return "Report{" +
        "id=" + id +
        ", correlationId=" + correlationId +
        ", timestamp=" + timestamp +
        ", resubmitUri='" + resubmitUri + '\'' +
        ", producer=" + producer +
        ", system=" + system +
        ", messageType=" + messageType +
        ", service='" + service + '\'' +
        ", dataFormat='" + dataFormat + '\'' +
        ", data='" + data + '\'' +
        ", errorTypes=" + errorTypes +
        ", errorMessage='" + errorMessage + '\'' +
        ", errorDetail='" + errorDetail + '\'' +
        ", headers=" + headers +
        '}';
  }
}
