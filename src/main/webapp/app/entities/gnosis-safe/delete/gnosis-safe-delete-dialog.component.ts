import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';

@Component({
  templateUrl: './gnosis-safe-delete-dialog.component.html',
})
export class GnosisSafeDeleteDialogComponent {
  gnosisSafe?: IGnosisSafe;

  constructor(protected gnosisSafeService: GnosisSafeService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gnosisSafeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
