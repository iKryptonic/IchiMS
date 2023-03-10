package client;

import lombok.Getter;
import tools.data.output.MaplePacketLittleEndianWriter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class WishList {

    private Set<Integer> items;

    @Getter
    private boolean changed;

    public WishList() {
        this.items = new LinkedHashSet<>(10);
    }

    public void addItem(int item) {
        items.add(item);
        changed = true;
    }


    public void setItem(int item) {
        items.add(item);
    }

    public void clear() {
        changed = true;
        items.clear();
    }

    public void encodeToCharInfo(MaplePacketLittleEndianWriter mplew) {
        mplew.write(items.size());
        if (items.size() > 0) {
            for (var item : items) {
                mplew.writeInt(item);
            }
        }
    }


    public void encodeToCashShop(MaplePacketLittleEndianWriter mplew) {
        var list = toArray();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
    }

    public Set<Integer> getItems() {
        return items;
    }

    public void update(int[] wishlist) {
        Arrays.stream(wishlist).forEach(this::addItem);
    }

    private int[] toArray() {
        var list = new int[10];
        var src = items.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        System.arraycopy(src, 0, list, 0, src.length);
        return list;
    }
}
