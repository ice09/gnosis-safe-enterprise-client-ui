import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISignedTransaction } from '../signed-transaction.model';
import { SignedTransactionService } from '../service/signed-transaction.service';
import { SignedTransactionDeleteDialogComponent } from '../delete/signed-transaction-delete-dialog.component';

@Component({
  selector: 'jhi-signed-transaction',
  templateUrl: './signed-transaction.component.html',
})
export class SignedTransactionComponent implements OnInit {
  signedTransactions?: ISignedTransaction[];
  isLoading = false;

  constructor(protected signedTransactionService: SignedTransactionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.signedTransactionService.query().subscribe(
      (res: HttpResponse<ISignedTransaction[]>) => {
        this.isLoading = false;
        this.signedTransactions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ISignedTransaction): number {
    return item.id!;
  }

  delete(signedTransaction: ISignedTransaction): void {
    const modalRef = this.modalService.open(SignedTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.signedTransaction = signedTransaction;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
