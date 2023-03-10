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

package client.layout;

import database.DatabaseConnection;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Slf4JSqlLogger;
import tools.data.output.MaplePacketLittleEndianWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@lombok.extern.slf4j.Slf4j
public class MapleKeyLayout implements Serializable {

    public static final int[] key = {2, 3, 4, 5, 6, 7, 8, 16, 17, 18, 19, 20, 23, 24, 25, 26, 27, 29, 31, 33, 34, 35, 37, 38,
            39, 40, 41, 43, 44, 45, 46, 48, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65};
    public static final int[] KeyType = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6};
    public static final int[] action = {10, 12, 13, 18, 24, 21, 29, 8, 5, 0, 4, 28, 1, 25, 19, 14, 15, 52, 2, 26, 17, 11, 3,
            20, 27, 16, 23, 9, 50, 51, 6, 22, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106};

    private static final long serialVersionUID = 9179541993413738569L;
    private final Map<Integer, KeyMapBinding> keyMapBindings;
    private final int characterId;


    public MapleKeyLayout(int characterId) {
        this.characterId = characterId;
        keyMapBindings = new ConcurrentHashMap<>();
    }


    public void encode(final MaplePacketLittleEndianWriter mplew) {
        for (int x = 0; x < 90; x++) {
            var binding = keyMapBindings.get(Integer.valueOf(x));
            if (binding != null) {
                mplew.write(binding.getType());
                mplew.writeInt(binding.getAction());
            } else {
                mplew.write(0);
                mplew.writeInt(0);
            }
        }
    }

    public final void saveKeys() {
        try (var con = DatabaseConnection.getConnection()) {
            Jdbi jDbi = Jdbi.create(con);
            jDbi.setSqlLogger(new Slf4JSqlLogger());
            for (var binding : new ArrayList<>(keyMapBindings.values())) {
                if (binding.isDeleted()) {
                    log.debug("Deleting key: {}", binding.getKey());
                    jDbi.withHandle(r -> {
                        r.createUpdate("DELETE FROM keymap WHERE `characterid` = :d.characterId AND `key` = :d.key")
                                .bindBean("d", binding)
                                .execute();
                        return true;
                    });
                    keyMapBindings.remove(binding.getKey());
                }
            }
            for (var binding : keyMapBindings.values()) {
                if (binding.isChanged()) {
                    log.debug("Saving key: {}", binding.getKey());
                    jDbi.withHandle(r -> {
                        r.createUpdate("INSERT INTO keymap(`characterid`, `key`, `type`, `action`, `fixed`) " +
                                        "VALUES (:d.characterId, :d.key, :d.type, :d.action, :d.fixed) " +
                                        "ON DUPLICATE KEY UPDATE `type` = :d.type, `action` = :d.action,`fixed` = :d.fixed")
                                .bindBean("d", binding)
                                .execute();
                        return true;
                    });
                    binding.setChanged(false);
                    binding.setDeleted(false);
                }
            }
        } catch (Exception ex) {
            log.error("Error saving key map", ex);
        }

    }

    public void setBinding(KeyMapBinding binding) {
        keyMapBindings.put(binding.getKey(), binding);
    }

    public void setDefaultKeys() {
        for (int i = 0; i < key.length; i++) {
            if (ExcludedKeyMap.fromKeyValue(action[i]) != null) {
                continue;
            }
            KeyMapBinding binding = new KeyMapBinding(characterId, key[i], (byte) KeyType[i], action[i], 0);
            binding.setChanged(true);
            setBinding(binding);
        }
    }

}
