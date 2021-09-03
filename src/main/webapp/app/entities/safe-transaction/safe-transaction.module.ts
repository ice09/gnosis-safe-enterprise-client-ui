import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SafeTransactionComponent } from './list/safe-transaction.component';
import { SafeTransactionDetailComponent } from './detail/safe-transaction-detail.component';
import { SafeTransactionUpdateComponent } from './update/safe-transaction-update.component';
import { SafeTransactionDeleteDialogComponent } from './delete/safe-transaction-delete-dialog.component';
import { SafeTransactionSubmitDialogComponent } from './submit/safe-transaction-submit-dialog.component';
import { SafeTransactionRoutingModule } from './route/safe-transaction-routing.module';

@NgModule({
  imports: [SharedModule, SafeTransactionRoutingModule],
  declarations: [
    SafeTransactionComponent,
    SafeTransactionDetailComponent,
    SafeTransactionUpdateComponent,
    SafeTransactionDeleteDialogComponent,
    SafeTransactionSubmitDialogComponent,
  ],
  entryComponents: [SafeTransactionDeleteDialogComponent],
})
export class SafeTransactionModule {}
