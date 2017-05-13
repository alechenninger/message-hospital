package com.redhat.it.qadmin2.api.domain;

import java.util.Set;

public interface Producer {
  SystemName name();
  Set<SyncService> syncServices();
}
