package org.boilermake.digger;

import org.joml.Vector2f;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Grave extends Block {
    private int digCount;
	private final Item reward;


	public Grave(int xpos, int ypos, int xsize, int ysize, int digCount, Item reward) {
	    this.position = new Vector2f(xpos, ypos);
	    this.size = new Vector2f(xsize, ysize);
	    this.digCount = digCount;
	    this.reward = reward;
    }

    public Grave(int xpos, int ypos, int digCount, Item reward) {
        this.position = new Vector2f(xpos, ypos);
        this.size = new Vector2f(32, 64);
        this.digCount = digCount;
        this.reward = reward;
    }

    public void dig() {
	    this.digCount--;
    }

    public Item getReward() {
	    return reward;
    }

    public boolean isDug() {
	    return digCount <= 0;
    }
}
