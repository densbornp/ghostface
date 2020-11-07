import { Component, HostListener, OnInit } from '@angular/core';
import { Paths } from '../paths';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  public homePath = Paths.PATH_HOME;

  selectedNav: HTMLElement;
  header: HTMLElement;
  content: HTMLElement;
  offset: any;

  public scroll: any;

  constructor() {}

  ngOnInit(): void {
    this.selectedNav = document.getElementById('nav-hero');
    this.header = document.getElementById('header');
    this.content = document.getElementById('info');
    this.offset = this.header.offsetTop;
  }

  @HostListener('window:scroll', ['$event'])
  onWindowScroll(e) {

    if(window.pageYOffset > this.offset) {
      this.header.classList.add('sticky');
      this.content.classList.add('sticky');
    } else {
      this.header.classList.remove('sticky');
      this.content.classList.remove('sticky');
    }
  }

  public selectMenuEntry(id): void {
    let el = document.getElementById(id);
    this.selectedNav.classList.remove('active');
    if (id === 'hero') {
      el = document.getElementById('nav-hero');
    } else if (id === 'info') {
      el = document.getElementById('nav-info');
    } else if (id === 'conversion') {
      el = document.getElementById('nav-conversion');
    } else if (id === 'contact') {
      el = document.getElementById('nav-contact');
    }
    el.classList.add('active');
    this.selectedNav = el;
  }
}
