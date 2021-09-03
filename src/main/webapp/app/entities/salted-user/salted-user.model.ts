import { IUser } from 'app/entities/user/user.model';

export interface ISaltedUser {
  id?: number;
  salt?: string | null;
  address?: string | null;
  user?: IUser;
}

export class SaltedUser implements ISaltedUser {
  constructor(public id?: number, public salt?: string | null, public address?: string | null, public user?: IUser) {}
}

export function getSaltedUserIdentifier(saltedUser: ISaltedUser): number | undefined {
  return saltedUser.id;
}
