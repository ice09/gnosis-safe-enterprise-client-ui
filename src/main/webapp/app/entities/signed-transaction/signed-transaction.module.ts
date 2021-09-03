import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SignedTransactionComponent } from './list/signed-transaction.component';
import { SignedTransactionDetailComponent } from './detail/signed-transaction-detail.component';
import { SignedTransactionUpdateComponent } from './update/signed-transaction-update.component';
import { SignedTransactionDeleteDialogComponent } from './delete/signed-transaction-delete-dialog.component';
import { SignedTransactionRoutingModule } from './route/signed-transaction-routing.module';

@NgModule({
  imports: [SharedModule, SignedTransactionRoutingModule],
  declarations: [
    SignedTransactionComponent,
    SignedTransactionDetailComponent,
    SignedTransactionUpdateComponent,
    SignedTransactionDeleteDialogComponent,
  ],
  entryComponents: [SignedTransactionDeleteDialogComponent],
})
export class SignedTransactionModule {}
