import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.scss']
})
export class HeroComponent implements OnInit {

  @ViewChild('carousel-inner') el: ElementRef;

  constructor() { }

  ngOnInit(): void {

  }

  scrollToElement(id: any) {
    let el = document.getElementById(id);
    el.scrollIntoView({behavior: "smooth"});
  }

}
