package handling.login.handler;

import client.MapleClient;
import database.LoginResult;
import handling.MaplePacketHandler;
import handling.login.LoginServer;
import lombok.extern.slf4j.Slf4j;
import tools.KoreanDateUtil;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

import java.util.Calendar;

@Slf4j
public class CharLoginPasswordHandler implements MaplePacketHandler {


    private String normalizeStringPassword(String password) {
        if (password == null) {
            return "";
        }
        return password.replace("\r", "");
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        final String login = slea.readMapleAsciiString();
        final String pwd = normalizeStringPassword(slea.readMapleAsciiString());

        LoginResult result = c.login(login, pwd);

        if (result.isLoginError()) {
            c.getSession().write(LoginPacket.getLoginFailed(result.getResult()));
            return;
        }

        final Calendar tempBannedUntil = result.getAccountData().getTempBanCalendar();

        if (tempBannedUntil.getTimeInMillis() != 0) {
            if (!c.tooManyLogin()) {
                c.getSession().write(LoginPacket.getTempBan(
                        KoreanDateUtil.getTempBanTimestamp(tempBannedUntil.getTimeInMillis()), c.getAccountData().getGreason()));
            }
        } else {
            c.resetLoginCount();
            c.setAccountData(result.getAccountData());
            LoginServer.getInstance().registerClient(c);
        }

    }

    @Override
    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }

}
