import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SafeTransactionDetailComponent } from './safe-transaction-detail.component';

describe('Component Tests', () => {
  describe('SafeTransaction Management Detail Component', () => {
    let comp: SafeTransactionDetailComponent;
    let fixture: ComponentFixture<SafeTransactionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SafeTransactionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ safeTransaction: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SafeTransactionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SafeTransactionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load safeTransaction on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.safeTransaction).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
