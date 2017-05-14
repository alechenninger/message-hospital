package messagehospital.api.application;

import messagehospital.api.domain.Producer;
import messagehospital.api.domain.Producers;
import messagehospital.api.domain.SystemName;

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
