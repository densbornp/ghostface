import { Component, HostListener, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    footerHeight: number;

    constructor() { }

    ngOnInit(): void {
        // Calculate footer height and inverse it to set margin-top
        this.footerHeight = document.getElementById('footer').offsetHeight * -1;
    }

    @HostListener('window:resize', ['$event'])
    onResize(event) {
        this.footerHeight = document.getElementById('footer').offsetHeight * -1;
    }
}
