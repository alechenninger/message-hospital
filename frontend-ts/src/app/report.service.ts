import { Injectable } from '@angular/core';

import { Report } from './report';

@Injectable()
export class ReportService {
  getAllReports(): Promise<Report[]> {
    return Promise.resolve([{
      id: "314613a3-4d8d-483c-bbae-8987272f4d87",
      consumer: "user_service",
      data: "<user></user>",
      messageType: "user",
      errorMessage: "NullPointerException",
      headers: new Map([["header1", "value1"], ["header2", "value2"]]),
      timestamp: new Date(),
    }]);
  }
}