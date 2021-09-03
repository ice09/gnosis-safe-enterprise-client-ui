import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISaltedUser } from '../salted-user.model';

@Component({
  selector: 'jhi-salted-user-detail',
  templateUrl: './salted-user-detail.component.html',
})
export class SaltedUserDetailComponent implements OnInit {
  saltedUser: ISaltedUser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saltedUser }) => {
      this.saltedUser = saltedUser;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
