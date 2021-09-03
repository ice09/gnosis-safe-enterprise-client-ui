import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';

@Component({
  templateUrl: './safe-transaction-submit-dialog.component.html',
})
export class SafeTransactionSubmitDialogComponent {
  safeTransaction?: ISafeTransaction;

  constructor(protected safeTransactionService: SafeTransactionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmSubmit(id: number): void {
    this.safeTransactionService.submit(id).subscribe(() => {
      this.activeModal.close('submitted');
    });
  }
}
