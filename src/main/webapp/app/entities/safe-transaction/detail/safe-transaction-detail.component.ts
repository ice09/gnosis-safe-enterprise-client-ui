import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISafeTransaction } from '../safe-transaction.model';

@Component({
  selector: 'jhi-safe-transaction-detail',
  templateUrl: './safe-transaction-detail.component.html',
})
export class SafeTransactionDetailComponent implements OnInit {
  safeTransaction: ISafeTransaction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ safeTransaction }) => {
      this.safeTransaction = safeTransaction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
