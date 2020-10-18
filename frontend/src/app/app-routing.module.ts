import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FaqComponent } from './faq/faq.component';
import { HomeComponent } from './home/home.component';
import { PolicyComponent } from './policy/policy.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  //{path: 'info', component: InfoComponent},
  //{path: 'conversion', component: ConversionComponent},
  //{path: 'contact', component: ContactComponent},
  {path: 'faq', component: FaqComponent},
  {path: 'policy', component: PolicyComponent},
  {path: '**', redirectTo: '', pathMatch: 'full'}
]

@NgModule({
  imports: [RouterModule.forRoot(routes, {scrollPositionRestoration: 'enabled'})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
