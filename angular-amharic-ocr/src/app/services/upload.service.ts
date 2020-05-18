import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpErrorResponse, HttpEventType } from  '@angular/common/http';  
import { Observable } from  'rxjs/Observable';
import { map, catchError } from 'rxjs/operators';
import { Property } from 'src/app/common/property';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  SERVER_URL: string = "http://localhost:8080/api/v1/image/upload/";
	constructor(private httpClient: HttpClient) {}

  public upload(formData: FormData): Observable<any> {
    return this.httpClient.post<any>(this.SERVER_URL, formData).pipe(
      map(response => {
        return response;
      }))
  }
}
