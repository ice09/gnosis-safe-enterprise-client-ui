jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { SafeTransactionService } from '../service/safe-transaction.service';

import { SafeTransactionSubmitDialogComponent } from './safe-transaction-submit-dialog.component';

describe('Component Tests', () => {
  describe('SafeTransaction Management submit Component', () => {
    let comp: SafeTransactionSubmitDialogComponent;
    let fixture: ComponentFixture<SafeTransactionSubmitDialogComponent>;
    let service: SafeTransactionService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SafeTransactionSubmitDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(SafeTransactionSubmitDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SafeTransactionSubmitDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SafeTransactionService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmSubmit', () => {
      it('Should call submit service on confirm submit', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'submit').and.returnValue(of({}));

          // WHEN
          comp.confirmsubmit(123);
          tick();

          // THEN
          expect(service.submit).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('submitted');
        })
      ));

      it('Should not call submit service on clear', () => {
        // GIVEN
        spyOn(service, 'submit');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.submit).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
