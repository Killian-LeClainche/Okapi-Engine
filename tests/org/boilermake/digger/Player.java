package org.boilermake.digger;

import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Player {

	public Vector position;

	public Player(Vector position) {
		this.position = position;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

}
