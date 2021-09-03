import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GnosisSafeComponent } from '../list/gnosis-safe.component';
import { GnosisSafeDetailComponent } from '../detail/gnosis-safe-detail.component';
import { GnosisSafeUpdateComponent } from '../update/gnosis-safe-update.component';
import { GnosisSafeRoutingResolveService } from './gnosis-safe-routing-resolve.service';

const gnosisSafeRoute: Routes = [
  {
    path: '',
    component: GnosisSafeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GnosisSafeDetailComponent,
    resolve: {
      gnosisSafe: GnosisSafeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GnosisSafeUpdateComponent,
    resolve: {
      gnosisSafe: GnosisSafeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GnosisSafeUpdateComponent,
    resolve: {
      gnosisSafe: GnosisSafeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gnosisSafeRoute)],
  exports: [RouterModule],
})
export class GnosisSafeRoutingModule {}
