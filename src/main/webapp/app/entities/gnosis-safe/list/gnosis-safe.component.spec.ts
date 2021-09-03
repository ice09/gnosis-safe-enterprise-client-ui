import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GnosisSafeService } from '../service/gnosis-safe.service';

import { GnosisSafeComponent } from './gnosis-safe.component';

describe('Component Tests', () => {
  describe('GnosisSafe Management Component', () => {
    let comp: GnosisSafeComponent;
    let fixture: ComponentFixture<GnosisSafeComponent>;
    let service: GnosisSafeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [GnosisSafeComponent],
      })
        .overrideTemplate(GnosisSafeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GnosisSafeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(GnosisSafeService);

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
      expect(comp.gnosisSafes?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
