package handling.channel.handler;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.cashshop.CashShopOperationHandlers;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;

@lombok.extern.slf4j.Slf4j
public class TwinDragonEggHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        final int uniqueId = (int) slea.readLong();
        c.getSession().write(MTSCSPacket.showTwinDragonEgg(uniqueId));
        CashShopOperationHandlers.doCSPackets(c);

    }

}
