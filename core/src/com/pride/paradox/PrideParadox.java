package com.pride.paradox;

import static com.pride.paradox.Print.print;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PrideParadox extends ApplicationAdapter {
	public enum GameState {
		Menu,
		Pause,
		Play,
	}
	SpriteBatch batch;
	BitmapFont title;
	GlyphLayout layout;

	public static GameState gameState = GameState.Menu;
	public OrthographicCamera camera;
	public Viewport viewport;
	public InputProcessor input;
	public Texture menuBG,cursorTexture;
	public float timeElapsed=0;
	String[] menuButtonNames={"START","HOW TO PLAY?","OPTIONS","EXIT"};
	public static Array<Button.MenuButton> menuButtonArray= new Array<>();
	@Override
	public void create () {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
		Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0); // 0, 0 are the hotspot coordinates
		Gdx.graphics.setCursor(customCursor);
		input= new InputProcessor();
		Gdx.input.setInputProcessor(input);

		camera=new OrthographicCamera();
		viewport=new ExtendViewport(1280,720,camera);
		camera.setToOrtho(false,1280,720);
		viewport.apply();

		layout=new GlyphLayout();
		title=new BitmapFont(Gdx.files.internal("joystix.fnt"));
//		title.getData().scale(2f);

		batch = new SpriteBatch();

		menuBG= new Texture(Gdx.files.internal("thumb.png"));
		cursorTexture= new Texture(Gdx.files.internal("cursor.png"));

		float index=0;
		for(String name : menuButtonNames){
			menuButtonArray.add(new Button.MenuButton(210-index*55,name,title));
			index++;
		}


	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height,true);
		super.resize(width, height);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		timeElapsed+=Gdx.graphics.getDeltaTime();
		camera.update();
		camera.position.set(1280/2f,720/2f,0);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		switch (gameState){
			case Menu: {
				batch.draw(menuBG,0,0,1280,720);
				for(Button.MenuButton button : menuButtonArray){
					button.render(batch,timeElapsed);
				}
				}break;
			case Pause:{

			}break;

			case Play:	{
				print("startGame");
			}break;
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public static class InputProcessor extends InputAdapter {

		@Override
		public boolean keyDown(int keycode) {

			return true;
		}

		@Override
		public boolean keyUp(int keycode) {

			return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {

			return true;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {

			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {

			return true;
		}
	}

}
