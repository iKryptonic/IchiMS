package handling.channel.handler;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.channel.handler.utils.InventoryHandlerUtils;
import tools.data.input.SeekableLittleEndianAccessor;

@lombok.extern.slf4j.Slf4j
public class UsePotencialHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        InventoryHandlerUtils.UseUpgradeScroll((byte) slea.readShort(), (byte) slea.readShort(), (byte) 0, c,
                c.getPlayer(), (byte) 1);

    }

}
