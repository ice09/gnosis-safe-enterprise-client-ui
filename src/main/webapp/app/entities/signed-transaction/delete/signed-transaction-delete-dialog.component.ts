import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISignedTransaction } from '../signed-transaction.model';
import { SignedTransactionService } from '../service/signed-transaction.service';

@Component({
  templateUrl: './signed-transaction-delete-dialog.component.html',
})
export class SignedTransactionDeleteDialogComponent {
  signedTransaction?: ISignedTransaction;

  constructor(protected signedTransactionService: SignedTransactionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.signedTransactionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
