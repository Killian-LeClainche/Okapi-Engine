package org.boilermake.digger;

import polaris.okapi.world.Vector;

/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Player {

	public Vector position;
	public Vector velocity;
	public final Vector acceleration = new Vector(2, -10);
	private final Vector terminalVelocity = new Vector(150, 150);

	public Player(Vector position) {
		this.position = position;
		this.velocity = new Vector(0, 0);
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public void setVelcity(float x, float y) {
		this.velocity.setX(x);
		this.velocity.setY(y);
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public void setPosition(float x, float y) {
		this.position.setX(x);
		this.position.setY(y);
		System.out.println(this.position.getX());
	}

	private void update() {
		getVelocity().setY(acceleration.getY() + getVelocity().getY());
		setPosition(getPosition().getX() + getVelocity().getX(), getPosition().getY() + getVelocity().getY());
	}

	private void move(int direction) {
		if(direction == 0) { //right
			getVelocity().setX(acceleration.getX() + getVelocity().getX());
		} else if(direction == 1) { //left
			getVelocity().setX(-acceleration.getX() + getVelocity().getX());
		}
	}

	public void moveRight() {
		move(0);
	}

	public void moveLeft() {
		move(1);
	}

	public void moveUp() {

	}

	public void moveDown() {

	}

}
