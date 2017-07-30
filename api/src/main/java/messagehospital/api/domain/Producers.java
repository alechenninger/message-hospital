package messagehospital.api.domain;

import java.util.Map;
import java.util.Set;

public class Producers {
  private final Map<ServiceName, Producer> producers;

  public Producers(Map<ServiceName, Producer> producers) {
    this.producers = producers;
  }

  public Set<ServiceName> producers() {
    return producers.keySet();
  }

  public Producer byName(ServiceName name) {
    return producers.get(name);
  }
}
