import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCarousel, NgbSlideEvent, NgbSlideEventSource } from '@ng-bootstrap/ng-bootstrap';
import { Directories } from '../directories';
import { Paths } from '../paths';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.scss'],
})
export class HeroComponent implements OnInit {
  public homePath = Paths.PATH_HOME;
  images: any[] = [];
  paused = false;
  pauseOnHover = true;
  pauseOnFocus = true;

  @ViewChild('carousel', {static : true}) carousel: NgbCarousel;

  constructor() {}

  ngOnInit(): void {
    this.images[0] = {image: Directories.PATH_SLIDER_IMGS + 'slide-1.jpg', title: "Title 1", descript: "Convert NOW!"};
    this.images[1] = {image: Directories.PATH_SLIDER_IMGS + 'slide-2.jpg', title: "Title 2", descript: "Ohhh yeah"};
    this.images[2] = {image: Directories.PATH_SLIDER_IMGS + 'slide-3.jpg', title: "Title 3", descript: "Thrreee"};
  }

  scrollToElement(id: any) {
    let el = document.getElementById(id);
    el.scrollIntoView({ behavior: 'smooth' });
  }

  togglePaused() {
    if (this.paused) {
      this.carousel.cycle();
    } else {
      this.carousel.pause();
    }
    this.paused = !this.paused;
  }

  onSlide(slideEvent: NgbSlideEvent) {
    if (slideEvent.paused &&
      (slideEvent.source === NgbSlideEventSource.ARROW_LEFT || slideEvent.source === NgbSlideEventSource.ARROW_RIGHT)) {
      this.togglePaused();
    }
    if (!slideEvent.paused && slideEvent.source === NgbSlideEventSource.INDICATOR) {
      this.togglePaused();
    }
  }
}
