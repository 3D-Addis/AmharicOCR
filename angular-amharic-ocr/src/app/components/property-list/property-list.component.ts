import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

import { Property } from 'src/app/common/property';
import { PropertyService } from 'src/app/services/property.service';
//import { UploadService } from  'src/app/services/upload.service';

@Component({
  selector: 'app-property-list',
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.css']
})
export class PropertyListComponent implements OnInit {

  @ViewChild("fileUpload", {static: false}) fileUpload: ElementRef;
  
  files  = [];
  dbProperties: Property[];
  public userFile: any = File;
  
  constructor(private _propertyService: PropertyService) { }

  ngOnInit(): void {
  }

  listPropertiesFromDB() {
    this._propertyService.getProperties().subscribe(data => {
        console.log(data);  
        this.dbProperties = data;
    })
  }

  //listPropertiesFrom(formData: FormData) {
    //this._uploadService.upload(formData).subscribe(data => {
      //  console.log(data);  
        //this.dbProperties = data;
    //})
  //}
  //onSelectFile(event) {  
    //const file = event.target.files[0];
    //this.userFile = file;
  //}

  //saveImage() { 
    //const formData = new FormData();
    //formData.append('file', this.userFile);
    //this.listPropertiesFrom(formData);
    
  //  this._uploadService.upload(formData).subscribe(data => {
   //     console.log(data);
    //})
  //}
}
