package com.redhat.it.messagehospital.api.http;

import com.redhat.it.messagehospital.api.domain.CorrelationId;
import com.redhat.it.messagehospital.api.domain.MessageType;
import com.redhat.it.messagehospital.api.domain.Report;
import com.redhat.it.messagehospital.api.domain.ReportId;
import com.redhat.it.messagehospital.api.domain.ReportRepository;
import com.redhat.it.messagehospital.api.domain.SystemName;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/reports")
public class ReportResource {
  private static final Logger log = LoggerFactory.getLogger(ReportResource.class);

  @Autowired
  ReportRepository repository;

  @Autowired
  ZoneOffset zone;

  @GetMapping
  public ResponseEntity<Stream<ReportDto>> getAll() {
    return ResponseEntity.ok(
        repository.allReports().map(r -> new ReportDto(r, zone)));
  }

  @PostMapping("/search")
  public ResponseEntity<Stream<ReportDto>> search(SearchRequest request) {
    Set<SystemName> producers = request.producers.stream()
        .map(SystemName::new)
        .collect(Collectors.toSet());

    Set<MessageType> messageTypes = request.messageTypes.stream()
        .map(MessageType::new)
        .collect(Collectors.toSet());

    return ResponseEntity.ok(
        repository.search(producers, messageTypes).map(r -> new ReportDto(r, zone)));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<ReportId> report(@RequestBody ReportRequest request) {
    log.info("{}", request);

    ReportId id = repository.nextReportId();

    Report report = new Report(id, new CorrelationId(request.correlationId),
        request.timestamp.toInstant(), request.resubmitUri,
        new SystemName(request.system), request.dataFormat, request.data,
        request.headers, new SystemName(request.producerSystem),
        new MessageType(request.messageType), request.service,
        new HashSet<>(request.errorTypes), request.errorMessage, request.errorDetail);

    repository.save(report);

    return ResponseEntity.ok(id);
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  static class ReportRequest {
    String correlationId;
    OffsetDateTime timestamp;
    String resubmitUri;
    String messageType;
    String data;
    Map<String, String> headers;
    String producerSystem;
    String dataFormat;
    String system;
    String service;
    Collection<String> errorTypes;
    String errorMessage;
    String errorDetail;

    @Override
    public String toString() {
      return "ReportRequest{" +
          "correlationId='" + correlationId + '\'' +
          ", timestamp=" + timestamp +
          ", resubmitUri='" + resubmitUri + '\'' +
          ", messageType='" + messageType + '\'' +
          ", data='" + data + '\'' +
          ", headers=" + headers +
          ", producerSystem='" + producerSystem + '\'' +
          ", dataFormat='" + dataFormat + '\'' +
          ", system='" + system + '\'' +
          ", service='" + service + '\'' +
          ", errorTypes=" + errorTypes +
          ", errorMessage='" + errorMessage + '\'' +
          ", errorDetail='" + errorDetail + '\'' +
          '}';
    }
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  static class SearchRequest {
    Collection<String> producers;
    Collection<String> messageTypes;
    Map<String, String> headers;
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  static class ReportDto {
    String id;
    String correlationId;
    OffsetDateTime timestamp;
    String resubmitUri;
    String producerSystem;
    String system;
    String messageType;
    String dataFormat;
    String data;
    Collection<String> errorTypes;
    String errorMessage;
    String errorDetail;
    Map<String, String> headers;

    ReportDto(Report report, ZoneOffset zone) {
      id = report.id().toString();
      correlationId = report.correlationId().toString();
      timestamp = report.timestamp().atOffset(zone);
      resubmitUri = report.resubmitUri();
      producerSystem = Objects.toString(report.producer(), null);
      system = Objects.toString(report.system(), null);
      messageType = Objects.toString(report.messageType(), null);
      dataFormat = report.dataFormat();
      data = report.data();
      errorTypes = report.errorTypes();
      errorMessage = report.errorMessage();
      errorDetail = report.errorDetail();
      headers = report.headers();
    }
  }
}
