package org.boilermake.digger;

import org.joml.Vector2f;
import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Item {
    private Vector2f position;
    private Vector2f velocity;
    private final Vector2f acceleration = new Vector2f(0, 2);
    private Vector2f size;
    private int itemType;

    public static class ItemType{
        public final static int SWORD = 1;
        public final static int GUN = 2;
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int xsize, int ysize, int itemType) {
        this.position = new Vector2f(xpos, ypos);
        this.velocity = new Vector2f(xvel, yvel);
        this.size = new Vector2f(xsize, ysize);
        this.itemType = itemType;
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int itemType) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(16, 9);
        this.itemType = itemType;
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getItemType() {
        return itemType;
    }

    public void update() {
        this.velocity.y -= this.acceleration.y;
        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;
    }
}
