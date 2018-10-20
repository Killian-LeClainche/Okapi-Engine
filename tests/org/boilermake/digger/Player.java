package org.boilermake.digger;

import org.joml.Vector2f;


/**
 * Created by Killian Le Clainche on 10/20/2018.
 */
public class Player extends Block {

	public Vector2f velocity;
	private int jumps;
	private boolean isJumping;
	private long jumpTime;
	private boolean isDoubleJumping;
	private boolean isGrounded;
	public final Vector2f acceleration = new Vector2f(0.7f, -4f);
	private final Vector2f terminalVelocity = new Vector2f(20, 0);
	private final int jumpVel = 50;
	private final Vector2f screen = new Vector2f(1920, 1080);

	public Player(Vector2f position) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.size = new Vector2f(32, 64);
		this.jumps = 2;
		this.isJumping = false;
		this.isDoubleJumping = false;
		this.isGrounded = false;
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
		this.velocity.y += acceleration.y;
		if(this.velocity.x > terminalVelocity.x) this.velocity.x = terminalVelocity.x;
		if(this.velocity.x < -terminalVelocity.x) this.velocity.x = -terminalVelocity.x;

		this.position.x += this.velocity.x;
		this.position.y += this.velocity.y;
		if(this.position.y - this.size.y/2 < 0) {
			this.position.y = this.size.y/2;
			this.velocity.y = 0;
			if(!this.isGrounded) {
				resetJumps();
				this.isJumping = false;
				this.isDoubleJumping = false;
				this.velocity.y = 0;
				this.isGrounded = true;
			}
		} else if(this.position.y + this.size.y/2 > screen.y){
			this.position.y = screen.y - this.size.y/2;
			this.velocity.y = 0;
		}

		if(this.position.x - this.size.x/2 < 0) {
			this.position.x = this.size.x/2;
			this.velocity.x = 0;
		} else if(this.position.x + this.size.x/2 > screen.x){
			this.position.x = screen.x - this.size.x/2;
			this.velocity.x = 0;
		}
	}

	public void slow() {
		if(this.velocity.x >= this.acceleration.x/2 + 0.1) {
			this.velocity.x -= 2*acceleration.x;
		} else if(this.velocity.x <= -this.acceleration.x/2 - 0.1) {
			this.velocity.x += 2*acceleration.x;
		} else {
			this.velocity.x = 0;
		}

	}

	public void moveRight() {
		this.velocity.x += acceleration.x;
	}

	public void moveLeft() {
		this.velocity.x += -acceleration.x;
	}

	public void moveUp() {
		if(hasJumps()) {
			this.isGrounded = false;
			if(!this.isJumping) {
				this.velocity.y = jumpVel;
				this.jumps--;
				this.isJumping = true;
				this.jumpTime = System.currentTimeMillis();
			} else if(System.currentTimeMillis() - this.jumpTime > 250 && !this.isDoubleJumping) {
				this.velocity.y = jumpVel;
				this.jumps--;
				this.isDoubleJumping = true;
			}
		}
	}

	public void moveDown() {
		this.velocity.y += 2*acceleration.y;
	}

	public HitBox useItem() {
	    return null;
    }
}
