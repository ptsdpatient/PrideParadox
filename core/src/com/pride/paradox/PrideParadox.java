package com.pride.paradox;

import static com.pride.paradox.Print.print;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.DistanceFieldFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.css.Rect;


public class PrideParadox extends ApplicationAdapter {
    public static GameState gameState = GameState.Menu;
    public static Vector3 touch;
    public static Vector2 point;
    public static Sprite player;
    public static OrthographicCamera camera;
    public Texture background;
    public BitmapFont dialogueFont,choiceFont;
    public static float timeElapsed = 0, controllerConectTime = 0f,drawTextTime=0,textDuration=0f,playerRotation=0;
    public static int menuButtonActiveIndex = 0,loadButtonIndex=0,typewriterIndex=0,currentLevel=0,storyLineIndex=0,lineDepth=0;
    public static Boolean controllerConnected = false,drawingDialogue=false,drawingText=false,fight=true,lineSkip=false,choiceMode=false,playerTurnLeft=false,playerTurnRight=false;
    public static Array<MenuButton> menuButtonArray = new Array<>();
    public static Array<LoadButton> loadButtonArray= new Array<>();
    public static Array<Array<StoryLine>> levels=new Array<>();
    public static Array<Animation<TextureRegion>> playerAnimation= new Array<>(3);
    public static StoryLine currentLine;
    public Viewport viewport;
    public static Rectangle choiceABounds,choiceBBounds;
    public InputProcessor input;
    public Texture menuBG, cursorTexture, gamepadConnect,htp,arena;
    public TextureRegion playerSheet;
    public static TextureRegion playerFrame;
    public TextureRegion[] loadButtonSheet;
    public int[] playerSpriteIndex={5,14,5};
    public ShapeRenderer shapeRenderer;

    public static String typewriter,dialogueMessage="";
    SpriteBatch batch;
    BitmapFont titleFont;
    GlyphLayout layout;
    String[] menuButtonNames = {"START", "HOW TO PLAY?", "EXIT"};
    String[] arenaBoundNames={"up","down","left","right"};
    public static Array<ArenaBounds> arenaBounds=new Array<>();
    public enum GameState {Menu, Load, Save, Pause, Play, Instructions}
    public enum choiceState{A,B}
    static choiceState choice=choiceState.A;
    public static FileHandle files(String input){
        return Gdx.files.internal(input);
    }
    public void drawChoice(SpriteBatch batch,StoryLine line){
        float alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 1.5 * Math.PI));
        choiceFont.setColor(1,1,1,(choice==choiceState.A)?alpha:1);
        choiceFont.draw(batch,line.choiceA.message,choiceABounds.x,choiceABounds.y+choiceABounds.height-10f,choiceABounds.width, Align.center,true);
        choiceFont.setColor(1,1,1,(choice==choiceState.B)?alpha:1);
        choiceFont.draw(batch,line.choiceB.message,choiceBBounds.x,choiceBBounds.y+choiceBBounds.height-10f,choiceBBounds.width, Align.center,true);
    }
    public void drawText(SpriteBatch batch, StoryLine line){
        dialogueMessage=line.byLine+" : "+line.message;
        if(drawTextTime<0.01){
            drawTextTime+=Gdx.graphics.getDeltaTime();
        }else {
            drawTextTime=0;
            if(typewriterIndex<dialogueMessage.length()){
                typewriterIndex++;
                typewriter=dialogueMessage.substring(0,typewriterIndex);
            }else {
                if(lineSkip){
                    if(((levels.get(currentLevel).get(storyLineIndex).depth!=lineDepth+1)&&levels.get(currentLevel).get(storyLineIndex).depth!=0)){
                         lineDepth++;
                    }
                    else {
                        lineDepth=0;
                        storyLineIndex++;
                    }
                    lineSkip=false;
                    textDuration=0f;
                    typewriterIndex=0;
                    drawTextTime=10f;
                    return;
                }
            }
        }
        batch.draw(background,1280/2f-500,0,1000,800);
        if(drawingText)dialogueFont.draw(batch,typewriter,230,220,800, Align.topLeft,true);
    }
        public static int calculateDepth(StoryLine storyLine){
            int depth=0;
            for(StoryLine current = storyLine;current!=null;current=current.choiceA){
                depth++;
//                print(current.message);
            }
            return depth;
        }

    public static void handleMenu() {
        switch (menuButtonActiveIndex) {
            case 0: {
                gameState = GameState.Load;
            }
            break;
            case 1: {
                gameState = GameState.Instructions;
            }
            break;
            case 2: {
                Gdx.app.exit();
            }break;
        }
    }
    public static Boolean checkExitKey(int keycode){
        return keycode==Input.Keys.X ||keycode==Input.Keys.ESCAPE || keycode==Input.Keys.CONTROL_RIGHT;
    }

    public static TextureRegion[] extractSprites(String name,int width,int height){
        TextureRegion sheet =new TextureRegion(new Texture(files(name)));
        return  sheet.split(width,height)[0];
    }

    public static void storyInitialize(){
        levels.add(new Array<StoryLine>());
        levels.get(currentLevel).add(new StoryLine("Select your gender! ","Narrator",new StoryLine("Female", "2.",new StoryLine("you selected female","narrator",new StoryLine("Good choice you are not misogyinist","narrator"))),new StoryLine("Male","1.",new StoryLine("you selected male","narrator"))));
        levels.get(currentLevel).add(new StoryLine("Hello there! My name is Tanishq!","Narator",new StoryLine("Niggesh do not toy with me jhajajajaja","sus",new StoryLine("what shit oh no!","what the ",new StoryLine("heheheh haw","heehehe")))));
        levels.get(currentLevel).add(new StoryLine("Imam gadzhi!","Narator",new StoryLine("Trisha takanava","sus",new StoryLine("Niggesh forever!","what the ",new StoryLine("i hate bjp","heehehe")))));

        for(StoryLine level : levels.get(currentLevel)){
            level.depth=calculateDepth(level);
//            print(level.depth);
        }
    }
    public static void drawPlayer(SpriteBatch batch,float stateTime){
        playerFrame = playerAnimation.get(0).getKeyFrame(stateTime, true);
        if(playerTurnLeft)player.rotate(-3);
        if(playerTurnRight)player.rotate(3);
        player.setRegion(playerFrame);
        player.setOriginCenter();
        player.draw(batch);
    }



    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(files("cursor.png"));
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        input = new InputProcessor();
        Gdx.input.setInputProcessor(input);
        Controllers.addListener(new controllerInput());

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);
        camera.setToOrtho(false, 1280, 720);
        viewport.apply();

        layout = new GlyphLayout();
        titleFont = new BitmapFont(files("joystix.fnt"));
//		titleFont.getData().scale(2f);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        menuBG = new Texture(files("thumb.png"));
        cursorTexture = new Texture(files("cursor.png"));
        gamepadConnect = new Texture(files("gamepad-connected.png"));
        htp=new Texture(files("htp.png"));
        dialogueFont = new BitmapFont(files("dialog.fnt"));
        choiceFont= new BitmapFont(files("choice.fnt"));
        background = new Texture(files("dialogue.png"));
        arena=new Texture(files("arena.png"));

        for(String name: arenaBoundNames) arenaBounds.add(new ArenaBounds(name));

        dialogueFont.getData().setScale(0.85f);

        choiceABounds=new Rectangle(120,720/2f,1280/3f,100);
        choiceBBounds=new Rectangle(1280-120-1280/3f,720/2f,1280/3f,100);

        loadButtonSheet=extractSprites("loadSheet.png",64,64);



        playerSheet=new TextureRegion(new Texture(files("player.png")));
        TextureRegion[][] totalFrames=playerSheet.split(32,32);
        for(int frameCount : playerSpriteIndex) {
            TextureRegion[] frames = new TextureRegion[frameCount];
            int j=0;
            for(int i=j;i<frameCount;i++){
                frames[j]=totalFrames[0][i];
                j++;
            }
            playerAnimation.add(new Animation<>(0.1f,frames));
        }
        player=new Sprite(playerAnimation.get(0).getKeyFrame(0));
        player.setPosition(1280/2f,720/2f);
        player.setOriginCenter();

        for(int i=0;i<3;i++){
            loadButtonArray.add(new LoadButton(200+i*400,i,75,loadButtonSheet));
        }

        int index = 0;
        for (String name : menuButtonNames) {
            menuButtonArray.add(new MenuButton(190 - index * 70, name, index));
            index++;
        }
        storyInitialize();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        timeElapsed += Gdx.graphics.getDeltaTime();
        camera.update();
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        switch (gameState) {
            case Menu: {
                batch.draw(menuBG, 0, 0, 1280, 720);
                for (MenuButton button : menuButtonArray) {
                    button.render(batch, timeElapsed);
                }
            }break;

            case Instructions:{
                batch.draw(htp,1280/2f-640*1.5f/2f,720/2f-480*1.5f/2f,640*1.5f,480*1.5f);
            }break;

            case Pause: {
            }break;

            case Play: {
                if(fight){
                    batch.draw(arena,1280/2f-640/2f,720/2f-480/2f,640,480);
                    drawPlayer(batch,timeElapsed);
                }
                if(drawingDialogue){
                    currentLine = levels.get(currentLevel).get(storyLineIndex);
                    if(currentLine.choice!=null) {
                        if(currentLine.choice==StoryLine.choiceState.A){
                            currentLine=currentLine.choiceA;
                        }
                        if(currentLine.choice==StoryLine.choiceState.B){
                            currentLine=currentLine.choiceB;
                        }
                    }

                    if(currentLine.choice==StoryLine.choiceState.Nill){
//                        print("choiceMode");
                        if(!choiceMode)choiceMode=true;
                        drawChoice(batch,currentLine);
                    }else {
                        int i = 0;
                        if ((currentLine.depth > lineDepth) && (currentLine.depth != 0)) {
                            for (StoryLine storyLine = currentLine; i != lineDepth + 1; storyLine = storyLine.choiceA) {
                                i++;
                                if(storyLine==null) break; else currentLine = storyLine;
                            }
                        }
//                        print("lineDepth : "+lineDepth +"; i = "+i);
                        if(lineDepth>i){
                            storyLineIndex++;
                            lineDepth=0;
                        }
//                        print(i+","+lineDepth+" : "+currentLine.message);
                    }



                    drawText(batch,currentLine);
                }
            }break;

            case Load:{
                titleFont.draw(batch,"SELECT GAME",220,600,900,10,true);
                for(LoadButton btn : loadButtonArray){
                    btn.render(batch,timeElapsed);
                }
            }break;
        }

        if (controllerConnected && controllerConectTime < 3f) {
            batch.draw(gamepadConnect, 1100, 620);
            controllerConectTime += Gdx.graphics.getDeltaTime();

        }

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


//        shapeRenderer.rect(choiceABounds.x,choiceABounds.y,choiceABounds.width,choiceABounds.height);
//        shapeRenderer.rect(choiceBBounds.x,choiceBBounds.y,choiceBBounds.width,choiceBBounds.height);

//		for(MenuButton button : menuButtonArray){
//			shapeRenderer.setColor(button.active?Color.BLUE:Color.GREEN);
//			Rectangle bounds = button.bounds;
//			shapeRenderer.rect(bounds.x,bounds.y,bounds.width,bounds.height);
//		}
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static void setChoiceDepth(){
        if(choice==choiceState.A){
            levels.get(currentLevel).get(storyLineIndex).choiceA.depth=calculateDepth(levels.get(currentLevel).get(storyLineIndex).choiceA);
        }else{
            levels.get(currentLevel).get(storyLineIndex).choiceB.depth=calculateDepth(levels.get(currentLevel).get(storyLineIndex).choiceB);
        }
        lineDepth=0;
    }


    public static class ArenaBounds{
        Rectangle bounds;
        String name;
        public ArenaBounds(String name){
            switch(name){
                case "left":{
                    bounds=new Rectangle(0,0,320,720);
                }break;
                case "right":{
                    bounds=new Rectangle(1280-320,0,320,720);
                }break;
                case "up":{
                    bounds=new Rectangle(0,720-120,1280,120);
                }break;
                case "down":{
                    bounds=new Rectangle(0,0,1280,120);
                }break;
            }
        }
        public Rectangle getBounds(){
            return bounds;
        }
    }
    public static class StoryLine {
        public String message;
        public String byLine;
        public int depth=0;
        public StoryLine choiceA=null,choiceB=null;

        enum choiceState{
            Nill,
            A,
            B,
        }
        choiceState choice=null;
        public StoryLine(String message, String byLine) {
            this.message = message.toUpperCase();
            this.byLine = byLine.toUpperCase();
        }
        public StoryLine(String message, String byLine, StoryLine choiceA) {
            this.message = message.toUpperCase();
            this.byLine = byLine.toUpperCase();
            this.choiceA = choiceA;
//            print(depth);
        }
        public StoryLine(String message, String byLine, StoryLine choiceA, StoryLine choiceB) {
            this.message = message.toUpperCase();
            this.byLine = byLine.toUpperCase();
            this.choiceA = choiceA;
            this.choiceB = choiceB;
            choice=choiceState.Nill;
        }
    }

    public static class LoadButton{
        public float y=720/3f,x,progress;
        public int index;
        private BitmapFont font;
        private Sprite object;
        public LoadButton(float x, int index, float progress, TextureRegion[] buttonSheet){
            this.x=x;
            this.progress=progress;
            this.index=index;
            this.object= new Sprite(buttonSheet[index==0?0:1]);
            this.font=new BitmapFont(files("joystix.fnt"));
            object.setScale(5f);
            object.setOriginCenter();
            object.setPosition(x,y);
        }
        public void render(SpriteBatch batch, float timeElapsed) {
            float alpha=1;
            object.draw(batch);
            if(index!=0)font.draw(batch,progress+"%",x-50,y+50);
            if (index == loadButtonIndex)alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 1.5 * Math.PI));
            font.setColor(1,1,1,(index==loadButtonIndex)?alpha:1);
            object.setAlpha(index == loadButtonIndex ? alpha : 1);
        }


    }
    public static class MenuButton {
        public final String name;
        public int index;
        public Rectangle bounds;
        private final float x,y;
        private float alpha;
        private final BitmapFont font;

        public MenuButton(float y, String name, int index) {
            this.font = new BitmapFont(files("joystix.fnt"));
            GlyphLayout layout = new GlyphLayout(font, name);
            this.x = (1280 - layout.width) / 2;
            this.y = y;
            this.name = name;
            this.index = index;
            this.bounds = new Rectangle(x, y - layout.height, layout.width, layout.height);
        }

        public void render(SpriteBatch batch, float timeElapsed) {
            font.draw(batch, name, this.x, this.y);
            if (index == menuButtonActiveIndex) {
                alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 3 * Math.PI));
            }
            font.setColor(1, 1, 1, index == menuButtonActiveIndex ? alpha : 1);
        }

        public Boolean isTouching(Vector2 touch) {
            return bounds.contains(touch);
        }
    }

    private static class controllerInput implements ControllerListener {
        @Override
        public void connected(Controller controller) {
            controllerConnected = true;
            controller.startVibration(300, 1f);
//            Gdx.app.log("Controller", "connected: " + controller.getName());
        }

        @Override
        public void disconnected(Controller controller) {
//            Gdx.app.log("Controller", "disconnected: " + controller.getName());
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
//            Gdx.app.log("Controller", "button down: " + buttonCode);
            return false;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            switch (gameState) {
                case Menu: {
                    if (buttonCode == 11) {
                        if (menuButtonActiveIndex > 0) menuButtonActiveIndex--;
                        else controller.startVibration(300, 0.5f);

                    }
                    if (buttonCode == 12) {
                        if (menuButtonActiveIndex < 3) menuButtonActiveIndex++;
                        else controller.startVibration(300, 0.5f);
                    }
                    if (buttonCode == 0) {
//                        controller.startVibration(400, 0.5f);
                        handleMenu();
                    }

                    if (buttonCode == 6) gameState = GameState.Load;
                }break;
                case Load:{
                    if(buttonCode==13){
                        if (loadButtonIndex > 0) loadButtonIndex--;
                        else controller.startVibration(300, 0.5f);
                    }
                    if(buttonCode==14){
                        if (loadButtonIndex <2) loadButtonIndex++;
                        else controller.startVibration(300, 0.5f);
                    }
                    if(buttonCode==1){
                        gameState=GameState.Menu;
//                        controller.startVibration(200, 0.7f);
                    }
                    if(buttonCode==0){
                        gameState=GameState.Play;
                        drawTextTime=10f;
                    }

                }break;
                case Instructions:{
                    if(buttonCode==1){
                        gameState=GameState.Menu;
//                        controller.startVibration(200, 0.7f);
                    }
                }break;
                case Play:{

                    if(choiceMode){
                        if(buttonCode==13){
                            choice=choiceState.A;
                        }
                        if(buttonCode==14){
                            choice=choiceState.B;
                        }
                        if(buttonCode==0){
                            levels.get(currentLevel).get(storyLineIndex).choice = choice==choiceState.A?StoryLine.choiceState.A:StoryLine.choiceState.B;
                            choiceMode=false;
                            setChoiceDepth();
                        }
                    }
                    if(buttonCode==0){
                        if(drawingDialogue&&drawingText&&!choiceMode){
                            if(typewriterIndex<dialogueMessage.length()){
                                typewriterIndex=dialogueMessage.length();
                                typewriter=dialogueMessage;
                            }else lineSkip=true;
                        }
                    }

                }

            }
//            Gdx.app.log("Controller", "button up: " + buttonCode);
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            switch (gameState) {
                case Menu: {
                    if (axisCode == 1) {
                        if ((MathUtils.floor(value) == -1)) {
                            if (menuButtonActiveIndex > 0) menuButtonActiveIndex--;
                            else controller.startVibration(300, 0.5f);
                        }
                        if ((MathUtils.floor(value) == 1)) {
                            if (menuButtonActiveIndex < 3) menuButtonActiveIndex++;
                            else controller.startVibration(300, 0.5f);
                        }

                    }
                }
                break;
                case Load:{
                    if(axisCode==0){
                        if ((MathUtils.floor(value) == -1)) {
                            if (loadButtonIndex > 0) loadButtonIndex--;
                            else controller.startVibration(300, 0.5f);
                        }
                        if ((MathUtils.floor(value) == 1)) {
                            if (loadButtonIndex < 2) loadButtonIndex++;
                            else controller.startVibration(300, 0.5f);
                        }
                    }
                }break;
                case Play:{
                    if(fight){
                        if(axisCode==0){
                            if (MathUtils.floor(value) == -1) {
                                playerTurnLeft = false;
                                playerTurnRight = true;
                            }
                            if (MathUtils.floor(value) == 1) {
                                playerTurnRight = false;
                                playerTurnLeft = true;
                            }
                            if (MathUtils.floor(value) == 0) {
                                playerTurnRight = false;
                                playerTurnLeft = false;
                            }
                        }
                    }
                        if(axisCode==0&&drawingDialogue){
                            if ((MathUtils.floor(value) == -1)) {
                                choice=choiceState.A;
                            }
                            if ((MathUtils.floor(value) == 1)) {
                                choice=choiceState.B;
                            }
                        }
                }
            }
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
            switch (gameState) {
                case Menu: {
                    if ((keycode == Input.Keys.UP||keycode == Input.Keys.W) && menuButtonActiveIndex > 0) {
                        menuButtonActiveIndex--;
                    }
                    if ((keycode == Input.Keys.DOWN||keycode == Input.Keys.W) && menuButtonActiveIndex < 2) {
                        menuButtonActiveIndex++;
                    }
                    if (keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE) {
                        handleMenu();
                    }
                }break;
                case Load:{
                    if((keycode==Input.Keys.A||keycode==Input.Keys.LEFT) && loadButtonIndex>0){
                        loadButtonIndex--;
                    }
                    if((keycode==Input.Keys.D||keycode==Input.Keys.RIGHT) && loadButtonIndex<2){
                        loadButtonIndex++;
                    }
                    if (keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE) {
                        gameState=GameState.Play;
                        drawTextTime=10f;
                    }
                }break;
                case Play:{
                    if (choiceMode) {
                        if((keycode==Input.Keys.A||keycode==Input.Keys.LEFT) ){
                            choice=choiceState.A;
                        }
                        if((keycode==Input.Keys.D||keycode==Input.Keys.RIGHT)){
                            choice=choiceState.B;
                        }
                        if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                            levels.get(currentLevel).get(storyLineIndex).choice = choice==choiceState.A?StoryLine.choiceState.A:StoryLine.choiceState.B;
                            choiceMode=false;
                            setChoiceDepth();
                        }
                    }

                    if (keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE) {
                        if(drawingDialogue&&drawingText&&!choiceMode){
                            if(typewriterIndex<dialogueMessage.length()){
                                typewriterIndex=dialogueMessage.length();
                                typewriter=dialogueMessage;
                            }else lineSkip=true;
                        }
                    }
                }break;
                case Instructions:{
                    if(checkExitKey(keycode)){
                        gameState=GameState.Menu;
                    }
                }

            }
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            touch = new Vector3(screenX, screenY, 0);
            camera.unproject(touch);
            point = new Vector2(touch.x, touch.y);
            switch (gameState) {
                case Menu: {
                    for (MenuButton button : menuButtonArray) {
                        if (button.isTouching(point)) menuButtonActiveIndex = button.index;
                    }
                }break;
                case Load:{
                    for(LoadButton button : loadButtonArray){
                        if (button.object.getBoundingRectangle().contains(point)) loadButtonIndex= button.index;
                    }
                }break;
                case Play:{
                    if(choiceABounds.contains(point)){
                        choice=choiceState.A;
                    }
                    if(choiceBBounds.contains(point)){
                        choice=choiceState.B;
                    }
                }break;
            }


            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {

            return true;
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer){
            touch = new Vector3(screenX, screenY, 0);
            camera.unproject(touch);
            point = new Vector2(touch.x, touch.y);
            switch (gameState) {
                case Menu: {
                    for (MenuButton btn : menuButtonArray) {
                        if (btn.isTouching(point)) {
                            menuButtonActiveIndex = btn.index;
                        }
                    }
                }break;
                case Load: {
                    for (LoadButton btn : loadButtonArray) {
                        if (btn.object.getBoundingRectangle().contains(point)) {
                            loadButtonIndex = btn.index;
                        }
                    }
                }break;
                case Play:{
                    if(choiceABounds.contains(point)){
                        choice=choiceState.A;
                    }
                    if(choiceBBounds.contains(point)){
                        choice=choiceState.B;
                    }
                }break;
            }



        return true;
        }
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            touch = new Vector3(screenX, screenY, 0);
            camera.unproject(touch);
            point = new Vector2(touch.x, touch.y);
            switch (gameState) {
                case Menu: {
                    for (MenuButton btn : menuButtonArray) {
                        if (btn.isTouching(point)) {
                            menuButtonActiveIndex=btn.index;
                            handleMenu();
                        }
                    }
                }break;
                case Load:{
                    boolean exit=true;
                    for(LoadButton btn: loadButtonArray){
                        if(btn.object.getBoundingRectangle().contains(point)){
                            gameState=GameState.Play;
                            drawTextTime=10f;
                            exit=false;
                        }
                    }
                    if(exit)gameState=GameState.Menu;
                }break;
                case Play:{
                    if(choiceMode){
                        if(choiceABounds.contains(point)){
                            levels.get(currentLevel).get(storyLineIndex).choice= StoryLine.choiceState.A;
                            choiceMode=false;
                            setChoiceDepth();
                        }
                        if(choiceBBounds.contains(point)){
                            levels.get(currentLevel).get(storyLineIndex).choice= StoryLine.choiceState.B;
                            choiceMode=false;
                            setChoiceDepth();
                        }
                    }
                    if(drawingDialogue&&drawingText&&!choiceMode){
                        if(typewriterIndex<dialogueMessage.length()){
                            typewriterIndex=dialogueMessage.length();
                            typewriter=dialogueMessage;
                        }else lineSkip=true;
                    }
                }break;
                case Instructions: {
                        gameState=GameState.Menu;
                }break;
            }
            return true;
        }
    }

}
