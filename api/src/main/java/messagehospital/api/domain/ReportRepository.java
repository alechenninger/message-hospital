package messagehospital.api.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface ReportRepository {
  ReportId nextReportId();

  Stream<Report> allReports();

  Optional<Report> reportById(ReportId id);

  Stream<Report> search(Set<ServiceName> consumers, Set<MessageType> types,
      Set<Map<String, String>> headers, int index, int max);

  Stream<String> headerNamesByType(MessageType type);

  void save(Report report);

  void saveAll(Stream<Report> reports);

  void removeReportsOlderThan(Instant time);
}
