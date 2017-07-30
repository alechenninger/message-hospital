package messagehospital.api.domain;

public interface ResubmitServiceFactory {
  ResubmitService forProtocol(ResubmitProtocol protocol);
}
