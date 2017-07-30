package messagehospital.api.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ServiceName {
  private String name;

  protected ServiceName() {}

  public ServiceName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServiceName that = (ServiceName) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
