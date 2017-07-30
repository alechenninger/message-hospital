package messagehospital.api.http;

import messagehospital.api.domain.CorrelationId;
import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.ReportId;
import messagehospital.api.domain.ReportRepository;
import messagehospital.api.domain.SystemName;

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
import java.util.Collections;
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
  public ResponseEntity<Stream<ReportDto>> search(@RequestBody SearchRequest request) {
    Set<SystemName> producers = request.producers.stream()
        .map(SystemName::new)
        .collect(Collectors.toSet());

    Set<MessageType> messageTypes = request.messageTypes.stream()
        .map(MessageType::new)
        .collect(Collectors.toSet());

    Set<Map<String, String>> headers = request.headers == null
        ? Collections.emptySet()
        : new HashSet<>(request.headers);

    return ResponseEntity.ok(repository
        .search(producers, messageTypes, headers, request.index, request.max)
        .map(r -> new ReportDto(r, zone)));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<ReportIdDto> report(@RequestBody ReportRequest request) {
    ReportId id = repository.nextReportId();

    Report report = new Report(id, new CorrelationId(request.correlationId),
        request.timestamp.toInstant(), request.resubmitUri,
        new SystemName(request.system), request.dataFormat, request.data,
        request.headers, new SystemName(request.producerSystem),
        new MessageType(request.messageType),
        new HashSet<>(request.errorTypes), request.errorMessage, request.errorDetail);

    repository.save(report);

    return ResponseEntity.ok(new ReportIdDto(id));
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class ReportRequest {
    public String correlationId;
    public OffsetDateTime timestamp;
    public String resubmitUri;
    public String messageType;
    public String data;
    public Map<String, String> headers;
    public String producerSystem;
    public String dataFormat;
    public String system;
    public String service;
    public Collection<String> errorTypes;
    public String errorMessage;

    public ReportRequest correlationId(String correlationId) {
      this.correlationId = correlationId;
      return this;
    }

    public ReportRequest timestamp(OffsetDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public ReportRequest resubmitUri(String resubmitUri) {
      this.resubmitUri = resubmitUri;
      return this;
    }

    public ReportRequest messageType(String messageType) {
      this.messageType = messageType;
      return this;
    }

    public ReportRequest data(String data) {
      this.data = data;
      return this;
    }

    public ReportRequest headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public ReportRequest producerSystem(String producerSystem) {
      this.producerSystem = producerSystem;
      return this;
    }

    public ReportRequest dataFormat(String dataFormat) {
      this.dataFormat = dataFormat;
      return this;
    }

    public ReportRequest system(String system) {
      this.system = system;
      return this;
    }

    public ReportRequest service(String service) {
      this.service = service;
      return this;
    }

    public ReportRequest errorTypes(Collection<String> errorTypes) {
      this.errorTypes = errorTypes;
      return this;
    }

    public ReportRequest errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public ReportRequest errorDetail(String errorDetail) {
      this.errorDetail = errorDetail;
      return this;
    }

    public String errorDetail;

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
  public static class SearchRequest {
    public Collection<String> producers;
    public Collection<String> messageTypes;
    public Collection<Map<String, String>> headers;
    public int index = 0;
    public int max = 20;

    public SearchRequest producers(Collection<String> producers) {
      this.producers = producers;
      return this;
    }

    public SearchRequest messageTypes(Collection<String> messageTypes) {
      this.messageTypes = messageTypes;
      return this;
    }

    public SearchRequest headers(Collection<Map<String, String>> headers) {
      this.headers = headers;
      return this;
    }

    public SearchRequest index(int index) {
      this.index = index;
      return this;
    }

    public SearchRequest max(int max) {
      this.max = max;
      return this;
    }
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class ReportDto {
    public String id;
    public String correlationId;
    public OffsetDateTime timestamp;
    public String resubmitUri;
    public String producerSystem;
    public String system;
    public String messageType;
    public String dataFormat;
    public String data;
    public Collection<String> errorTypes;
    public String errorMessage;
    public String errorDetail;
    public Map<String, String> headers;

    ReportDto() {}

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

    @Override
    public String toString() {
      return "ReportDto{" +
          "id='" + id + '\'' +
          ", correlationId='" + correlationId + '\'' +
          ", timestamp=" + timestamp +
          ", resubmitUri='" + resubmitUri + '\'' +
          ", producerSystem='" + producerSystem + '\'' +
          ", system='" + system + '\'' +
          ", messageType='" + messageType + '\'' +
          ", dataFormat='" + dataFormat + '\'' +
          ", data='" + data + '\'' +
          ", errorTypes=" + errorTypes +
          ", errorMessage='" + errorMessage + '\'' +
          ", errorDetail='" + errorDetail + '\'' +
          ", headers=" + headers +
          '}';
    }
  }

  public static class ReportIdDto {
    public String id;

    public ReportIdDto(String id) {
      this.id = id;
    }

    public ReportIdDto(ReportId id) {
      this(id.toString());
    }

    @Override
    public String toString() {
      return "ReportIdDto{" +
          "id='" + id + '\'' +
          '}';
    }
  }
}
