package scripting.v1.game;

import client.MapleClient;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Scriptable;
import scripting.ScriptMan;
import scripting.v1.game.helper.AskAvatarHelper;
import scripting.v1.game.helper.ApiClass;
import server.life.MapleNPC;
import tools.packet.npcpool.NpcPoolPackets;

@Slf4j
public class NpcScripting extends PlayerScripting {

    private final int npc;
    protected Scriptable globalScope;


    public NpcScripting(int npc, MapleClient client, Scriptable globalScope) {
        super(client);
        this.npc = npc;
        this.globalScope = globalScope;
    }

    protected void throwContinuation(Context cx) {
        try {
            throw cx.captureContinuation();
        } finally {
            Context.exit();
        }
    }

    @ApiClass
    public Object getContinuation() {
        return continuation;
    }

    @ApiClass
    public void setContinuation(Object continuation) {
        this.continuation = continuation;
    }

    public void resume(Object obj) {
        Context cx = Context.enter();
        try {
            cx.resumeContinuation(continuation, globalScope, obj);
        } catch (ContinuationPending pending) {
            this.setContinuation(pending.getContinuation());
        } finally {
            Context.exit();
        }
    }

    @ApiClass
    public void say(String text) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnSay(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text), false, true));
        throwContinuation(cx);
    }

    @ApiClass
    public void sayUser(String text) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnSay(ScriptMan.NpcReplacedByUser, npc, (byte) 2, parseText(text), false, true));
        throwContinuation(cx);
    }

    @ApiClass
    public void sayOk(String text) {
        Context cx = Context.enter();
        getClient().sendPacket(ScriptMan.OnSay(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text), false, false));
        throwContinuation(cx);
    }

    @ApiClass
    public void sayOkUser(String text) {
        Context cx = Context.enter();
        getClient().sendPacket(ScriptMan.OnSay(ScriptMan.NpcReplayedByNpc, npc, (byte) 2, parseText(text), false, false));
        throwContinuation(cx);
    }


    @ApiClass
    public void askYesNo(String text) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskYesNo(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text)));
        throwContinuation(cx);
    }

    @ApiClass
    public void askYesUser(String text) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskYesNo(ScriptMan.NpcReplayedByNpc, npc, (byte) 2, parseText(text)));
        throwContinuation(cx);
    }

    @ApiClass
    public void askAccept(String text) {
        Context cx = Context.enter();
        if (text.contains("#L")) { // will dc otherwise!
            askMenu(text);
            return;
        }
        sendPacket(ScriptMan.OnAskAccept(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text)));
        throwContinuation(cx);
    }

    @ApiClass
    public void askAcceptUser(String text) {
        Context cx = Context.enter();
        if (text.contains("#L")) { // will dc otherwise!
            askMenu(text);
            return;
        }
        sendPacket(ScriptMan.OnAskAccept(ScriptMan.NpcReplayedByNpc, npc, (byte) 2, parseText(text)));
        throwContinuation(cx);
    }


    @ApiClass
    public int askAvatar(String text, int item, int... styles) {
        Context cx = Context.enter();
        player.addTemporaryData("askAvatar", styles);
        player.addTemporaryData("askAvatarItem", item);
        sendPacket(ScriptMan.OnAskAvatar(ScriptMan.NpcReplayedByNpc, npc, parseText(text), styles));
        throwContinuation(cx);
        return 1;
    }

    @ApiClass
    public int makeRandAvatar(int item, int... styles) {
        player.addTemporaryData("askAvatar", styles);
        player.addTemporaryData("askAvatarItem", item);
        return AskAvatarHelper.processAskAvatar(player, 1, true);
    }

    @ApiClass
    public void askMenu(String text) {
        Context cx = Context.enter();
        if (!text.contains("#L")) { // sendSimple will dc otherwise!
            say(text);
            return;
        }
        sendPacket(ScriptMan.OnAskMenu(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text)));
        throwContinuation(cx);
    }

    @ApiClass
    public void askMenuUser(String text) {
        Context cx = Context.enter();
        if (!text.contains("#L")) { // sendSimple will dc otherwise!
            sayUser(text);
            return;
        }
        sendPacket(ScriptMan.OnAskMenu(ScriptMan.NpcReplacedByUser, npc, (byte) 2, parseText(text)));
        throwContinuation(cx);
    }


    @ApiClass
    public void askText(String text, String def, int col, int line) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskText(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text), def, col, line));
        throwContinuation(cx);
    }

    @ApiClass
    public void askTextUser(String text, String def, int col, int line) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskText(ScriptMan.NpcReplayedByNpc, npc, (byte) 2, parseText(text), def, col, line));
        throwContinuation(cx);
    }


    @ApiClass
    public void askNumber(String text, int def, int min, int max) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskNumber(ScriptMan.NpcReplayedByNpc, npc, (byte) 0, parseText(text), def, min, max));
        throwContinuation(cx);
    }

    @ApiClass
    public void askNumberUser(String text, int def, int min, int max) {
        Context cx = Context.enter();
        sendPacket(ScriptMan.OnAskNumber(ScriptMan.NpcReplayedByNpc, npc, (byte) 2, parseText(text), def, min, max));
        throwContinuation(cx);
    }

    @ApiClass
    public void setSpecialAction(int npcId, String action) {
        MapleNPC npcInstance = player.getMap().getNPCById(npcId);
        sendPacket(NpcPoolPackets.setSpecialAction(npcInstance, action));
    }

    //Replaces stuff based on gender.
    private String parseText(String text) {
        String finalText = text.replace("#gender#", player.getGender() == 0 ? "boy" : "girl");
        MapleNPC npcObject = player.getMap().getNPCById(npc);
        if (npcObject != null) {
            finalText = finalText.replace("#npcname#", npcObject.getName());
        }
        finalText = finalText.replace("#level#", "Lvl." + String.valueOf(player.getLevel()));
        finalText = finalText.replace("#job#", player.getJob().getName());
        finalText = finalText.replace("#street#", player.getMap().getStreetName());
        finalText = finalText.replace("#mapname#", player.getMap().getMapName());
        finalText = finalText.replace("#name#", player.getName());

        return finalText;
    }


}
