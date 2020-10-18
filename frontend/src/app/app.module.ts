import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { FaqComponent } from './faq/faq.component';
import { PolicyComponent } from './policy/policy.component';
import { HeroComponent } from './hero/hero.component';
import { ContactComponent } from './contact/contact.component';
import { InfoComponent } from './info/info.component';
import { ConversionComponent } from './conversion/conversion.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    FaqComponent,
    PolicyComponent,
    HeroComponent,
    ContactComponent,
    InfoComponent,
    ConversionComponent,
    FooterComponent,
    HeaderComponent,
    PageNotFoundComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
