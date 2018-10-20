package org.boilermake.digger;

import polaris.okapi.render.Texture;
import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Terrain {
    private final Vector position;
    private final Vector size;

    public Terrain(int xpos, int ypos, int xsize, int ysize) {
        this.position = new Vector(xpos, ypos);
        this.size = new Vector(xsize, ysize);
    }

    public Terrain(int xpos, int ypos) {
        this.position = new Vector(xpos, ypos);
        this.size = new Vector(16, 9);
    }

}
