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

package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleQuestStatus;
import client.skill.SkillFactory;
import constants.ServerConstants;
import database.LoginState;
import handling.ServerMigration;
import handling.channel.ChannelServer;
import handling.channel.handler.utils.PartyHandlerUtils.PartyOperation;
import handling.world.WorldServer;
import handling.world.alliance.AllianceManager;
import handling.world.buddy.BuddyListEntry;
import handling.world.buddy.BuddyManager;
import handling.world.expedition.MapleExpedition;
import handling.world.guild.GuildManager;
import handling.world.guild.MapleGuild;
import handling.world.helper.CharacterIdChannelPair;
import handling.world.helper.CharacterTransfer;
import handling.world.helper.FindCommand;
import handling.world.helper.MapleMessenger;
import handling.world.helper.MapleMessengerCharacter;
import handling.world.messenger.MessengerManager;
import handling.world.party.MapleParty;
import handling.world.party.MaplePartyCharacter;
import handling.world.party.PartyManager;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.MapleUserPackets;
import tools.packet.ReportPackets;

import java.util.ArrayList;
import java.util.List;

@lombok.extern.slf4j.Slf4j
public class InterServerHandler {


    public static final void onLoggedIn(final int characterId, final MapleClient c) {
        final ChannelServer channelServer = c.getChannelServer();

        ServerMigration serverMigration = WorldServer.getInstance().getMigrationService().getServerMigration(characterId, c.getSessionIPAddress());
        if (serverMigration != null) {
            c.setAccountData(serverMigration.getAccountData());
        } else {
            log.error("Missing server migration: {}", c.getAccountData().getName());
            return;
        }

        MapleCharacter player;
        final CharacterTransfer transfer = serverMigration.getCharacterTransfer();

        if (transfer == null) { // Logged for the first time
            player = MapleCharacter.loadCharFromDB(characterId, c, true);
            player.setLoginTime(System.currentTimeMillis());
        } else {
            player = MapleCharacter.reconstructChr(transfer, c, true);
        }
        c.setPlayer(player);

        c.updateLoginState(LoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);

        c.getSession().write(MaplePacketCreator.getCharInfo(player));
        if (player.isGameMaster()) {
            SkillFactory.getSkill(9101004).getEffect(1).applyTo(player);
        }
        player.sendSkills();
        c.getSession().write(MaplePacketCreator.temporaryStats_Reset());


        try {
            if (serverMigration != null) {
                player.silentGiveBuffs(serverMigration.getBuffsFromStorage(player.getId()));
                player.giveCoolDowns(serverMigration.getCooldownsFromStorage(player.getId()));
                player.giveSilentDebuff(serverMigration.getDiseaseFromStorage(player.getId()));
            }


            // Start of buddylist
            final int[] buddyIds = player.getBuddyList().getBuddyIds();
            BuddyManager.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                player.receivePartyMemberHP();
                player.updatePartyMemberHP();

                final MapleParty party = player.getParty();
                PartyManager.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
                if (party != null && party.getExpeditionId() > 0) {
                    MapleExpedition me = PartyManager.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.getSession().write(MapleUserPackets.showExpedition(me, false, true));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = FindCommand.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                final BuddyListEntry ble = player.getBuddyList().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddyList().put(ble);
            }
            c.getSession().write(MaplePacketCreator.updateBuddylist(BuddyListModifyHandler.UPDATE, player.getBuddyList().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                MessengerManager.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                MessengerManager.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                GuildManager.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(MaplePacketCreator.showGuildInfo(player));
                final MapleGuild gs = GuildManager.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = AllianceManager.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (byte[] pack : packetList) {
                            if (pack != null) {
                                c.getSession().write(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }


        } catch (Exception e) {
            log.info("Log_Login_Error.rtf", e);
        }
        player.getSkillMacros().sendMacros(c);
        player.showNote();
        player.updatePartyMemberHP();
        if (transfer == null) { // Login
            player.startFairySchedule();
        }

        c.getSession().write(MaplePacketCreator.getKeymap(player.getKeyLayout()));
        c.sendPing();


        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.getSession().write(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        player.expirationTask(true, transfer == null);
        if (player.getJob().equals(MapleJob.DARKKNIGHT)) {
            player.checkBerserk();
        }
        player.spawnSavedPets();
        c.getSession().write(MaplePacketCreator.getQuickSlot("42,82,71,73,29,83,79,81"));
        player.updatePetAuto();


        if (ServerConstants.SHOP_DISCOUNT) {
            c.getSession().write(MaplePacketCreator.enableShopDiscount((byte) ServerConstants.SHOP_DISCOUNT_PERCENT));
        }

        final List<Integer> ii = new ArrayList<>();
        ii.add(9250130);
        ii.add(9250131);
        ii.add(1013103);
        ii.add(2006);
        ii.add(2082005);
        ii.add(9010014);
        ii.add(2060103);
        ii.add(1052119);
        ii.add(9270064);
        ii.add(1013101);
        ii.add(1300005);
        ii.add(2082010);
        ii.add(9201137);
        ii.add(9201136);
        ii.add(9010010);
        ii.add(9010011);//Mushrom PQ  in henesys
        MapleQuest quest = MapleQuest.getInstance(7103);
        if (quest != null && c.getPlayer().getQuest(quest).getStatus() == 2) {
            ii.add(2041021);
        }


        c.getPlayer().getClient().getSession().write(MaplePacketCreator.setNPCScriptable(1201002, "Buy new skills"));
        player.maxMastery();//Necessary for now. TODO: Remove max mastery from login
        player.getClient().getSession().write(MaplePacketCreator.setNPCScriptable(ii));
        player.getMap().addPlayer(player);
        player.getClient().sendPacket(ReportPackets.enableReport());
        player.getClient().enableActions();


    }


}
