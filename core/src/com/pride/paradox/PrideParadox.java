package com.pride.paradox;

import static com.pride.paradox.Print.print;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PrideParadox extends ApplicationAdapter {
	public enum GameState {	Menu, Load, Save, Pause, Play, Settings, Instructions}
	SpriteBatch batch;
	BitmapFont title;
	GlyphLayout layout;
	public static GameState gameState = GameState.Menu;
	public static Vector3 touch;
	public static Vector2 point;
	public static OrthographicCamera camera;
	public Viewport viewport;
	public InputProcessor input;
	public Texture menuBG,cursorTexture;
	public float timeElapsed=0;
	public static int menuButtonActiveIndex=0;
	public ShapeRenderer shapeRenderer;
	String[] menuButtonNames={"START","HOW TO PLAY?","OPTIONS","EXIT"};
	public static Array<MenuButton> menuButtonArray= new Array<MenuButton>();


	public static class StoryLine{
		public String message;
		public String byLine;
		public StoryLine choiceA;
		public StoryLine choiceB;
		public StoryLine(String message,String byLine){
			this.message=message;
			this.byLine=byLine;
		}
		public StoryLine(String message,String byLine,StoryLine choiceA,StoryLine choiceB){
			this.message=message;
			this.byLine=byLine;
			this.choiceA=choiceA;
			this.choiceB=choiceB;
		}
	}
	public static class MenuButton {
		private float y,x,alpha;
		public int index;
		private GlyphLayout layout;
		public  Rectangle bounds;
		private BitmapFont font;
		public final String name;

		public MenuButton(float y, String name,int index){
			this.font = new BitmapFont(Gdx.files.internal("joystix.fnt"));
			layout = new GlyphLayout(font,name);
			this.x = (1280 - layout.width) / 2;
			this.y=y;
			this.name=name;
			this.index=index;
			this.bounds= new Rectangle(x,y-layout.height,layout.width,layout.height);
		}
		public void render(SpriteBatch batch,float timeElapsed){
			font.draw(batch,name,this.x,this.y);
			if(index==menuButtonActiveIndex){
				alpha = (float) (0.5 + 0.5*Math.sin(timeElapsed * 3 * Math.PI));
			}
			font.setColor(1,1,1,index==menuButtonActiveIndex?alpha:1);

		}
		public Boolean isTouching(Vector2 touch){
			return bounds.contains(touch);
		}
	}


	@Override
	public void create () {
		Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
		Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
		Gdx.graphics.setCursor(customCursor);
		input= new InputProcessor();
		Gdx.input.setInputProcessor(input);
		Controllers.addListener(new controllerInput());

		camera=new OrthographicCamera();
		viewport=new ExtendViewport(1280,720,camera);
		camera.setToOrtho(false,1280,720);
		viewport.apply();

		layout=new GlyphLayout();
		title=new BitmapFont(Gdx.files.internal("joystix.fnt"));
//		title.getData().scale(2f);

		shapeRenderer=new ShapeRenderer();
		batch = new SpriteBatch();

		menuBG= new Texture(Gdx.files.internal("thumb.png"));
		cursorTexture= new Texture(Gdx.files.internal("cursor.png"));

		int index=0;
		for(String name : menuButtonNames){
			menuButtonArray.add(new MenuButton(210-index*55,name,index));
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
				for(MenuButton button : menuButtonArray){
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

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

//		for(MenuButton button : menuButtonArray){
//			shapeRenderer.setColor(button.active?Color.BLUE:Color.GREEN);
//			Rectangle bounds = button.bounds;
//			shapeRenderer.rect(bounds.x,bounds.y,bounds.width,bounds.height);
//		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}




	private static class controllerInput implements ControllerListener {
		@Override
		public void connected(Controller controller) {
			Gdx.app.log("Controller", "connected: " + controller.getName());
		}

		@Override
		public void disconnected(Controller controller) {
			Gdx.app.log("Controller", "disconnected: " + controller.getName());
		}

		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			Gdx.app.log("Controller", "button down: " + buttonCode);
			return false;
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonCode) {
			Gdx.app.log("Controller", "button up: " + buttonCode);
			return false;
		}

		@Override
		public boolean axisMoved(Controller controller, int axisCode, float value) {
			Gdx.app.log("Controller", "axis moved: " + axisCode + " value: " + value);
			return false;
		}
	}


	public static class InputProcessor extends InputAdapter {

		@Override
		public boolean keyDown(int keycode) {
			
			return true;
		}

		@Override
		public boolean keyUp(int keycode) {
	    	switch(gameState){
				case Menu :{
					if(keycode== Input.Keys.UP&&menuButtonActiveIndex>0){
						menuButtonActiveIndex--;
					}
					if(keycode== Input.Keys.DOWN&&menuButtonActiveIndex<3){
						menuButtonActiveIndex++;
					}
					if(keycode==Input.Keys.ENTER||keycode==Input.Keys.SPACE){
						switch (menuButtonActiveIndex){
							case 0:{
								gameState=GameState.Load;
							}break;
							case 1:{
								gameState=GameState.Instructions;
							}break;
							case 2:{
								gameState=GameState.Settings;
							}break;
							case 3:{
								Gdx.app.exit();
							}
						}
					}
				}break;

			}
			return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			touch = new Vector3(screenX,screenY,0);
			camera.unproject(touch);
			point= new Vector2(touch.x,touch.y);

			for (MenuButton button : menuButtonArray) {
				if(button.isTouching(point))menuButtonActiveIndex=button.index;
			}


			return true;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {

			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			touch = new Vector3(screenX,screenY,0);
			camera.unproject(touch);
			point= new Vector2(touch.x,touch.y);

			for (MenuButton btn : menuButtonArray) {
				if(btn.isTouching(point)){
					switch (menuButtonActiveIndex){
						case 0:{
							gameState=GameState.Load;
						}break;
						case 1:{
							gameState=GameState.Instructions;
						}break;
						case 2:{
							gameState=GameState.Settings;
						}break;
						case 3:{
							Gdx.app.exit();
						}
					}
				}
			}
			return true;
		}
	}

}
