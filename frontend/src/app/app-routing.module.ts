import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FaqComponent } from './faq/faq.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { PolicyComponent } from './policy/policy.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

const routes: Routes = [
    { path: '', component: LandingPageComponent },
    { path: 'faq', component: FaqComponent },
    { path: 'policy', component: PolicyComponent },
    { path: '**', component: PageNotFoundComponent, pathMatch: 'full' },
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {
            scrollPositionRestoration: 'enabled',
            anchorScrolling: 'enabled'
        }),
    ],
    exports: [RouterModule],
})
export class AppRoutingModule { }
