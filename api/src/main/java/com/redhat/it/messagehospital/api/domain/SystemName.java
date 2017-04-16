package com.redhat.it.messagehospital.api.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class SystemName {
  private String name;

  protected SystemName() {}

  public SystemName(String name) {
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
    SystemName that = (SystemName) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
