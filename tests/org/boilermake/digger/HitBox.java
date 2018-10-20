package org.boilermake.digger;

import org.joml.Vector2f;

public class HitBox extends Block {
    private Vector2f velocity;
    private int time;
    private Player owner;
    private int boxType;

    public static class HitBoxTypes {
        public static final int SWORD = 1;
        public static final int GUN = 2;
        public static final int FIST = 3;
    }

    public HitBox(int xpos, int ypos, int xsize, int ysize, int xvel, int yvel, int time, Player owner, int boxType) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(xsize, ysize);
        this.velocity = new Vector2f(xvel, yvel);
        this.time = time;
        this.owner = owner;
        this.boxType = boxType;
    }

    public void update() {
        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;
        this.time--;
    }

    public boolean isExpired() {
        return this.time <= 0;
    }

}
