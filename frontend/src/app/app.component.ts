import { Component, ElementRef, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CookieService } from 'ngx-cookie';
import { Constants } from './constants';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { setTheme } from 'ngx-bootstrap/utils';
import { CookieModalComponent } from './cookie-modal/cookie-modal.component';

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
        if (!this.isCookieAvailable()) {
            this.showCookieModal();
        }
    }

    private showCookieModal() {
        this.modalRef = this.modalService.show(CookieModalComponent, this.config);
    }

    private isCookieAvailable() {
        return this.cookieService.get(Constants.COOKIE);
    }
}
