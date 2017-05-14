package messagehospital.api.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface ReportRepository {
  ReportId nextReportId();

  Stream<Report> reportsByCorrelationId(CorrelationId id);

  Stream<Report> allReports();

  // TODO: start index, max results
  Stream<Report> search(Set<SystemName> producers, Set<MessageType> types,
      Set<Map<String, String>> headers, int index, int max);

  Set<String> headerNamesByType(MessageType type);

  void save(Report report);

  void removeReportsOlderThan(Instant time);
}
