package server.maps.event;

import client.MapleCharacter;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

import java.awt.*;

@lombok.extern.slf4j.Slf4j
public class PuppeteerMapEvent extends AbstractMapEvent {


    public PuppeteerMapEvent(MapleMap map) {
        super(map);

    }


    @Override
    public void onUserEnter(MapleCharacter c) {
        if (!c.getJob().isAran()) {
            return;
        }
        map.broadcastMessage(MaplePacketCreator.getClock(10 * 60));
        map.spawnNpc(1104000, new Point(615, 249));

    }

    @Override
    public void onUserExit(MapleCharacter c) {
        map.killAllMonsters(true);

    }

}
