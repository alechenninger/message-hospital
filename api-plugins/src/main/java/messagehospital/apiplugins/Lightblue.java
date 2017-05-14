package messagehospital.apiplugins;

import messagehospital.api.domain.Producer;
import messagehospital.api.domain.SyncService;

import java.util.Set;

public class Lightblue implements Producer {
  @Override
  public String name() {
    return null;
  }

  @Override
  public Set<SyncService> syncServices() {
    return null;
  }
}
