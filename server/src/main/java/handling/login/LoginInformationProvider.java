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

package handling.login;

import provider.MapleData;
import provider.MapleDataTool;
import server.config.ServerEnvironment;

import java.util.ArrayList;
import java.util.List;

@lombok.extern.slf4j.Slf4j
public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> forbiddenName = new ArrayList<>();

    LoginInformationProvider() {
        log.info("Loading LoginInformationProvider :::");
        final MapleData nameData = ServerEnvironment.getConfig().getDataProvider("wz/Etc").getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            forbiddenName.add(MapleDataTool.getString(data));
        }
    }

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    public boolean isForbiddenName(final String in) {
        for (final String name : forbiddenName) {
            if (in.contains(name)) {
                return true;
            }
        }
        return false;
    }
}
