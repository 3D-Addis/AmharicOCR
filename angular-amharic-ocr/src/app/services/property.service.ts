

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Property } from '/Dev 2020/angular-java/OCR Tool for Hareg/AmharicOCR/angular-amharic-ocr/src/app/common/property';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {

  private baseUrl = "http://localhost:8080/api/v1/properties/";

  constructor(private httpClient: HttpClient) { }

  getProperties(): Observable<Property[]> {
    return this.httpClient.get<GetResponseProperties>(this.baseUrl).pipe(
          map(response => {
            return response._embedded.properties
          }
        ), 
          catchError( (error:any) => {
            return Observable.throw(error)
          }
        )
      )
    }
  }

interface GetResponseProperties{
  _embedded: {
    properties : Property[];
  }
}