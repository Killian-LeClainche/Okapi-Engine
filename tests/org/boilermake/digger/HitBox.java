package org.boilermake.digger;

import org.joml.Vector2f;

public class HitBox extends Block {
    private Vector2f velocity;
    private int damage;
    private int time;

    public HitBox(int xpos, int ypos, int xsize, int ysize, int xvel, int yvel, int damage, int time) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(xsize, ysize);
        this.velocity = new Vector2f(xvel, yvel);
        this.damage = damage;
        this.time = time;
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
