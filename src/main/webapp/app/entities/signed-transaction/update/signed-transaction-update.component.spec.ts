jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SignedTransactionService } from '../service/signed-transaction.service';
import { ISignedTransaction, SignedTransaction } from '../signed-transaction.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ISafeTransaction } from 'app/entities/safe-transaction/safe-transaction.model';
import { SafeTransactionService } from 'app/entities/safe-transaction/service/safe-transaction.service';

import { SignedTransactionUpdateComponent } from './signed-transaction-update.component';

describe('Component Tests', () => {
  describe('SignedTransaction Management Update Component', () => {
    let comp: SignedTransactionUpdateComponent;
    let fixture: ComponentFixture<SignedTransactionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let signedTransactionService: SignedTransactionService;
    let userService: UserService;
    let safeTransactionService: SafeTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SignedTransactionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SignedTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SignedTransactionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      signedTransactionService = TestBed.inject(SignedTransactionService);
      userService = TestBed.inject(UserService);
      safeTransactionService = TestBed.inject(SafeTransactionService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const signedTransaction: ISignedTransaction = { id: 456 };
        const signer: IUser = { id: 38932 };
        signedTransaction.signer = signer;

        const userCollection: IUser[] = [{ id: 48070 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [signer];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call SafeTransaction query and add missing value', () => {
        const signedTransaction: ISignedTransaction = { id: 456 };
        const safeTransaction: ISafeTransaction = { id: 587 };
        signedTransaction.safeTransaction = safeTransaction;

        const safeTransactionCollection: ISafeTransaction[] = [{ id: 19988 }];
        spyOn(safeTransactionService, 'query').and.returnValue(of(new HttpResponse({ body: safeTransactionCollection })));
        const additionalSafeTransactions = [safeTransaction];
        const expectedCollection: ISafeTransaction[] = [...additionalSafeTransactions, ...safeTransactionCollection];
        spyOn(safeTransactionService, 'addSafeTransactionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        expect(safeTransactionService.query).toHaveBeenCalled();
        expect(safeTransactionService.addSafeTransactionToCollectionIfMissing).toHaveBeenCalledWith(
          safeTransactionCollection,
          ...additionalSafeTransactions
        );
        expect(comp.safeTransactionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const signedTransaction: ISignedTransaction = { id: 456 };
        const signer: IUser = { id: 42031 };
        signedTransaction.signer = signer;
        const safeTransaction: ISafeTransaction = { id: 50436 };
        signedTransaction.safeTransaction = safeTransaction;

        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(signedTransaction));
        expect(comp.usersSharedCollection).toContain(signer);
        expect(comp.safeTransactionsSharedCollection).toContain(safeTransaction);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const signedTransaction = { id: 123 };
        spyOn(signedTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: signedTransaction }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(signedTransactionService.update).toHaveBeenCalledWith(signedTransaction);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const signedTransaction = new SignedTransaction();
        spyOn(signedTransactionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: signedTransaction }));
        saveSubject.complete();

        // THEN
        expect(signedTransactionService.create).toHaveBeenCalledWith(signedTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const signedTransaction = { id: 123 };
        spyOn(signedTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ signedTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(signedTransactionService.update).toHaveBeenCalledWith(signedTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSafeTransactionById', () => {
        it('Should return tracked SafeTransaction primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSafeTransactionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
