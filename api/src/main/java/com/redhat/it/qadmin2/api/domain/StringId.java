package com.redhat.it.qadmin2.api.domain;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@Embeddable
abstract class StringId implements Serializable {
  protected String rawId;

  protected StringId() {}

  protected StringId(String rawId) {
    this.rawId = Objects.requireNonNull(rawId);
  }

  @Override
  public String toString() {
    return rawId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StringId stringId = (StringId) o;
    return Objects.equals(rawId, stringId.rawId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rawId);
  }
}
