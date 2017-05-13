package com.redhat.it.qadmin.apiplugins;

import com.redhat.it.qadmin2.api.domain.Producer;
import com.redhat.it.qadmin2.api.domain.SyncService;

import java.util.Set;

public class Ebs implements Producer {
  @Override
  public String name() {
    return null;
  }

  @Override
  public Set<SyncService> syncServices() {
    return null;
  }
}
