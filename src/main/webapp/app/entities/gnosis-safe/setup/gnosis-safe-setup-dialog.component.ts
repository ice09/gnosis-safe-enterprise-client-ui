import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';

@Component({
  templateUrl: './gnosis-safe-setup-dialog.component.html',
})
export class GnosisSafeSetupDialogComponent {
  gnosisSafe?: IGnosisSafe;

  constructor(protected gnosisSafeService: GnosisSafeService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmSetup(id: number): void {
    this.gnosisSafeService.setup(id).subscribe(() => {
      this.activeModal.close('setup');
    });
  }
}
