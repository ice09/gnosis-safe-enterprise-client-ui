import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISignedTransaction, SignedTransaction } from '../signed-transaction.model';
import { SignedTransactionService } from '../service/signed-transaction.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ISafeTransaction } from 'app/entities/safe-transaction/safe-transaction.model';
import { SafeTransactionService } from 'app/entities/safe-transaction/service/safe-transaction.service';

@Component({
  selector: 'jhi-signed-transaction-update',
  templateUrl: './signed-transaction-update.component.html',
})
export class SignedTransactionUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  safeTransactionsSharedCollection: ISafeTransaction[] = [];

  editForm = this.fb.group({
    id: [],
    signedTx: [null],
    salt: [null, Validators.required],
    signer: [null],
    safeTransaction: [null, Validators.required],
  });

  constructor(
    protected signedTransactionService: SignedTransactionService,
    protected userService: UserService,
    protected safeTransactionService: SafeTransactionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signedTransaction }) => {
      this.updateForm(signedTransaction);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const signedTransaction = this.createFromForm();
    if (signedTransaction.id !== undefined) {
      this.subscribeToSaveResponse(this.signedTransactionService.update(signedTransaction));
    } else {
      this.subscribeToSaveResponse(this.signedTransactionService.create(signedTransaction));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackSafeTransactionById(index: number, item: ISafeTransaction): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISignedTransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(signedTransaction: ISignedTransaction): void {
    this.editForm.patchValue({
      id: signedTransaction.id,
      signedTx: signedTransaction.signedTx,
      salt: signedTransaction.salt,
      signer: signedTransaction.signer,
      safeTransaction: signedTransaction.safeTransaction,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, signedTransaction.signer);
    this.safeTransactionsSharedCollection = this.safeTransactionService.addSafeTransactionToCollectionIfMissing(
      this.safeTransactionsSharedCollection,
      signedTransaction.safeTransaction
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('signer')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.safeTransactionService
      .query()
      .pipe(map((res: HttpResponse<ISafeTransaction[]>) => res.body ?? []))
      .pipe(
        map((safeTransactions: ISafeTransaction[]) =>
          this.safeTransactionService.addSafeTransactionToCollectionIfMissing(safeTransactions, this.editForm.get('safeTransaction')!.value)
        )
      )
      .subscribe((safeTransactions: ISafeTransaction[]) => (this.safeTransactionsSharedCollection = safeTransactions));
  }

  protected createFromForm(): ISignedTransaction {
    return {
      ...new SignedTransaction(),
      id: this.editForm.get(['id'])!.value,
      signedTx: this.editForm.get(['signedTx'])!.value,
      salt: this.editForm.get(['salt'])!.value,
      signer: this.editForm.get(['signer'])!.value,
      safeTransaction: this.editForm.get(['safeTransaction'])!.value,
    };
  }
}
