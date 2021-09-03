import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';
import { SafeTransactionDeleteDialogComponent } from '../delete/safe-transaction-delete-dialog.component';
import { SafeTransactionSubmitDialogComponent } from '../submit/safe-transaction-submit-dialog.component';

@Component({
  selector: 'jhi-safe-transaction',
  templateUrl: './safe-transaction.component.html',
})
export class SafeTransactionComponent implements OnInit {
  safeTransactions?: ISafeTransaction[];
  isLoading = false;

  constructor(protected safeTransactionService: SafeTransactionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.safeTransactionService.query().subscribe(
      (res: HttpResponse<ISafeTransaction[]>) => {
        this.isLoading = false;
        this.safeTransactions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ISafeTransaction): number {
    return item.id!;
  }

  delete(safeTransaction: ISafeTransaction): void {
    const modalRef = this.modalService.open(SafeTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.safeTransaction = safeTransaction;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  submit(safeTransaction: ISafeTransaction): void {
    const modalRef = this.modalService.open(SafeTransactionSubmitDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.safeTransaction = safeTransaction;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.loadAll();
    });
  }

}
