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

package scripting;

import client.MapleClient;
import scripting.v1.event.EventCenter;
import scripting.v1.event.EventInstance;
import server.MaplePortal;
import server.quest.MapleQuest;

@lombok.extern.slf4j.Slf4j
public class PortalPlayerInteraction extends AbstractPlayerInteraction {

    private final MaplePortal portal;

    public PortalPlayerInteraction(final MapleClient c, final MaplePortal portal) {
        super(c, portal.getId(), c.getPlayer().getMapId());
        this.portal = portal;
    }

    public final MaplePortal getPortal() {
        return portal;
    }

    public final void inFreeMarket() {
        if (getMapId() != 910000000) {
            saveLocation("FREE_MARKET");
            playPortalSE();
            warp(910000000, "out00");
        }
    }

    // summon one monster on reactor location
    @Override
    public void spawnMonster(int id) {
        spawnMonster(id, 1, portal.getPosition());
    }

    // summon monsters on reactor location
    @Override
    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, portal.getPosition());
    }

    public void completeQuest(int id, int npcId) {
        MapleQuest.getInstance(id).complete(getPlayer(), npcId);
    }

    public void registerEvent(String eventName) {
        EventCenter center = getPlayer().getChannelServer().getEventCenter();
        EventInstance instance = center.register(eventName, getPlayer());
        instance.onEventStart();
    }
}
