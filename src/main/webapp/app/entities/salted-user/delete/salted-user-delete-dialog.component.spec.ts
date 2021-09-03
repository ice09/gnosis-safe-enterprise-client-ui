jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { SaltedUserService } from '../service/salted-user.service';

import { SaltedUserDeleteDialogComponent } from './salted-user-delete-dialog.component';

describe('Component Tests', () => {
  describe('SaltedUser Management Delete Component', () => {
    let comp: SaltedUserDeleteDialogComponent;
    let fixture: ComponentFixture<SaltedUserDeleteDialogComponent>;
    let service: SaltedUserService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SaltedUserDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(SaltedUserDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SaltedUserDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SaltedUserService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
