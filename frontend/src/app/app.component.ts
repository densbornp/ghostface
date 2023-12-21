import { Component, OnInit } from '@angular/core';
import { setTheme } from 'ngx-bootstrap/utils';
import { CookieService } from './services/cookie.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    title: string;

    constructor(private cookieService: CookieService) {
        setTheme('bs5');
        this.title = 'GhostFace - Slash surveillance -'
    }

    ngOnInit(): void {

    }
}
