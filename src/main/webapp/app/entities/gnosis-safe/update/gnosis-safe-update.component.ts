import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IGnosisSafe, GnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-gnosis-safe-update',
  templateUrl: './gnosis-safe-update.component.html',
})
export class GnosisSafeUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    address: [null, [Validators.required]],
    signatures: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
    owners: [null, Validators.required],
  });

  constructor(
    protected gnosisSafeService: GnosisSafeService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gnosisSafe }) => {
      this.updateForm(gnosisSafe);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gnosisSafe = this.createFromForm();
    if (gnosisSafe.id !== undefined) {
      this.subscribeToSaveResponse(this.gnosisSafeService.update(gnosisSafe));
    } else {
      this.subscribeToSaveResponse(this.gnosisSafeService.create(gnosisSafe));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  getSelectedUser(option: IUser, selectedVals?: IUser[]): IUser {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGnosisSafe>>): void {
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

  protected updateForm(gnosisSafe: IGnosisSafe): void {
    this.editForm.patchValue({
      id: gnosisSafe.id,
      name: gnosisSafe.name,
      address: gnosisSafe.address,
      signatures: gnosisSafe.signatures,
      owners: gnosisSafe.owners,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, ...(gnosisSafe.owners ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, ...(this.editForm.get('owners')!.value ?? []))))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IGnosisSafe {
    return {
      ...new GnosisSafe(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      address: this.editForm.get(['address'])!.value,
      signatures: this.editForm.get(['signatures'])!.value,
      owners: this.editForm.get(['owners'])!.value,
    };
  }
}
