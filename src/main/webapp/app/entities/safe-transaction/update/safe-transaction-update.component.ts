import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ISafeTransaction, SafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGnosisSafe } from 'app/entities/gnosis-safe/gnosis-safe.model';
import { GnosisSafeService } from 'app/entities/gnosis-safe/service/gnosis-safe.service';

@Component({
  selector: 'jhi-safe-transaction-update',
  templateUrl: './safe-transaction-update.component.html',
})
export class SafeTransactionUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  gnosisSafesSharedCollection: IGnosisSafe[] = [];

  editForm = this.fb.group({
    id: [],
    comment: [],
    token: [],
    value: [null, [Validators.required]],
    receiver: [null, [Validators.required]],
    created: [null, [Validators.required]],
    creator: [],
    gnosisSafe: [null, Validators.required],
  });

  constructor(
    protected safeTransactionService: SafeTransactionService,
    protected userService: UserService,
    protected gnosisSafeService: GnosisSafeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ safeTransaction }) => {
      if (safeTransaction.id === undefined) {
        const today = dayjs().startOf('day');
        safeTransaction.created = today;
      }

      this.updateForm(safeTransaction);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const safeTransaction = this.createFromForm();
    if (safeTransaction.id !== undefined) {
      this.subscribeToSaveResponse(this.safeTransactionService.update(safeTransaction));
    } else {
      this.subscribeToSaveResponse(this.safeTransactionService.create(safeTransaction));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackGnosisSafeById(index: number, item: IGnosisSafe): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISafeTransaction>>): void {
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

  protected updateForm(safeTransaction: ISafeTransaction): void {
    this.editForm.patchValue({
      id: safeTransaction.id,
      comment: safeTransaction.comment,
      token: safeTransaction.token,
      value: safeTransaction.value,
      receiver: safeTransaction.receiver,
      created: safeTransaction.created ? safeTransaction.created.format(DATE_TIME_FORMAT) : null,
      creator: safeTransaction.creator,
      gnosisSafe: safeTransaction.gnosisSafe,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, safeTransaction.creator);
    this.gnosisSafesSharedCollection = this.gnosisSafeService.addGnosisSafeToCollectionIfMissing(
      this.gnosisSafesSharedCollection,
      safeTransaction.gnosisSafe
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('creator')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.gnosisSafeService
      .query()
      .pipe(map((res: HttpResponse<IGnosisSafe[]>) => res.body ?? []))
      .pipe(
        map((gnosisSafes: IGnosisSafe[]) =>
          this.gnosisSafeService.addGnosisSafeToCollectionIfMissing(gnosisSafes, this.editForm.get('gnosisSafe')!.value)
        )
      )
      .subscribe((gnosisSafes: IGnosisSafe[]) => (this.gnosisSafesSharedCollection = gnosisSafes));
  }

  protected createFromForm(): ISafeTransaction {
    return {
      ...new SafeTransaction(),
      id: this.editForm.get(['id'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      token: this.editForm.get(['token'])!.value,
      value: this.editForm.get(['value'])!.value,
      receiver: this.editForm.get(['receiver'])!.value,
      created: this.editForm.get(['created'])!.value ? dayjs(this.editForm.get(['created'])!.value, DATE_TIME_FORMAT) : undefined,
      creator: this.editForm.get(['creator'])!.value,
      gnosisSafe: this.editForm.get(['gnosisSafe'])!.value,
    };
  }
}
