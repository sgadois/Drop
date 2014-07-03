package com.sgadois.drop;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Droplet implements Poolable {
	
	public Rectangle position;
	public boolean alive;
	
	public Droplet() {
		this.position = new Rectangle();
		this.position.height = 32;
		this.position.width = 32;
		this.alive = false;
	}

	public void init(float posX, float posY) {
		this.position.setPosition(posX, posY);
		alive = true;
	}
	
	@Override
	public void reset() {
		this.position.setPosition(0, 0);
		this.alive = false;
	}
	
	public boolean render(float delta, Rectangle bucket) {
		this.position.setPosition(this.position.x, this.position.y - 1*delta*200);
		
		if(this.position.y - 32 < 0)
			this.alive = false;
		if(this.position.overlaps(bucket)) {
			this.alive = false;
			return true;
		}
		return false;
	}

}
