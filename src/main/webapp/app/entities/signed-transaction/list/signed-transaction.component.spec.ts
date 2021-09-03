import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SignedTransactionService } from '../service/signed-transaction.service';

import { SignedTransactionComponent } from './signed-transaction.component';

describe('Component Tests', () => {
  describe('SignedTransaction Management Component', () => {
    let comp: SignedTransactionComponent;
    let fixture: ComponentFixture<SignedTransactionComponent>;
    let service: SignedTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SignedTransactionComponent],
      })
        .overrideTemplate(SignedTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SignedTransactionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SignedTransactionService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.signedTransactions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
