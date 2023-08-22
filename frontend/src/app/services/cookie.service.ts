import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
  })
  export class CookieService {

    constructor(private http: HttpClient) { }

    createCookie(): Observable<Object> {
      return this.http.post("/cookie", null);
    }

    isCookieSet(): Observable<Object> {
        return this.http.get("/cookie");
    }
}
