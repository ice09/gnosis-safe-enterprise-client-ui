import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SaltedUserComponent } from '../list/salted-user.component';
import { SaltedUserDetailComponent } from '../detail/salted-user-detail.component';
import { SaltedUserUpdateComponent } from '../update/salted-user-update.component';
import { SaltedUserRoutingResolveService } from './salted-user-routing-resolve.service';

const saltedUserRoute: Routes = [
  {
    path: '',
    component: SaltedUserComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SaltedUserDetailComponent,
    resolve: {
      saltedUser: SaltedUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SaltedUserUpdateComponent,
    resolve: {
      saltedUser: SaltedUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SaltedUserUpdateComponent,
    resolve: {
      saltedUser: SaltedUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(saltedUserRoute)],
  exports: [RouterModule],
})
export class SaltedUserRoutingModule {}
