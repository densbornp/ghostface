import { Component, HostListener, OnInit } from '@angular/core';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss']
})
export class FaqComponent implements OnInit {

    footerHeight: number;

    constructor() { }

    ngOnInit(): void {
        this.footerHeight = document.getElementById('footer').offsetHeight;
    }

    @HostListener('window:resize', ['$event'])
    onResize(event) {
        this.footerHeight = document.getElementById('footer').offsetHeight;
    }
}
