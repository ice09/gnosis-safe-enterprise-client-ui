jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISafeTransaction, SafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';

import { SafeTransactionRoutingResolveService } from './safe-transaction-routing-resolve.service';

describe('Service Tests', () => {
  describe('SafeTransaction routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SafeTransactionRoutingResolveService;
    let service: SafeTransactionService;
    let resultSafeTransaction: ISafeTransaction | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SafeTransactionRoutingResolveService);
      service = TestBed.inject(SafeTransactionService);
      resultSafeTransaction = undefined;
    });

    describe('resolve', () => {
      it('should return ISafeTransaction returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSafeTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSafeTransaction).toEqual({ id: 123 });
      });

      it('should return new ISafeTransaction if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSafeTransaction = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSafeTransaction).toEqual(new SafeTransaction());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSafeTransaction = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSafeTransaction).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
