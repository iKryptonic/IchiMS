package handling.login.handler;

import client.MapleClient;
import database.CharacterService;
import database.LoginState;
import handling.AbstractMaplePacketHandler;
import handling.ServerMigration;
import handling.world.WorldServer;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

@lombok.extern.slf4j.Slf4j
public class CharSelectedHandler extends AbstractMaplePacketHandler {


    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        final int characterId = slea.readInt();
        String hardwareID = slea.readMapleAsciiString();
        String macAddress = slea.readMapleAsciiString();
        log.info("HardwareID: " + macAddress);
        log.info("MAC: " + hardwareID);
        if (c.tooManyLogin() || !CharacterService.checkIfCharacterExist(c.getAccountData().getId(), characterId)) {
            c.getSession().close();
            return;
        }

        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        WorldServer.getInstance().getMigrationService().putMigrationEntry(new ServerMigration(characterId, c.getAccountData(), c.getSessionIPAddress()));

        c.updateLoginState(LoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.getSession().write(MaplePacketCreator.getServerIP(
                Integer.parseInt(WorldServer.getInstance().getChannel(c.getChannel()).getPublicAddress().split(":")[1]), characterId));

    }

}
