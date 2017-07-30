import 'dart:async';

abstract class ReportResource {
  Stream<Report> search(Iterable<String> producers);
}

class Report {
  // TODO

  Report();

  factory Report.fromJson(Map<String, Object> json) {
    return new Report();
  }
}