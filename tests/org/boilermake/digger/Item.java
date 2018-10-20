package org.boilermake.digger;

import org.joml.Vector2f;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Item extends Block {
    private Vector2f velocity;
    private final Vector2f acceleration = new Vector2f(0, -2);
    private final Vector2f screen = new Vector2f(1920, 1080);
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
        if(this.position.x < 0) {
            this.position.x = 0;
            this.velocity.x = 0;
        }
        else if(this.position.x + this.size.x > screen.x) {
            this.position.x = screen.x - this.size.x;
            this.velocity.x = 0;
        }
        if(this.position.y < 0) {
            this.position.y = 0;
            this.velocity.y = 0;
        }
        else if(this.position.y + this.size.y > screen.y) {
            this.position.y = screen.y - this.size.y;
            this.velocity.y = 0;
        }
    }
}
