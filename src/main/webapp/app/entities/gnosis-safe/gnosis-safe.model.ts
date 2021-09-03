import { ISafeTransaction } from 'app/entities/safe-transaction/safe-transaction.model';
import { IUser } from 'app/entities/user/user.model';

export interface IGnosisSafe {
  id?: number;
  name?: string;
  address?: string;
  signatures?: number;
  transactions?: ISafeTransaction[] | null;
  owners?: IUser[];
}

export class GnosisSafe implements IGnosisSafe {
  constructor(
    public id?: number,
    public name?: string,
    public address?: string,
    public signatures?: number,
    public transactions?: ISafeTransaction[] | null,
    public owners?: IUser[]
  ) {}
}

export function getGnosisSafeIdentifier(gnosisSafe: IGnosisSafe): number | undefined {
  return gnosisSafe.id;
}
