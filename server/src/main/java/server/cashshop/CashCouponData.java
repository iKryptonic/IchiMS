/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2011 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package server.cashshop;

/**
 * @author AuroX
 */
@lombok.extern.slf4j.Slf4j
public class CashCouponData {

    private final byte type;
    private final int data;
    private final int quantity;

    public CashCouponData(byte type, int data, int quantity) {
        this.type = type;
        this.data = data;
        this.quantity = quantity;
    }

    public int getData() {
        return data;
    }

    public int getQuantity() {
        return quantity;
    }

    public byte getType() {
        return type;
    }
}
