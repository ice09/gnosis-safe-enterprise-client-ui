import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SaltedUserService } from '../service/salted-user.service';

import { SaltedUserComponent } from './salted-user.component';

describe('Component Tests', () => {
  describe('SaltedUser Management Component', () => {
    let comp: SaltedUserComponent;
    let fixture: ComponentFixture<SaltedUserComponent>;
    let service: SaltedUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SaltedUserComponent],
      })
        .overrideTemplate(SaltedUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SaltedUserComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SaltedUserService);

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
      expect(comp.saltedUsers?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
