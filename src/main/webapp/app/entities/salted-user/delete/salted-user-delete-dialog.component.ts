import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISaltedUser } from '../salted-user.model';
import { SaltedUserService } from '../service/salted-user.service';

@Component({
  templateUrl: './salted-user-delete-dialog.component.html',
})
export class SaltedUserDeleteDialogComponent {
  saltedUser?: ISaltedUser;

  constructor(protected saltedUserService: SaltedUserService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.saltedUserService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
