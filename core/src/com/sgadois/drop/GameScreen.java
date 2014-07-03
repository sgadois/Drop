package com.sgadois.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	
	final Drop game;
	
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Vector3 touchPos;
	long lastDropTime;
	
	
	private final Array<Droplet> activeDroplets = new Array<Droplet>();
	
	private final Pool<Droplet> dropletPool = new Pool<Droplet>() {

		@Override
		protected Droplet newObject() {
			return new Droplet();
		}
		
	};
	
	
	public GameScreen(final Drop gam) {
		
		this.game = gam;
		
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
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		/*
		 * Add Sprite
		 */
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.batch.draw(bucketImage, bucket.x, bucket.y);
		for(Droplet droplet : activeDroplets)
			game.batch.draw(dropImage, droplet.position.x, droplet.position.y);
		game.batch.end();
		
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
		
		Droplet item;
        int len = activeDroplets.size;
        for (int i = len; --i >= 0;) {
            item = activeDroplets.get(i);
            boolean catchDrop = item.render(delta, bucket);
            if(catchDrop)
            	this.dropSound.play();
            if (item.alive == false) {
            	activeDroplets.removeIndex(i);
                dropletPool.free(item);
            }
        }
		

	}
	
	private void spawnRainDrop() {
		Droplet item = dropletPool.obtain();
		item.init(MathUtils.random(0, 480-32), 320);
		activeDroplets.add(item);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
