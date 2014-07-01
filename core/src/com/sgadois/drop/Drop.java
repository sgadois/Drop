package com.sgadois.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	SpriteBatch batch;
	Rectangle bucket;
	Vector3 touchPos;
	Array<Rectangle> raindrops;
	long lastDropTime;
	
	@Override
	public void create () {
		
		/*
		 * Loading
		 */
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		rainMusic.setLooping(true);
		rainMusic.play();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 320);
		
		batch = new SpriteBatch();
		
		raindrops = new Array<Rectangle>();
		
		/*
		 * Creating
		 */
		bucket = new Rectangle();
		bucket.width = 32;
		bucket.height = 32;
		bucket.x = 480 / 2 - 32 / 2;
		bucket.y = 5;
		
		spawnRainDrop();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		/*
		 * Add Sprite
		 */
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops)
			batch.draw(dropImage, raindrop.x, raindrop.y);
		batch.end();
		
		/*
		 * Controls
		 */
		if(Gdx.input.isTouched()){
			touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			// Transform touchPos coordinate into camera coordinate
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 32 / 2;
		}
		
		/*
		 * Raindrops
		 */
		if(TimeUtils.nanoTime() - this.lastDropTime > 1000000000) 
			this.spawnRainDrop();
		
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()){
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 32 < 0) 
				iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
				
		}
		

	}
	
	private void spawnRainDrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 480-32);
		raindrop.y = 320;
		raindrop.width = 32;
		raindrop.height = 32;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
