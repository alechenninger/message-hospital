package messagehospital.api.infrastructure;

import messagehospital.api.domain.MessageType;
import messagehospital.api.domain.Report;
import messagehospital.api.domain.ReportId;
import messagehospital.api.domain.ReportRepository;
import messagehospital.api.domain.ServiceName;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Repository that synchronously writes new reports to a fast, optionally durable queue, then
 * asynchronously indexes them a delegate repository used for searching. Reports aren't immediately
 * available for reads, but write throughput can be higher as a result.
 */
public class DeferredReportRepository implements ReportRepository {
  private final ReportRepository delegate;

  public DeferredReportRepository(ReportRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public ReportId nextReportId() {
    return null;
  }

  @Override
  public Stream<Report> allReports() {
    return null;
  }

  @Override
  public Stream<Report> search(Set<ServiceName> consumers, Set<MessageType> types, Set<Map<String, String>> headers, int index, int max) {
    return null;
  }

  @Override
  public Stream<String> headerNamesByType(MessageType type) {
    return null;
  }

  @Override
  public void save(Report report) {

  }

  @Override
  public void saveAll(Stream<Report> reports) {

  }

  @Override
  public void removeReportsOlderThan(Instant time) {

  }

  @Override
  public Optional<Report> reportById(ReportId id) {
    return null;
  }
}
