import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SignedTransactionComponent } from '../list/signed-transaction.component';
import { SignedTransactionDetailComponent } from '../detail/signed-transaction-detail.component';
import { SignedTransactionUpdateComponent } from '../update/signed-transaction-update.component';
import { SignedTransactionRoutingResolveService } from './signed-transaction-routing-resolve.service';

const signedTransactionRoute: Routes = [
  {
    path: '',
    component: SignedTransactionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SignedTransactionDetailComponent,
    resolve: {
      signedTransaction: SignedTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SignedTransactionUpdateComponent,
    resolve: {
      signedTransaction: SignedTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SignedTransactionUpdateComponent,
    resolve: {
      signedTransaction: SignedTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(signedTransactionRoute)],
  exports: [RouterModule],
})
export class SignedTransactionRoutingModule {}
