import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { FaqComponent } from './faq/faq.component';
import { PolicyComponent } from './policy/policy.component';
import { ContactComponent } from './contact/contact.component';
import { InfoComponent } from './info/info.component';
import { ConversionComponent } from './conversion/conversion.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RootComponent } from './root/root.component';
import { HttpClientModule } from '@angular/common/http';
import { LandingComponent } from './landing/landing.component';
import { HeroComponent } from './hero/hero.component';

@NgModule({
  declarations: [
    AppComponent,
    FaqComponent,
    PolicyComponent,
    ContactComponent,
    InfoComponent,
    ConversionComponent,
    FooterComponent,
    HeaderComponent,
    PageNotFoundComponent,
    RootComponent,
    LandingComponent,
    HeroComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
