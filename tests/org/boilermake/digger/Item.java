package org.boilermake.digger;

import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Item {
    private Vector position;
    private Vector size;
    private int itemType;

    public static class ItemType{
        public final static int SWORD = 1;
        public final static int GUN = 2;
    }

    public Item(int xpos, int ypos, int xsize, int ysize, int itemType) {
        this.position = new Vector(xpos, ypos);
        this.size = new Vector(xsize, ysize);
        this.itemType = itemType;
    }

    public Item(int xpos, int ypos, int itemType) {
        this.position = new Vector(xpos, ypos);
        this.size = new Vector(16, 9);
        this.itemType = itemType;
    }

    public Vector getPosition() {
        return position;
    }

    public int getItemType() {
        return itemType;
    }
}
