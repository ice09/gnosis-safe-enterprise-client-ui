jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SafeTransactionService } from '../service/safe-transaction.service';
import { ISafeTransaction, SafeTransaction } from '../safe-transaction.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGnosisSafe } from 'app/entities/gnosis-safe/gnosis-safe.model';
import { GnosisSafeService } from 'app/entities/gnosis-safe/service/gnosis-safe.service';

import { SafeTransactionUpdateComponent } from './safe-transaction-update.component';

describe('Component Tests', () => {
  describe('SafeTransaction Management Update Component', () => {
    let comp: SafeTransactionUpdateComponent;
    let fixture: ComponentFixture<SafeTransactionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let safeTransactionService: SafeTransactionService;
    let userService: UserService;
    let gnosisSafeService: GnosisSafeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SafeTransactionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SafeTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SafeTransactionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      safeTransactionService = TestBed.inject(SafeTransactionService);
      userService = TestBed.inject(UserService);
      gnosisSafeService = TestBed.inject(GnosisSafeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const safeTransaction: ISafeTransaction = { id: 456 };
        const creator: IUser = { id: 2487 };
        safeTransaction.creator = creator;

        const userCollection: IUser[] = [{ id: 97240 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [creator];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call GnosisSafe query and add missing value', () => {
        const safeTransaction: ISafeTransaction = { id: 456 };
        const gnosisSafe: IGnosisSafe = { id: 7625 };
        safeTransaction.gnosisSafe = gnosisSafe;

        const gnosisSafeCollection: IGnosisSafe[] = [{ id: 55223 }];
        spyOn(gnosisSafeService, 'query').and.returnValue(of(new HttpResponse({ body: gnosisSafeCollection })));
        const additionalGnosisSafes = [gnosisSafe];
        const expectedCollection: IGnosisSafe[] = [...additionalGnosisSafes, ...gnosisSafeCollection];
        spyOn(gnosisSafeService, 'addGnosisSafeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        expect(gnosisSafeService.query).toHaveBeenCalled();
        expect(gnosisSafeService.addGnosisSafeToCollectionIfMissing).toHaveBeenCalledWith(gnosisSafeCollection, ...additionalGnosisSafes);
        expect(comp.gnosisSafesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const safeTransaction: ISafeTransaction = { id: 456 };
        const creator: IUser = { id: 53039 };
        safeTransaction.creator = creator;
        const gnosisSafe: IGnosisSafe = { id: 8808 };
        safeTransaction.gnosisSafe = gnosisSafe;

        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(safeTransaction));
        expect(comp.usersSharedCollection).toContain(creator);
        expect(comp.gnosisSafesSharedCollection).toContain(gnosisSafe);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const safeTransaction = { id: 123 };
        spyOn(safeTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: safeTransaction }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(safeTransactionService.update).toHaveBeenCalledWith(safeTransaction);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const safeTransaction = new SafeTransaction();
        spyOn(safeTransactionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: safeTransaction }));
        saveSubject.complete();

        // THEN
        expect(safeTransactionService.create).toHaveBeenCalledWith(safeTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const safeTransaction = { id: 123 };
        spyOn(safeTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ safeTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(safeTransactionService.update).toHaveBeenCalledWith(safeTransaction);
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

      describe('trackGnosisSafeById', () => {
        it('Should return tracked GnosisSafe primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackGnosisSafeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
