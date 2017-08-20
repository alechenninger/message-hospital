import { Component } from '@angular/core';
import { OnInit } from '@angular/core';

import { ReportService } from './report.service'
import { Report } from './report'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private reportService: ReportService) { }

  title = 'message-hospital';
  reports: Array<Report>;

  ngOnInit(): void {
    this.getReports();
  }

  getReports(): void {
    this.reportService.getAllReports().then(r => this.reports = r);
  }
}
