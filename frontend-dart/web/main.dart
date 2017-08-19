// Copyright (c) 2017, ahenning. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:angular2/angular2.dart';
import 'package:angular2/platform/browser.dart';

import 'package:frontend/message_hospital.dart';
import 'package:frontend/http_report_resource.dart';

void main() {
  bootstrap(MessageHospital, [provide(ReportResource, useClass: HttpReportResource)]);
}
