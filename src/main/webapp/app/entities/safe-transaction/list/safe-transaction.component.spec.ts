import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SafeTransactionService } from '../service/safe-transaction.service';

import { SafeTransactionComponent } from './safe-transaction.component';

describe('Component Tests', () => {
  describe('SafeTransaction Management Component', () => {
    let comp: SafeTransactionComponent;
    let fixture: ComponentFixture<SafeTransactionComponent>;
    let service: SafeTransactionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SafeTransactionComponent],
      })
        .overrideTemplate(SafeTransactionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SafeTransactionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SafeTransactionService);

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
      expect(comp.safeTransactions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
