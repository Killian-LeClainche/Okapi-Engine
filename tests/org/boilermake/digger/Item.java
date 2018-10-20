package org.boilermake.digger;

import org.joml.Vector2f;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Item extends Block {
    private Vector2f velocity;
    private final Vector2f acceleration = new Vector2f(0, -2);
    private final Vector2f screen = new Vector2f(1720, 1080);
    private int itemType;

    public static class ItemType{
        public final static int NOTHING = 0;
        public final static int DAGGER = 1;
        public final static int SWORD = 2;
        public final static int CLAYMORE = 3;
        public final static int HALBERD = 4;
        public final static int SNIPER = 5;
        public final static int SHOTGUN = 6;
        public final static int PISTOL = 7;
        public final static int RIFLE = 8;
        public final static int GOD_FIST = 10;
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int xsize, int ysize, int itemType) {
        this.position = new Vector2f(xpos, ypos);
        this.velocity = new Vector2f(xvel, yvel);
        this.size = new Vector2f(xsize, ysize);
        this.itemType = itemType;
    }

    public Item(int xpos, int ypos, int xvel, int yvel, int itemType) {
        this.position = new Vector2f(xpos, ypos);
        this.velocity = new Vector2f(xvel, yvel);
        this.size = new Vector2f(32, 32);
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
