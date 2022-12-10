package database;

import client.TeleportRock;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class TeleportRockService {


    public static void save(TeleportRock teleportRock, int characterId) {
        var con = DatabaseConnection.getConnection();
        if (!teleportRock.isChanged()) {
            return;
        }
        try {
            CharacterService.deleteWhereCharacterId(con, "DELETE FROM " + teleportRock.getName() + " WHERE characterid = ?", characterId);
            for (var map_id : teleportRock.getMap_ids()) {
                var ps = con.prepareStatement("INSERT INTO " + teleportRock.getName() + " (characterid, mapid) VALUES(?, ?) ");
                ps.setInt(1, characterId);
                ps.setInt(2, map_id);
                ps.execute();
                ps.close();
            }
        } catch (SQLException e) {
            log.error("Could not save teleport rocks", e);
        }
    }
}