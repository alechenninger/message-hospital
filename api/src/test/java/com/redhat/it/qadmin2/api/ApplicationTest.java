package com.redhat.it.qadmin2.api;

import static org.junit.Assert.assertEquals;

import com.redhat.it.qadmin2.api.http.ReportResource.ReportDto;
import com.redhat.it.qadmin2.api.http.ReportResource.ReportRequest;
import com.redhat.it.qadmin2.api.http.ReportResource.SearchRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "ArraysAsListWithZeroOrOneArgument"})
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
  @Autowired
  TestRestTemplate restTemplate;

  @Test
  public void reportAndSearch() {
    ResponseEntity<ReportDto> response = restTemplate.postForEntity("/reports",
        new ReportRequest()
            .correlationId("100")
            .data("<xml></xml>")
            .dataFormat("application/xml")
            .timestamp(OffsetDateTime.now())
            .producerSystem("test")
            .messageType("customer")
            .errorTypes(Arrays.asList("exception"))
            .headers(Stream.<String[]>of(
                new String[]{"test1", "value1"})
                .collect(Collectors.toMap(h -> h[0], h -> h[1]))),
        ReportDto.class);

    assertEquals(200, response.getStatusCodeValue());

    List all = restTemplate.getForEntity("/reports", List.class).getBody();
    assertEquals(1, all.size());

    ResponseEntity<List> searchResponse = restTemplate.postForEntity("/reports/search",
        new SearchRequest()
            .producers(Arrays.asList("test"))
            .messageTypes(Arrays.asList("customer"))
            .headers(Collections.singleton(Stream.<String[]>of(
                new String[]{"test1", "value1"})
                .collect(Collectors.toMap(h -> h[0], h -> h[1])))),
        List.class);
    List<ReportDto> reports = searchResponse.getBody();

    assertEquals(1, reports.size());

    searchResponse = restTemplate.postForEntity("/reports/search",
        new SearchRequest()
            .producers(Arrays.asList("test"))
            .messageTypes(Arrays.asList("customer"))
            .headers(Collections.singleton(Stream.<String[]>of(
                new String[]{"test1", "wrong"})
                .collect(Collectors.toMap(h -> h[0], h -> h[1])))),
        List.class);
    reports = searchResponse.getBody();

    assertEquals(0, reports.size());
  }
}
