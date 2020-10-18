import { Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  selectedNav: HTMLElement;

  constructor() {}

  ngOnInit(): void {
    this.selectedNav = document.getElementById("nav-hero");
  }

  scrollToElement(id) {
    let el = document.getElementById(id);
    el.scrollIntoView({behavior: "smooth"});
    this.selectMenuEntry(id, el);
  }

  private selectMenuEntry(id, el): void {
    this.selectedNav.classList.remove("active");
    if (id === "hero") {
      el = document.getElementById("nav-hero");
    } else if (id === "info") {
      el = document.getElementById("nav-info");
    } else if (id === "conversion") {
      el = document.getElementById("nav-conversion");
    } else if (id === "contact") {
      el = document.getElementById("nav-contact");
    }
    el.classList.add("active");
    this.selectedNav = el;
  }
}
