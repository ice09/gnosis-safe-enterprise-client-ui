import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISaltedUser, SaltedUser } from '../salted-user.model';
import { SaltedUserService } from '../service/salted-user.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-salted-user-update',
  templateUrl: './salted-user-update.component.html',
})
export class SaltedUserUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    salt: [null, Validators.required],
    address: [],
    user: [],
  });

  constructor(
    protected saltedUserService: SaltedUserService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saltedUser }) => {
      this.updateForm(saltedUser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const saltedUser = this.createFromForm();
    if (saltedUser.id !== undefined) {
      this.subscribeToSaveResponse(this.saltedUserService.update(saltedUser));
    } else {
      this.subscribeToSaveResponse(this.saltedUserService.create(saltedUser));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISaltedUser>>): void {
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

  protected updateForm(saltedUser: ISaltedUser): void {
    this.editForm.patchValue({
      id: saltedUser.id,
      salt: saltedUser.salt,
      address: saltedUser.address,
      user: saltedUser.user,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, saltedUser.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): ISaltedUser {
    return {
      ...new SaltedUser(),
      id: this.editForm.get(['id'])!.value,
      salt: this.editForm.get(['salt'])!.value,
      address: this.editForm.get(['address'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
