package messagehospital.api.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.ReportId;
import messagehospital.api.domain.ReportRepository;
import messagehospital.api.domain.ServiceName;
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

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
    Set<ServiceName> consumers = request.consumers.stream()
        .map(ServiceName::new)
        .collect(Collectors.toSet());

    Set<MessageType> messageTypes = request.messageTypes.stream()
        .map(MessageType::new)
        .collect(Collectors.toSet());

    Set<Map<String, String>> headers = request.headers == null
        ? Collections.emptySet()
        : new HashSet<>(request.headers);

    return ResponseEntity.ok(repository
        .search(consumers, messageTypes, headers, request.index, request.max)
        .map(r -> new ReportDto(r, zone)));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<ReportIdDto> report(@RequestBody ReportRequest request) throws UnsupportedEncodingException {
    ReportId id = repository.nextReportId();

    Report report = new Report(id,
        request.timestamp.toInstant(),
        new ServiceName(request.consumer),
        new Report.Message(
            new MessageType(request.messageType),
            new Report.Message.Data(request.dataFormat, request.data.getBytes("UTF-8")),
            request.headers),
        new Report.Exception(
            new ArrayList<>(request.errorTypes),
            request.errorMessage,
            request.errorDetail));

    repository.save(report);

    return ResponseEntity.ok(new ReportIdDto(id));
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class ReportRequest {
    public OffsetDateTime timestamp;
    public String messageType;
    public String data;
    public Map<String, String> headers;
    public String dataFormat;
    public String consumer;
    public Collection<String> errorTypes;
    public String errorMessage;

    public ReportRequest timestamp(OffsetDateTime timestamp) {
      this.timestamp = timestamp;
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

    public ReportRequest dataFormat(String dataFormat) {
      this.dataFormat = dataFormat;
      return this;
    }

    public ReportRequest consumer(String consumer) {
      this.consumer = consumer;
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
          ", timestamp=" + timestamp +
          ", messageType='" + messageType + '\'' +
          ", data='" + data + '\'' +
          ", headers=" + headers +
          ", dataFormat='" + dataFormat + '\'' +
          ", consumer='" + consumer + '\'' +
          ", errorTypes=" + errorTypes +
          ", errorMessage='" + errorMessage + '\'' +
          ", errorDetail='" + errorDetail + '\'' +
          '}';
    }
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class SearchRequest {
    public Collection<String> consumers;
    public Collection<String> messageTypes;
    public Collection<Map<String, String>> headers;
    public int index = 0;
    public int max = 20;

    public SearchRequest consumers(Collection<String> producers) {
      this.consumers = producers;
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
    public OffsetDateTime timestamp;
    public String consumer;
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
      timestamp = report.timestamp().atOffset(zone);
      consumer = Objects.toString(report.consumer(), null);
      messageType = Objects.toString(report.message().type(), null);
      dataFormat = Objects.toString(report.message().data().mimeType(), null);
      data = report.message().data().dataAsUtf8();
      errorTypes = report.exception().typeHierarchy();
      errorMessage = report.exception().shortMessage();
      errorDetail = report.exception().longMessage();
      headers = report.message().headers();
    }

    @Override
    public String toString() {
      return "ReportDto{" +
          "id='" + id + '\'' +
          ", timestamp=" + timestamp +
          ", consumer='" + consumer + '\'' +
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
