import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'salted-user',
        data: { pageTitle: 'gnosisSafeEnterpriseEditionApp.saltedUser.home.title' },
        loadChildren: () => import('./salted-user/salted-user.module').then(m => m.SaltedUserModule),
      },
      {
        path: 'gnosis-safe',
        data: { pageTitle: 'gnosisSafeEnterpriseEditionApp.gnosisSafe.home.title' },
        loadChildren: () => import('./gnosis-safe/gnosis-safe.module').then(m => m.GnosisSafeModule),
      },
      {
        path: 'safe-transaction',
        data: { pageTitle: 'gnosisSafeEnterpriseEditionApp.safeTransaction.home.title' },
        loadChildren: () => import('./safe-transaction/safe-transaction.module').then(m => m.SafeTransactionModule),
      },
      {
        path: 'signed-transaction',
        data: { pageTitle: 'gnosisSafeEnterpriseEditionApp.signedTransaction.home.title' },
        loadChildren: () => import('./signed-transaction/signed-transaction.module').then(m => m.SignedTransactionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
