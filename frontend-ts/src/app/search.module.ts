import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ListModule, ActionModule } from 'patternfly-ng';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  TabsModule,
  ModalModule,
  CollapseModule,
  AlertModule,
  PopoverModule,
  TooltipModule,
  TypeaheadModule,
  ComponentLoaderFactory,
  BsDropdownModule,
  ButtonsModule,
} from 'ngx-bootstrap';

import { AppComponent } from './app.component';
import { ReportService } from './report.service'

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    TabsModule,
    ModalModule,
    CollapseModule,
    AlertModule,
    PopoverModule,
    TooltipModule,
    TypeaheadModule,
    BsDropdownModule,
    ButtonsModule,
    ListModule,
    ActionModule,
  ],
})
export class SearchModule { }
