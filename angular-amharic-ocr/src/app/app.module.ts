import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort'
import { LayoutModule } from '@angular/cdk/layout';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PropertyListComponent } from './components/property-list/property-list.component';
import { PropertyService } from './services/property.service';
import { DatTableComponent } from './dat-table/dat-table.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { FileUploadComponent } from './file-upload/file-upload.component';

const routes: Routes = [
  {path: 'properties', component: PropertyListComponent},
  {path: 'category/:id', component: PropertyListComponent},
  {path: '', redirectTo: 'properties', pathMatch: 'full'},
  {path: '**', component: PageNotFoundComponent}

];

@NgModule({
  declarations: [
    AppComponent,
    PropertyListComponent,
    DatTableComponent,
    FileUploadComponent
  ],
  imports: [
    ReactiveFormsModule,
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    LayoutModule, 
    MatCardModule, 
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTableModule,  
    MatProgressBarModule,
    MatPaginatorModule,
    MatSortModule,
    RouterModule.forRoot(routes)
  ],
  providers: [
    PropertyService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
