import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

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
import { ReportService } from './report.service';
import { SearchModule } from './search.module';

@NgModule({
  imports: [
    BsDropdownModule.forRoot(),
    ButtonsModule.forRoot(),
    TabsModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    CollapseModule.forRoot(),
    AlertModule.forRoot(),
    PopoverModule.forRoot(),
    TypeaheadModule.forRoot(),
    BrowserModule,
    CommonModule,
    FormsModule,
    SearchModule,
  ],
  providers: [ReportService],//, BsDropdownConfig, TabsetConfig, TooltipConfig],
  bootstrap: [AppComponent],
})
export class AppModule { }
