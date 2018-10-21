package org.boilermake.digger;

import org.joml.Vector2f;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Item extends Block {
    public Vector2f velocity;
    public Vector2f position;
    public Vector2f size;
    private final Vector2f acceleration = new Vector2f(0, -2);
    private final Vector2f screen = new Vector2f(1920, 1080);
    public int itemType;
    private int itemDelay;

    public static class ItemType{
        public final static int NOTHING = 0;
        public final static int SWORD = 1;
        public final static int CLAYMORE = 2;
        public final static int HALBERD = 3;
        public final static int SNIPER = 4;
        public final static int SHOTGUN = 5;
        public final static int RIFLE = 6;
        public final static int GOD_FIST = 7;
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int xsize, int ysize, int itemType) {
        this.position = new Vector2f(xpos, ypos);
        this.velocity = new Vector2f(xvel, yvel);
        this.size = new Vector2f(xsize, ysize);
        this.itemType = itemType;
        switch (itemType) {
            case ItemType.NOTHING: this.itemDelay = 60; break;
            case ItemType.SWORD: this.itemDelay = 30; break;
            case ItemType.CLAYMORE: this.itemDelay = 40; break;
            case ItemType.HALBERD: this.itemDelay = 50; break;
            case ItemType.SNIPER: this.itemDelay = 60; break;
            case ItemType.SHOTGUN: this.itemDelay = 70; break;
            case ItemType.RIFLE: this.itemDelay = 50; break;
            case ItemType.GOD_FIST: this.itemDelay = 2; break;
        }
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int itemType) {
        this(xpos, ypos, xvel, yvel, 32, 32, itemType);
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public Vector2f getSize() {
        return this.size;
    }

    public Vector2f getVelocity() { return this.velocity; }

    public int getItemType() {
        return itemType;
    }

    public int getItemDelay() {
        return itemDelay;
    }

    public void update() {
        this.velocity.y -= this.acceleration.y;
        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;
        if(this.position.x - this.size.x/2 < 0) {
            this.position.x = this.size.x/2;
            this.velocity.x = 0;
        }
        else if(this.position.x + this.size.x/2 > screen.x) {
            this.position.x = screen.x - this.size.x/2;
            this.velocity.x = 0;
        }
        if(this.position.y - this.size.y/2 < 0) {
            this.position.y = this.size.y/2;
            this.velocity.y = 0;
        }
        else if(this.position.y + this.size.y/2 > screen.y) {
            this.position.y = screen.y - this.size.y/2;
            this.velocity.y = 0;
        }
    }
}
