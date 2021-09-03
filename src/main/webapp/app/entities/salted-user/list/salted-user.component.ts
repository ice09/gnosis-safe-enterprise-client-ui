import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISaltedUser } from '../salted-user.model';
import { SaltedUserService } from '../service/salted-user.service';
import { SaltedUserDeleteDialogComponent } from '../delete/salted-user-delete-dialog.component';

@Component({
  selector: 'jhi-salted-user',
  templateUrl: './salted-user.component.html',
})
export class SaltedUserComponent implements OnInit {
  saltedUsers?: ISaltedUser[];
  isLoading = false;

  constructor(protected saltedUserService: SaltedUserService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.saltedUserService.query().subscribe(
      (res: HttpResponse<ISaltedUser[]>) => {
        this.isLoading = false;
        this.saltedUsers = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ISaltedUser): number {
    return item.id!;
  }

  delete(saltedUser: ISaltedUser): void {
    const modalRef = this.modalService.open(SaltedUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.saltedUser = saltedUser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
