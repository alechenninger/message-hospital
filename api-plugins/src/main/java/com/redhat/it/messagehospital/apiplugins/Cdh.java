package com.redhat.it.messagehospital.apiplugins;

import com.redhat.it.messagehospital.api.domain.Producer;
import com.redhat.it.messagehospital.api.domain.RepublishService;

import java.util.Set;

public class Cdh implements Producer {
  @Override
  public String name() {
    return null;
  }

  @Override
  public Set<RepublishService> syncServices() {
    return null;
  }
}
