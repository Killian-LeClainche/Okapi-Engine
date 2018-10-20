package org.boilermake.digger;

import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Grave {
	private final Vector position;
	private final Vector size;
    private int digTime;
    private final int digTick;
	private final Item reward;


	public Grave(int xpos, int ypos, float xsize, float ysize, int digTime, Item reward) {
	    this.position = new Vector(xpos, ypos);
	    this.size = new Vector(xsize, ysize);
	    this.digTime = digTime;
	    this.reward = reward;
	    this.digTick = 1;
    }

    public Grave(int xpos, int ypos, int digTime, Item reward) {
        this.position = new Vector(xpos, ypos);
        this.size = new Vector(19.2, 10.8)
        this.digTime = digTime;
        this.reward = reward;
        this.digTick = 1;
    }

    public void update() {
	    this.digTime -= digTick;
    }

    public Vector getPosition() {
	    return position;
    }

    public Item getReward() {
	    return reward;
    }

    public boolean isDug() {
	    return digTime <= 0;
    }
}
