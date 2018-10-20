package org.boilermake.digger;

import org.joml.Vector2f;


/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Player {

	public Vector2f position;
	public Vector2f velocity;
	public final Vector2f acceleration = new Vector2f(0.5f, -1);
	private final Vector2f terminalVelocity = new Vector2f(10, 10);

	public Player(Vector2f position) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(float x, float y) {
		this.velocity.x = x;
		this.velocity.y = y;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}

	public void update() {
		getVelocity().y += acceleration.y;
		if(getVelocity().y > terminalVelocity.y) getVelocity().y = terminalVelocity.y;
		if(getVelocity().y < -terminalVelocity.y) getVelocity().y = -terminalVelocity.y;
		if(getVelocity().x > terminalVelocity.x) getVelocity().x = terminalVelocity.x;
		if(getVelocity().x < -terminalVelocity.x) getVelocity().x = -terminalVelocity.x;
		setPosition(getPosition().x + getVelocity().x, getPosition().y + getVelocity().y);
		System.out.println(this.position);
	}

	public void slow() {
		setVelocity(0, 0);
	}

	private void move(int direction) {
		if(direction == 0) { //right
			getVelocity().x += acceleration.x;
		} else if(direction == 1) { //left
			getVelocity().x += -acceleration.x;
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
