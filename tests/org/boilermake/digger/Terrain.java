package org.boilermake.digger;

import org.joml.Vector2f;
import polaris.okapi.render.Texture;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Terrain extends Block {

    public Terrain(int xpos, int ypos, int xsize, int ysize) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(xsize, ysize);
    }

    public Terrain(int xpos, int ypos) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(16, 9);
    }
}
