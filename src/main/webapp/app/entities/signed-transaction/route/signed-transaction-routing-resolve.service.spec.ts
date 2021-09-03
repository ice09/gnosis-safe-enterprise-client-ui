jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISignedTransaction, SignedTransaction } from '../signed-transaction.model';
import { SignedTransactionService } from '../service/signed-transaction.service';

import { SignedTransactionRoutingResolveService } from './signed-transaction-routing-resolve.service';

describe('Service Tests', () => {
  describe('SignedTransaction routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SignedTransactionRoutingResolveService;
    let service: SignedTransactionService;
    let resultSignedTransaction: ISignedTransaction | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SignedTransactionRoutingResolveService);
      service = TestBed.inject(SignedTransactionService);
      resultSignedTransaction = undefined;
    });

    describe('resolve', () => {
      it('should return ISignedTransaction returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSignedTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSignedTransaction).toEqual({ id: 123 });
      });

      it('should return new ISignedTransaction if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSignedTransaction = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSignedTransaction).toEqual(new SignedTransaction());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSignedTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSignedTransaction).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
