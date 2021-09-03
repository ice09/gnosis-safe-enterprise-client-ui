jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { GnosisSafeService } from '../service/gnosis-safe.service';

import { GnosisSafeSetupDialogComponent } from './gnosis-safe-setup-dialog.component';

describe('Component Tests', () => {
  describe('GnosisSafe Management Setup Component', () => {
    let comp: GnosisSafeSetupDialogComponent;
    let fixture: ComponentFixture<GnosisSafeSetupDialogComponent>;
    let service: GnosisSafeService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [GnosisSafeSetupDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(GnosisSafeSetupDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GnosisSafeSetupDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(GnosisSafeService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmSetup', () => {
      it('Should call setup service on confirmSetup', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'setup').and.returnValue(of({}));

          // WHEN
          comp.confirmSetup(123);
          tick();

          // THEN
          expect(service.setup).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('setupd');
        })
      ));

      it('Should not call setup service on clear', () => {
        // GIVEN
        spyOn(service, 'setup');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.setup).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
