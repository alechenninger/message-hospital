package messagehospital.api.domain;

import java.util.Set;

public interface Producer {
  SystemName name();
  Set<SyncService> syncServices();
}
