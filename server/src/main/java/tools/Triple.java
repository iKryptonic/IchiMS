/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tools;

import java.io.Serializable;

@lombok.extern.slf4j.Slf4j
public class Triple<E, F, G> implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    public E left;
    public F mid;
    public G right;

    public Triple(E left, F mid, G right) {
        this.left = left;
        this.mid = mid;
        this.right = right;
    }

    public E getLeft() {
        return left;
    }

    public F getMid() {
        return mid;
    }

    public G getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Left: " + left.toString() + " Mid: " + mid.toString() + " Right:" + right.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((mid == null) ? 0 : mid.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked") final Triple<E, F, G> other = (Triple<E, F, G>) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (mid == null) {
            if (other.mid != null) {
                return false;
            }
        } else if (!mid.equals(other.mid)) {
            return false;
        }
        if (right == null) {
            return other.right == null;
        } else return right.equals(other.right);
    }
}
