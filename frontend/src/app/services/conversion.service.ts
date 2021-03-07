import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConversionService {

  constructor(private http: HttpClient) { }

  uploadFile(formData: any): Observable<Object> {
    return this.http.post("/upload", formData);
  }

  downloadFile(): Observable<Object> {
      return this.http.get("/download");
  }
}
