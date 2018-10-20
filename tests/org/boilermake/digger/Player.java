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
	private boolean isFacingLeft;
	public final Vector2f acceleration = new Vector2f(0.7f, -4f);
	private final Vector2f terminalVelocity = new Vector2f(20, 0);
	private final int jumpVel = 50;
	private final Vector2f screen = new Vector2f(1720, 1080);
	private int item;
	private int delay;

	public Player(Vector2f position) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.size = new Vector2f(64, 128);
		this.jumps = 2;
		this.isJumping = false;
		this.isDoubleJumping = false;
		this.isGrounded = false;
		this.item = Item.ItemType.NOTHING;
		this.delay = 60;
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

	public void setItem(int item) {
		this.item = item;
	}

	public void setDelay(int delay) {
	    this.delay = delay;
    }

	public boolean isGrounded() {
	    return isGrounded;
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
		this.isFacingLeft = false;
	}

	public void moveLeft() {
		this.velocity.x += -acceleration.x;
		this.isFacingLeft = true;
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

	public void stopX() {
		this.velocity.x = 0;
	}

	public void stopY() {
		this.velocity.y = 0;
	}

	public HitBox useItem() {
	    switch(this.item) {
            case Item.ItemType.DAGGER : return createDaggerHitBox();
            case Item.ItemType.SWORD : return createSwordHitBox();
            case Item.ItemType.CLAYMORE : return createClaymoreHitBox();
            case Item.ItemType.HALBERD : return createHalberdHitBox();
            case Item.ItemType.SNIPER : return createSniperHitBox();
            case Item.ItemType.SHOTGUN : return createShotgunHitBox();
            case Item.ItemType.PISTOL : return createPistolHitBox();
            case Item.ItemType.RIFLE : return createRifleHitBox();
            case Item.ItemType.GOD_FIST : return createGodFistHitBox();
			default: return null;
		}
    }

    public HitBox createDaggerHitBox() {
	    if(this.isFacingLeft) {
            return new HitBox((int)(this.position.x - this.size.x/2 - 8), (int)this.position.y, 16, 8, 0, 0, 15, this, HitBox.HitBoxTypes.SWORD);
        }
        else {
            return new HitBox((int)(this.position.x + this.size.x/2 + 8), (int)this.position.y, 16, 8, 0, 0, 15, this, HitBox.HitBoxTypes.SWORD);
        }
    }

    public HitBox createSwordHitBox() {
        if(this.isFacingLeft) {
            return new HitBox((int)(this.position.x - this.size.x/2 - 16), (int)this.position.y, 32, 16, 0, 0, 25, this, HitBox.HitBoxTypes.SWORD);
        }
        else {
            return new HitBox((int)(this.position.x + this.size.x/2 + 16), (int)this.position.y, 32, 16, 0, 0, 25, this, HitBox.HitBoxTypes.SWORD);
        }
    }

    public HitBox createClaymoreHitBox() {
        if(this.isFacingLeft) {
            return new HitBox((int)(this.position.x - this.size.x/2 - 32), (int)this.position.y, 64, 16, 0, 0, 35, this, HitBox.HitBoxTypes.SWORD);
        }
        else {
            return new HitBox((int)(this.position.x + this.size.x/2 + 32), (int)this.position.y, 64, 16, 0, 0, 35, this, HitBox.HitBoxTypes.SWORD);
        }
    }

    public HitBox createHalberdHitBox() {
        if(this.isFacingLeft) {
            return new HitBox((int)(this.position.x - this.size.x/2 - 48), (int)this.position.y, 96, 32, 0, 0, 45, this, HitBox.HitBoxTypes.SWORD);
        }
        else {
            return new HitBox((int)(this.position.x + this.size.x/2 + 48), (int)this.position.y, 96, 32, 0, 0, 45, this, HitBox.HitBoxTypes.SWORD);
        }
    }

    public HitBox createSniperHitBox() {
        if(this.isFacingLeft) {
            int xsize = (int)(this.position.x - this.size.x/2);
            int xpos = (int)(this.position.x - this.size.x/2 - xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, 0, 0, 5, this, HitBox.HitBoxTypes.GUN);
        }
        else {
            int xsize = (int)(screen.x - this.position.x + this.size.x/2);
            int xpos = (int)(this.position.x + this.size.x/2 + xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, 0, 0, 5, this, HitBox.HitBoxTypes.GUN);
        }
    }

    public HitBox createShotgunHitBox() {
        if(this.isFacingLeft) {
            int xsize = 64;
            int xpos = (int)(this.position.x - this.size.x/2 - xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 96, 0, 0, 10, this, HitBox.HitBoxTypes.GUN);
        }
        else {
            int xsize = 64;
            int xpos = (int)(this.position.x + this.size.x/2 + xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, 0, 0, 10, this, HitBox.HitBoxTypes.GUN);
        }
    }

    public HitBox createPistolHitBox() {
        if(this.isFacingLeft) {
            int xsize = 16;
            int xpos = (int)(this.position.x - this.size.x/2 - xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, (int)(-screen.x/10), 0, 10, this, HitBox.HitBoxTypes.GUN);
        }
        else {
            int xsize = 16;
            int xpos = (int)(this.position.x + this.size.x/2 + xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, (int)(screen.x/10), 0, 10, this, HitBox.HitBoxTypes.GUN);
        }
    }

    public HitBox createRifleHitBox() {
        if(this.isFacingLeft) {
            int xsize = 24;
            int xpos = (int)(this.position.x - this.size.x/2 - xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, (int)(-screen.x/5), 0, 5, this, HitBox.HitBoxTypes.GUN);
        }
        else {
            int xsize = 24;
            int xpos = (int)(this.position.x + this.size.x/2 + xsize/2.0);
            return new HitBox(xpos, (int)this.position.y, xsize, 8, (int)(screen.x/5), 0, 5, this, HitBox.HitBoxTypes.GUN);
        }
    }

    public HitBox createGodFistHitBox() {
        return new HitBox((int)this.position.x, (int)this.position.y, 128, 128, 0, 0, 1, this, HitBox.HitBoxTypes.FIST);
    }
}
