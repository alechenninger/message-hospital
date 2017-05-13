package com.redhat.it.qadmin2.api.domain;

import java.util.Map;
import java.util.Set;

public class Producers {
  private final Map<SystemName, Producer> producers;

  public Producers(Map<SystemName, Producer> producers) {
    this.producers = producers;
  }

  public Set<SystemName> producers() {
    return producers.keySet();
  }

  public Producer byName(SystemName name) {
    return producers.get(name);
  }
}
