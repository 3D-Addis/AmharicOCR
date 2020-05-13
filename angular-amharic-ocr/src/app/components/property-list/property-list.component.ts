import { Component, OnInit } from '@angular/core';
import { Property } from '/Dev 2020/angular-java/OCR Tool for Hareg/AmharicOCR/angular-amharic-ocr/src/app/common/property';
import { PropertyService } from 'src/app/services/property.service';

@Component({
  selector: 'app-property-list',
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.css']
})
export class PropertyListComponent implements OnInit {

  
  properties: Property[];
  
  constructor(private _propertyService: PropertyService) { }

  ngOnInit(): void {
    this.listProperties();
  }

  listProperties() {
    this._propertyService.getProperties().subscribe(data => {
        console.log(data);
        this.properties = data;
      }
    )
  }

}
