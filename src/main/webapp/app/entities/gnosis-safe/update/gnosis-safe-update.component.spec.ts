jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { GnosisSafeService } from '../service/gnosis-safe.service';
import { IGnosisSafe, GnosisSafe } from '../gnosis-safe.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { GnosisSafeUpdateComponent } from './gnosis-safe-update.component';

describe('Component Tests', () => {
  describe('GnosisSafe Management Update Component', () => {
    let comp: GnosisSafeUpdateComponent;
    let fixture: ComponentFixture<GnosisSafeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let gnosisSafeService: GnosisSafeService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [GnosisSafeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(GnosisSafeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GnosisSafeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      gnosisSafeService = TestBed.inject(GnosisSafeService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const gnosisSafe: IGnosisSafe = { id: 456 };
        const owners: IUser[] = [{ id: 8136 }];
        gnosisSafe.owners = owners;

        const userCollection: IUser[] = [{ id: 62330 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [...owners];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ gnosisSafe });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const gnosisSafe: IGnosisSafe = { id: 456 };
        const owners: IUser = { id: 48385 };
        gnosisSafe.owners = [owners];

        activatedRoute.data = of({ gnosisSafe });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(gnosisSafe));
        expect(comp.usersSharedCollection).toContain(owners);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const gnosisSafe = { id: 123 };
        spyOn(gnosisSafeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ gnosisSafe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: gnosisSafe }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(gnosisSafeService.update).toHaveBeenCalledWith(gnosisSafe);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const gnosisSafe = new GnosisSafe();
        spyOn(gnosisSafeService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ gnosisSafe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: gnosisSafe }));
        saveSubject.complete();

        // THEN
        expect(gnosisSafeService.create).toHaveBeenCalledWith(gnosisSafe);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const gnosisSafe = { id: 123 };
        spyOn(gnosisSafeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ gnosisSafe });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(gnosisSafeService.update).toHaveBeenCalledWith(gnosisSafe);
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
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedUser', () => {
        it('Should return option if no User is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedUser(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected User for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedUser(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this User is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedUser(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
