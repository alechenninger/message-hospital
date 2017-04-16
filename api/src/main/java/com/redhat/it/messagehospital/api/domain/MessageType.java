package com.redhat.it.messagehospital.api.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class MessageType {
  private String messageType;

  protected MessageType() {}

  public MessageType(String messageType) {
    this.messageType = messageType;
  }

  public String toString() {
    return messageType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MessageType that = (MessageType) o;
    return Objects.equals(messageType, that.messageType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageType);
  }
}
