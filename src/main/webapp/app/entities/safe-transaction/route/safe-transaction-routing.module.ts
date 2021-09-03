import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SafeTransactionComponent } from '../list/safe-transaction.component';
import { SafeTransactionDetailComponent } from '../detail/safe-transaction-detail.component';
import { SafeTransactionUpdateComponent } from '../update/safe-transaction-update.component';
import { SafeTransactionRoutingResolveService } from './safe-transaction-routing-resolve.service';

const safeTransactionRoute: Routes = [
  {
    path: '',
    component: SafeTransactionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SafeTransactionDetailComponent,
    resolve: {
      safeTransaction: SafeTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SafeTransactionUpdateComponent,
    resolve: {
      safeTransaction: SafeTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SafeTransactionUpdateComponent,
    resolve: {
      safeTransaction: SafeTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(safeTransactionRoute)],
  exports: [RouterModule],
})
export class SafeTransactionRoutingModule {}
