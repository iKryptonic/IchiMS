package tools.packet.cfield;

import tools.data.output.MaplePacketLittleEndianWriter;

@lombok.extern.slf4j.Slf4j
public class CFieldBattleField {

    public byte[] OnScoreUpdate(int wolves, int sheeps) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(348);
        mplew.write(wolves);
        mplew.write(sheeps);
        return mplew.getPacket();
    }
}
