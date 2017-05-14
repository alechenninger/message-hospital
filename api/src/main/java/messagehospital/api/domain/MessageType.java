package messagehospital.api.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class MessageType {
  private String name;

  protected MessageType() {}

  public MessageType(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MessageType that = (MessageType) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
