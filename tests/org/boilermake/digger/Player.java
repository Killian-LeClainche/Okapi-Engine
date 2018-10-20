package org.boilermake.digger;

import org.joml.Vector2f;


/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Player extends Block {

	public Vector2f velocity;
	private int jumps;
	private boolean isJumping;
	public final Vector2f acceleration = new Vector2f(0.75f, -9.8f);
	private final Vector2f terminalVelocity = new Vector2f(20, 20);
	private final int jumpVel = 20;
	private final Vector2f max = new Vector2f(1850, 1000);
	private final Vector2f min = new Vector2f(35, 35);

	public Player(Vector2f position) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.jumps = 2;
		this.isJumping = false;
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

	public boolean hasJumps() {
		return this.jumps != 0;
	}

	public void resetJumps() {
		this.jumps = 2;
	}

	public void update() {
		getVelocity().y += acceleration.y;
		if(getVelocity().y > terminalVelocity.y) getVelocity().y = terminalVelocity.y;
		if(getVelocity().y < -terminalVelocity.y) getVelocity().y = -terminalVelocity.y;
		if(getVelocity().x > terminalVelocity.x) getVelocity().x = terminalVelocity.x;
		if(getVelocity().x < -terminalVelocity.x) getVelocity().x = -terminalVelocity.x;

		this.position.x += this.velocity.x;
		this.position.y += this.velocity.y;
		if(this.position.y < min.y) {
			this.position.y = min.y;
			this.jumps = 1;
			this.isJumping = false;
			this.velocity.y = 0;
		} else if(this.position.y > max.y){
			this.position.y = max.y;
			this.velocity.y = 0;
		}

		if(this.position.x < min.x) {
			this.position.x = min.x;
			this.velocity.x = 0;
		} else if(this.position.x > max.x){
			this.position.x = max.x;
			this.velocity.x = 0;
		}
	}

	public void slow() {
		setVelocity(0, 0);
	}

	private void move(int direction) {
		if(direction == 0) { //right
			this.velocity.x += acceleration.x;
		}
		if(direction == 1) { //left
			this.velocity.x += -acceleration.x;
		}
		if(direction == 2 && hasJumps()) { //up
			this.velocity.y += jumpVel;
			if(!this.isJumping) {
				this.jumps--;
				this.isJumping = true;
			}
		}
	}

	public void moveRight() {
		move(0);
	}

	public void moveLeft() {
		move(1);
	}

	public void moveUp() {
		move(2);
	}

	public void moveDown() {

	}

}
