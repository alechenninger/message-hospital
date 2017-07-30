// Copyright (c) 2017, ahenning. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:frontend/report_resource.dart';

import 'package:angular2/core.dart';
import 'package:angular_components/angular_components.dart';

import 'dart:async';

@Component(
  selector: 'message-hospital',
  styleUrls: const ['message_hospital.css'],
  template: r'''
  <h1>Message hospital</h1>
  <material-input #producer label="Producer" floatingLabel autoFocus></material-input>
  <material-button raised class="blue" (click)="search(producer.value)">Search</material-button>
  ''',
  directives: const [materialDirectives],
  providers: const [materialProviders],
)
class MessageHospital {
  final ReportResource _reports;

  MessageHospital(this._reports);

  Future<Null> search(String producer) async {

  }
}
