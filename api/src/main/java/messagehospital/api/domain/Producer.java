package messagehospital.api.domain;

import java.util.Set;

public interface Producer {
  ServiceName name();
  Set<SyncService> syncServices();
}
