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

package server.maps;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import handling.world.WorldServer;
import scripting.PortalScriptManager;
import server.MaplePortal;
import tools.MaplePacketCreator;

import java.awt.*;

@lombok.extern.slf4j.Slf4j
public class MapleGenericPortal implements MaplePortal {

    private final int type;
    private String name, target, scriptName;
    private Point position;
    private int targetmap;
    private int id;
    private boolean portalState = true;

    public MapleGenericPortal(final int type) {
        this.type = type;
    }

    @Override
    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public final Point getPosition() {
        return position;
    }

    public final void setPosition(final Point position) {
        this.position = position;
    }

    @Override
    public final String getTarget() {
        return target;
    }

    public final void setTarget(final String target) {
        this.target = target;
    }

    @Override
    public final int getTargetMapId() {
        return targetmap;
    }

    public final void setTargetMapId(final int targetmapid) {
        this.targetmap = targetmapid;
    }

    @Override
    public final int getType() {
        return type;
    }

    @Override
    public final String getScriptName() {
        return scriptName;
    }

    @Override
    public final void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public final void enterPortal(final MapleClient c) {
        if (getPosition().distanceSq(c.getPlayer().getPosition()) > 22500) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
        }
        final MapleMap currentmap = c.getPlayer().getMap();
        if (portalState || c.getPlayer().isGameMaster()) {
            if (getScriptName() != null) {
                c.getPlayer().checkFollow();
                try {
                    PortalScriptManager.getInstance().executePortalScript(this, c);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else if (getTargetMapId() != 999999999) {
                final MapleMap to = WorldServer.getInstance().getChannel(c.getChannel()).getMapFactory().getMap(getTargetMapId());
                c.getPlayer().changeMapPortal(to, to.getPortal(getTarget()) == null ? to.getPortal(0) : to.getPortal(getTarget())); //late resolving makes this harder but prevents us from loading the whole world at once
            }
        }
        if (c != null && c.getPlayer() != null && c.getPlayer().getMap() == currentmap) { // Character is still on the same map.
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    @Override
    public boolean getPortalState() {
        return portalState;
    }

    @Override
    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}
