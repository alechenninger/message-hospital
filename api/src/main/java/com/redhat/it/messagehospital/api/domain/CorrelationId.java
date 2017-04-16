package com.redhat.it.messagehospital.api.domain;

public class CorrelationId extends StringId {
  protected CorrelationId() {
  }

  public CorrelationId(String rawId) {
    super(rawId);
  }
}
