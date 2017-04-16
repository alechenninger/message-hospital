package com.redhat.it.messagehospital.api.application;

import com.redhat.it.messagehospital.api.domain.Producer;
import com.redhat.it.messagehospital.api.domain.Producers;
import com.redhat.it.messagehospital.api.domain.SystemName;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Configuration
public class PluginConfiguration {

  @Bean
  public Producers producersOnClasspath() {
    ServiceLoader<Producer> loader = ServiceLoader.load(Producer.class);
    Map<SystemName, Producer> producers = new LinkedHashMap<>();
    for (Producer producer : loader) {
      producers.put(producer.name(), producer);
    }
    return new Producers(producers);
  }
}
