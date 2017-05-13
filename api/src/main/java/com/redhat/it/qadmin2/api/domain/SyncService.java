package com.redhat.it.qadmin2.api.domain;

import java.util.Map;
import java.util.Set;

public interface SyncService {
  MessageType messageType();
  Set<String> keys();
  // TODO: sync result?
  void syncBy(Map<String, String> keys);

  class SyncException extends Exception {
    public SyncException() {
    }

    public SyncException(String message) {
      super(message);
    }

    public SyncException(String message, Throwable cause) {
      super(message, cause);
    }

    public SyncException(Throwable cause) {
      super(cause);
    }

    public SyncException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }
}
