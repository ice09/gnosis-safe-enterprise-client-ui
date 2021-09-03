import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISignedTransaction } from '../signed-transaction.model';

@Component({
  selector: 'jhi-signed-transaction-detail',
  templateUrl: './signed-transaction-detail.component.html',
})
export class SignedTransactionDetailComponent implements OnInit {
  signedTransaction: ISignedTransaction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signedTransaction }) => {
      this.signedTransaction = signedTransaction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
