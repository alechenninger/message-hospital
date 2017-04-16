package com.redhat.it.messagehospital.api.domain;

import java.util.Set;

public interface Producer {
  SystemName name();
  Set<RepublishService> syncServices();
}
