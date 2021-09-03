import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';

@Component({
  templateUrl: './safe-transaction-delete-dialog.component.html',
})
export class SafeTransactionDeleteDialogComponent {
  safeTransaction?: ISafeTransaction;

  constructor(protected safeTransactionService: SafeTransactionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.safeTransactionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
