package org.boilermake.digger;

import org.joml.Vector2f;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Grave extends Block {
    private int digTime;
    private final int digTick;
	private final Item reward;


	public Grave(int xpos, int ypos, int xsize, int ysize, int digTime, Item reward) {
	    this.position = new Vector2f(xpos, ypos);
	    this.size = new Vector2f(xsize, ysize);
	    this.digTime = digTime;
	    this.reward = reward;
	    this.digTick = 1;
    }

    public Grave(int xpos, int ypos, int digTime, Item reward) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(32, 64);
        this.digTime = digTime;
        this.reward = reward;
        this.digTick = 1;
    }

    public void update() {
	    this.digTime -= digTick;
    }

    public Item getReward() {
	    return reward;
    }

    public boolean isDug() {
	    return digTime <= 0;
    }
}
