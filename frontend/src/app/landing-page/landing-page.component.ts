import { Component } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { CookieModalComponent } from '../cookie-modal/cookie-modal.component';
import { CookieService } from '../services/cookie.service';

@Component({
    selector: 'app-landing-page',
    templateUrl: './landing-page.component.html',
    styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent {
    modalRef: BsModalRef;
    config = {
        animated: true,
        ignoreBackdropClick: true,
        'class': 'modal-dialog-centered'
    };

    constructor(private modalService: BsModalService, private cookieService: CookieService) {
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
