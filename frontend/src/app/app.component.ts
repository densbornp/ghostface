import { Component, OnInit } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { setTheme } from 'ngx-bootstrap/utils';
import { CookieModalComponent } from './cookie-modal/cookie-modal.component';
import { CookieService } from './services/cookie.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    title: string;
    modalRef: BsModalRef;
    config = {
        animated: true,
        ignoreBackdropClick: true,
        'class': 'modal-dialog-centered'
    };

    constructor(private modalService: BsModalService, private cookieService: CookieService) {
        setTheme('bs5');
        this.title = 'GhostFace - Slash surveillance -'
    }

    ngOnInit(): void {
        this.checkCookieAndOpenConsent();
    }

    private showCookieModal() {
        this.modalRef = this.modalService.show(CookieModalComponent, this.config);
    }

    private checkCookieAndOpenConsent() {
        this.cookieService.isCookieSet().subscribe(() => {
        }, (error) => { this.showCookieModal(); });

    }
}
