import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConversionService {

  constructor(private http: HttpClient) { }

  uploadFile(data: any): Observable<Object> {
    return this.http.post("/upload", { params: data });
  }

  downloadFile(): Observable<Object> {
      return this.http.get("/download");
  }
}
