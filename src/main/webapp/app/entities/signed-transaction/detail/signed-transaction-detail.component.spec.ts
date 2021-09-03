import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SignedTransactionDetailComponent } from './signed-transaction-detail.component';

describe('Component Tests', () => {
  describe('SignedTransaction Management Detail Component', () => {
    let comp: SignedTransactionDetailComponent;
    let fixture: ComponentFixture<SignedTransactionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SignedTransactionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ signedTransaction: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SignedTransactionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SignedTransactionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load signedTransaction on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.signedTransaction).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
