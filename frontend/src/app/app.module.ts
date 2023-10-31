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
import { HeaderComponent } from './navigation/navigation.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HttpClientModule } from '@angular/common/http';
import { HeroComponent } from './hero/hero.component';
import { CookieModule } from 'ngx-cookie';
import { CookieModalComponent } from './cookie-modal/cookie-modal.component';
import { ModalModule } from 'ngx-bootstrap/modal';
import { LandingPageComponent } from './landing-page/landing-page.component';

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
    HeroComponent,
    CookieModalComponent,
    LandingPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CookieModule.withOptions(),
    ModalModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
