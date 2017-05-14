package messagehospital.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.stream.Stream;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(Application.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    ObjectMapper mapper = converter.getObjectMapper();
    SimpleModule streamSerializer = new SimpleModule();
    streamSerializer.addSerializer(Stream.class, new StreamJsonSerializer());
    mapper.registerModule(streamSerializer);
    return converter;
  }

  @Bean
  public Clock clock(ZoneOffset zoneOffset) {
    return Clock.system(zoneOffset);
  }

  @Bean
  public ZoneOffset zoneOffset() {
    return ZoneOffset.UTC;
  }

  /**
   * @see <a href="https://github.com/FasterXML/jackson-modules-java8/issues/3">
   *   (datatypes) Add Serialization Support for Streams #3</a>
   */
  private static class StreamJsonSerializer extends JsonSerializer<Stream> {
    @Override
    public void serialize(Stream value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      try (Stream stream = value) {
        serializers
            .findValueSerializer(Iterator.class)
            .serialize(stream.iterator(), gen, serializers);
      }
    }
  }
}
