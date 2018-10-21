package org.boilermake.digger;

import org.joml.Vector2f;

public class Block {
    public Vector2f position;
    public Vector2f size;

    public Block() {

    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setPosition(int posx, int posy) {
        this.position.x = posx;
        this.position.y = posy;
    }

    public void setSize(int sizex, int sizey) {
        this.size.x = sizex;
        this.size.y = sizey;

    }
}
