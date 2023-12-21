import { Component } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Constants } from '../constants';
import { CookieService } from '../services/cookie.service';


@Component({
  selector: 'app-cookie-modal',
  templateUrl: './cookie-modal.component.html',
  styleUrls: ['./cookie-modal.component.scss']
})
export class CookieModalComponent {

    constructor(private modalService: BsModalService, private cookieService: CookieService) {}

    confirm() {
        this.cookieService.createCookie().subscribe(() => {
            this.modalService.hide();
        });
    }

    cancel() {
        this.modalService.hide();
    }

}
