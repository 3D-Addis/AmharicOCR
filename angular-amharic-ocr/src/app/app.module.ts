import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { PropertyListComponent } from './components/property-list/property-list.component';
import { PropertyService } from './services/property.service'

@NgModule({
  declarations: [
    AppComponent,
    PropertyListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [
    PropertyService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
