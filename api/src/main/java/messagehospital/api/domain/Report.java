package messagehospital.api.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Immutable
@Table(indexes = {@Index(columnList = "correlationId")})
public class Report {

  @EmbeddedId
  private ReportId id;

  @Embedded
  @AttributeOverride(name = "rawId", column = @Column(name = "correlationId"))
  private CorrelationId correlationId;

  private Instant timestamp;

  /** As in message broker and queue. */
  /*
  Other ways to think about this:

  Protocol for possibly "fixing" and continuing a message.

  So for example, might use HTTP with a particular URL pattern and schema.
  Or might use JMS for a particular connection url and destination.

  Inputs: message body, headers, correlation id, report id
  Contract: Replays message.

  It means a report must include a particular protocol for resubmit. Which means the app has to know
  about that protocol. This is a source of coupling, so should be plugable. It could be fancy with
  runtime configurations and templates and stuff but if its plugable that kind of thing could be
  added whenever. Could consider including some amount of this on the report (for example URL and
  body template). Then the app is very loosely coupled to how systems' resubmit may work at the cost
  of probably lots of redundant data in reports.

  Also think about security. Only certain roles should be able to resubmit certain messages. Who
  knows about these business rules? Not the app. Reporter might. So reporter should probably include
  roles in resubmit protocol. But again this is largely redundant info. What if roles change? Should
  it really be per report? If configuration, than these rules also need to be plugable.

  Plugable is really just another way of saying modular.
  */
  private String resubmitUri;

  @Embedded
  @AttributeOverride(name = "name", column = @Column(name = "producerSystem"))
  private SystemName producer;

  @Embedded
  @AttributeOverride(name = "name", column = @Column(name = "system"))
  private SystemName system;

  @Embedded
  private MessageType messageType;

  private String dataFormat;

  private String data;

  /**
   * As in exception causes and hierarchy.
   */
  @ElementCollection
  private Set<String> errorTypes;

  private String errorMessage;

  private String errorDetail;

  @ElementCollection
  private Map<String, String> headers;

  protected Report() {}

  public Report(ReportId id, CorrelationId correlationId, Instant timestamp, String resubmitUri, SystemName system,
      String dataFormat, String data, Map<String, String> headers, SystemName producer, MessageType messageType, Set<String> errorTypes, String errorMessage, String errorDetail) {
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
    this.errorTypes = errorTypes;
    this.errorMessage = errorMessage;
    this.errorDetail = errorDetail;
  }

  /**
   * Uniquely identifies the report.
   */
  public ReportId id() {
    return id;
  }

  /**
   * Correlates this report with other reports that share the same correlation id. Reports correlate
   * when they relate to the same business event or distributed transaction (for example a JMS
   * message ID or an HTTP X-Correlation-ID header).
   */
  // TODO: Should be eventId?
  public CorrelationId correlationId() {
    return correlationId;
  }

  // TODO: Rework this
  public String resubmitUri() {
    return resubmitUri;
  }

  /**
   * Identifies the best known origin system of the event represented by {@link #correlationId()}.
   */
  // or "publisher"? "sender"?
  // should this just be a header?
  public SystemName producer() {
    return producer;
  }

  /**
   * Identifies the kind of origin event represented by {@link #correlationId()}, for example
   * "UserCreated" or "PurchaseOrder."
   */
  // TODO: Message types plural?
  // TODO: event name instead?
  // should this just be a header?
  public MessageType messageType() {
    return messageType;
  }

  // should this just be a header?
  // consumer()?
  public SystemName system() {
    return system;
  }

  /**
   * A mime type for {@link #data()} such as "application/json" or
   * "application/vnd.mycompany.user+xml".
   */
  public String dataFormat() {
    return dataFormat;
  }

  // TODO: Bundle all error values together?
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

  // TODO: Is all data textual? Encapsulate together with dataFormat? class Data { ... }
  public String data() {
    return data;
  }

  /**
   * Searchable metadata about the event and/or the report. Each header's key and value are expected
   * to be small.
   */
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
        ", dataFormat='" + dataFormat + '\'' +
        ", data='" + data + '\'' +
        ", errorTypes=" + errorTypes +
        ", errorMessage='" + errorMessage + '\'' +
        ", errorDetail='" + errorDetail + '\'' +
        ", headers=" + headers +
        '}';
  }
}
