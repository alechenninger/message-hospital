package messagehospital.api.domain;

import org.hibernate.annotations.Immutable;
import org.springframework.util.MimeType;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Immutable
public class Report {

  @EmbeddedId
  private ReportId id;

  private Instant timestamp;

  @Embedded
  @AttributeOverride(name = "name", column = @Column(name = "consumer"))
  private ServiceName consumer;

  @Embedded
  private Message message;

  @Embedded
  private Exception exception;

  protected Report() {}

  public Report(ReportId id, Instant timestamp, ServiceName consumer, Message message,
      Exception exception) {
    this.id = id;
    this.timestamp = timestamp;
    this.consumer = consumer;
    this.message = message;
    this.exception = exception;
  }

  /**
   * Uniquely identifies the report.
   */
  public ReportId id() {
    return id;
  }

  public ServiceName consumer() {
    return consumer;
  }

  public Instant timestamp() {
    return timestamp;
  }

  public Message message() {
    return message;
  }

  public Exception exception() {
    return exception;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Report report = (Report) o;
    return Objects.equals(id, report.id) &&
        Objects.equals(timestamp, report.timestamp) &&
        Objects.equals(consumer, report.consumer) &&
        Objects.equals(message, report.message) &&
        Objects.equals(exception, report.exception);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Report{" +
        "id=" + id +
        ", timestamp=" + timestamp +
        ", consumer=" + consumer +
        ", message=" + message +
        ", exception=" + exception +
        '}';
  }

  @Embeddable
  public static class Message {

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "type"))
    private MessageType type;
    @Embedded
    private Data data;
    @ElementCollection
    private Map<String, String> headers;

    protected Message() {}

    public Message(MessageType type, Data data, Map<String, String> headers) {
      this.type = type;
      this.data = data;
      this.headers = headers;
    }

    public MessageType type() {
      return type;
    }

    public Data data() {
      return data;
    }

    public Map<String, String> headers() {
      return headers;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Message message = (Message) o;
      return Objects.equals(type, message.type) &&
          Objects.equals(data, message.data) &&
          Objects.equals(headers, message.headers);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, data, headers);
    }

    @Override
    public String toString() {
      return "Message{" +
          "type=" + type +
          ", data=" + data +
          ", headers=" + headers +
          '}';
    }

    @Embeddable
    public static class Data {
      private String mimeType;
      private byte[] data;

      protected Data() {}

      public Data(String mimeType, byte[] data) {
        this.mimeType = mimeType;
        this.data = data;
      }

      public MimeType mimeType() {
        return MimeType.valueOf(mimeType);
      }

      public byte[] data() {
        return data;
      }

      public String dataAsUtf8() {
        return new String(data, Charset.forName("UTF-8"));
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data1 = (Data) o;
        return Objects.equals(mimeType, data1.mimeType) &&
            Arrays.equals(data, data1.data);
      }

      @Override
      public int hashCode() {
        return Objects.hash(mimeType, data);
      }

      @Override
      public String toString() {
        return "Data{" +
            "mimeType='" + mimeType + '\'' +
            ", data=" + Arrays.toString(data) +
            ", dataAsUtf8='" + dataAsUtf8() + '\'' +
            '}';
      }
    }
  }

  @Embeddable
  public static class Exception {
    @ElementCollection
    private List<String> typeHierarchy;
    private String shortMessage;
    private String longMessage;

    protected Exception() {}

    public Exception(List<String> typeHierarchy, String shortMessage, String longMessage) {
      this.typeHierarchy = typeHierarchy;
      this.shortMessage = shortMessage;
      this.longMessage = longMessage;
    }

    public List<String> typeHierarchy() {
      return typeHierarchy;
    }

    public String shortMessage() {
      return shortMessage;
    }

    public String longMessage() {
      return longMessage;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Exception exception = (Exception) o;
      return Objects.equals(typeHierarchy, exception.typeHierarchy) &&
          Objects.equals(shortMessage, exception.shortMessage) &&
          Objects.equals(longMessage, exception.longMessage);
    }

    @Override
    public int hashCode() {
      return Objects.hash(typeHierarchy, shortMessage, longMessage);
    }

    @Override
    public String toString() {
      return "Exception{" +
          "typeHierarchy=" + typeHierarchy +
          ", shortMessage='" + shortMessage + '\'' +
          ", longMessage='" + longMessage + '\'' +
          '}';
    }
  }
}
