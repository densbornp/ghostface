import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Paths } from '../paths';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.scss'],
})
export class HeroComponent implements OnInit {
  public homePath = Paths.PATH_HOME;

  @ViewChild('carousel-inner') el: ElementRef;

  constructor() {}

  ngOnInit(): void {}

  scrollToElement(id: any) {
    let el = document.getElementById(id);
    el.scrollIntoView({ behavior: 'smooth' });
  }
}
