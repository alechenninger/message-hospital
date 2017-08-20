export class Report {
  id: String;
  data: String;
  consumer: String;
  messageType: String;
  timestamp: Date;
  headers: Map<String, String>;
  errorMessage: String;
}