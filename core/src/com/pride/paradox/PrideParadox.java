package com.pride.paradox;

import static com.pride.paradox.Print.index;
import static com.pride.paradox.Print.print;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
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

import java.util.Objects;


public class PrideParadox extends ApplicationAdapter {
    public static GameState gameState = GameState.Menu;
    public static Vector3 touch,mousePos;
    public static Vector2 point;
    public static Sprite player,transition,leftChar,rightChar;
    public static OrthographicCamera camera;
    public Texture background;
    public BitmapFont dialogueFont,choiceFont;
    public static float score,transitionAlpha=0f,health=10000,playerTime=0,playerFPS= 0.08F,shootTimeOut=0, timeElapsed = 0, gameSavedTime=0,controllerConectTime = 0f,drawTextTime=0,textDuration=0f;
    public static int overButtonActiveIndex=0,kills=0,frameIndex=0,saveIndex=0,pauseButtonActiveIndex=0,menuButtonActiveIndex = 0,loadButtonIndex=0,typewriterIndex=0,currentLevel=0,currentWave=-1,storyLineIndex=0,lineDepth=0,playerAnimationId=0;
    public static Boolean gameSaved=false,playerHurt=false,gameStarted=false,mouseControlActive=false,controllerConnected=false,drawingDialogue=true,drawingText=true,fight=false,lineSkip=false,choiceMode=false,playerTurnLeft=false,playerTurnRight=false,playerForward=false,playerBackward=false,fireProjectile=false,fireKey=false;
    public static Array<MenuButton> menuButtonArray = new Array<>();
    public static Array<OverButton> overButtonList =new Array<>();
    public static Array<ExplosionEffect> explosionList=new Array<>();
    public static Array<PauseButton> pauseButtons=new Array<>();
    public static Array<HealthBar> healthBars=new Array<>();
    public static Array<GameButton> gameButtonList = new Array<>();
    public static Array<LoadButton> loadButtonArray= new Array<>();
    public static Array<Array<StoryLine>> gameStory=new Array<>();
    public static Array<Projectile> projectileList=new Array<>();
    public static Array<EnemyClass> enemyList=new Array<>();
    public static Array<EnemyWave[]> enemyWaves= new Array<>(6);
    public static Array<MobileButton> mobileButtonList = new Array<>();
    public static Array<Animation<TextureRegion>> playerAnimation= new Array<>(3);
    public static StoryLine currentLine;
    public Viewport viewport;
    public static Circle playerBounds;
    public static Rectangle choiceABounds,choiceBBounds;
    public InputProcessor input;
    public Texture menuBG;
    public Texture cursorTexture;
    public Texture gamepadConnect,gameSave;
    public Texture htp;
    public Texture arena;
    public Texture bgTexture;
    public static Texture gameBG;
    public TextureRegion playerSheet;
    public static TextureRegion playerFrame;
    public TextureRegion[] loadButtonSheet,mobileButtonSheet,gameButtonSheet,kidSheet,dogSheet;
//    public ShapeRenderer shapeRenderer;
    public static String typewriter,dialogueMessage="";
    public static EnemyAction FollowPlayer,StayAround;
    SpriteBatch batch;
    BitmapFont titleFont;
    GlyphLayout layout,scoreLayout;
    static String[] menuButtonNames = {"START", "HOW TO PLAY?","GAME RESET" ,"EXIT"};
    static String[] overButtonNames={"RETRY","MENU","QUIT"};
    static String[] pauseButtonNames={"RESUME","SAVE","HOW TO PLAY?","MENU","QUIT"};
    String[] mobileButtonNames={"look","icon","fire","forward","backward"};
    String[] arenaBoundNames={"up","down","left","right"};
    public static EnemyType Kid,Dog,Doctor,Scammer,Bot,Politician;
    public static Array<ArenaBounds> arenaBounds=new Array<>();
    public enum GameState {Menu, Load, Pause, Play, Instructions,Over}
    public enum choiceState{A,B}
    static choiceState choice=choiceState.A;
    public static Preferences[] gameSaves=new Preferences[3];

    public static FileHandle files(String input){
        return Gdx.files.internal(input);
    }
    public enum RenderWhat{Left,BG,Right}

    public enum EnemyActionType{ThrowVer,Attack,Look,Move,Around, Bounce, Turn, Fall, Target, FaceCenter, PointAway, ThrowHor, Rotate}


    public static void handleOver(){
        switch(overButtonActiveIndex){
            case 0:{
                health=10;
                initialize();
                gameState=GameState.Play;
            }break;
            case 1:{
                gameState=GameState.Menu;
            }break;
            case 2:{
                Gdx.app.exit();
            }break;
        }
    }
    public static void loadGame(int saveIndex){
        currentLevel=gameSaves[saveIndex].getInteger("level",2);
        health=gameSaves[saveIndex].getFloat("score",0);
        kills=gameSaves[saveIndex].getInteger("kills",0);
        if(health==0)health=10;
    }
    public static void initializeStory(){
        for(StoryLine level : gameStory.get(currentLevel)){
            level.depth=calculateDepth(level);
        }
    }
    public static void createStory(){
        gameStory.clear();
        gameStory=new Array<>();

        gameStory.add(new Array<>());
        gameStory.get(0).add(new StoryLine("The year is 2047. India has faced and overcome many challenges, Epidemics, Mass Extinction, World War 3, and a severe Food Crisis",new StoryLine("The country has emerged victorious, with people finding jobs and the caste system being a thing of the past.", new StoryLine("However, acceptance of the LGBTQ+ community is still lacking in many areas, including government jobs and private companies.",new StoryLine("Our story is set in a bustling city. Despite progress, LGBTQ+ individuals are still treated unfairly.",new StoryLine("They have had enough and decide to protest, demanding equal rights and the opportunity to fulfill their dreams.",new StoryLine("They have had enough and decide to protest, demanding equal rights and the opportunity to fulfill their dreams.",new StoryLine("Among the protestors is the child of our protagonist, who has identified himself as a bisexual but faces discrimination at school.",new StoryLine("His father warns him to stay away from the rally, but the child sneaks out to join it anyway.",new StoryLine("The peaceful rally turns into chaos when a violent group attacks, leading to a mass shootout.", new StoryLine("People run in all directions, and the police get involved.",new StoryLine("Hearing about the violence, the protagonist rushes to the rally, only to learn from a police officer that his child, who was at the front, has died.",new StoryLine("Grief-stricken, the protagonist blames himself and the world. He spends years in mourning,",new StoryLine("wishing he could change the past to make India more accepting of LGBTQ+ individuals.",false),RenderWhat.Left,"man-stand.png"),RenderWhat.BG,"sad.jpeg")),RenderWhat.BG,"tragedy.jpeg")))))),RenderWhat.BG,"rally.jpeg"))));
        gameStory.get(0).add(new StoryLine("One day, he learns about a time machine experiment in an underground lab in China.",new StoryLine("Determined to prevent his child's fate, he uses his software skills to bypass security and access the time machine.",new StoryLine("Without hesitation, the protagonist travels back 32 years to 2015, finding himself in his younger body.",new StoryLine("He is determined to change the course of history and ensure a future where his child and all LGBTQ+ individuals are accepted and valued.",new StoryLine("Player : This is unbelievable! I am finally back in past! I feel so young... is this my home?",new StoryLine("Player : Judging by my home decoration, I think i am somewhere between 2012 and 2016.",new StoryLine("Player : I should head out, i don't want anyone noticing me as an adult. Also, how will i go back to the future!?",new StoryLine("You get out of your home to find out the fresh breeze outside",new StoryLine("Player : Wow i missed this place so much!",false),RenderWhat.BG,"dog-bg.jpeg"),RenderWhat.BG,"empty.png")),RenderWhat.BG,"house.jpeg"),RenderWhat.Left,"player-stand.png"),RenderWhat.BG,"time.jpg"),RenderWhat.Left,"man-stand.png"),RenderWhat.BG,"lab.jpeg"));
        gameStory.get(0).add(new StoryLine("Player : What the hell is that huge dog!!",new StoryLine("Dog : Who's this new person? I must jump on them to show my excitement!",new StoryLine("Player : Um Sho Shoo get lost dog! Where is your owner? Where is your belt?!",new StoryLine("Dog : Woof! Woof! (A toddler! hmm, am I allowed to maul him to death)",new StoryLine("The dog has become aggressive! You must use your power of righteousness to get away from this dog!",true)))),RenderWhat.Right,"dog-stand.png"));
        gameStory.get(0).add(new StoryLine("Dog : Woof! (I jump on people because no one taught me not to. Isn't it fun?)",new StoryLine("Player : This dog has no manners! It seems as if he was abandoned by his owners as a pup",true)));
        gameStory.get(0).add(new StoryLine("Player : Phew, he is finally calmed down...",false));
        gameStory.get(0).add(new StoryLine("Dog : woof... woof... (I act out because no one showed me love or boundaries.)",new StoryLine("Punish him!",new StoryLine("You grab the dog by his jaws and punch him until he is life less.",new StoryLine("The dog is no longer barking... knowing that the dog won't trouble the locals anymore you feel proud.", false),RenderWhat.Right,"empty.png")),new StoryLine("Pet him!",new StoryLine("You pet him lovingly, the dog is big in size and has been feared for it, ever since he was abbandoned all he wanted was love from someone",new StoryLine("Dog : Woof! (No one trained me to be good, so being bad is all I know. Now on... I shall not trouble the civillians for my fun.)",new StoryLine("You call the animal control to give this dog asylum, you hope to meet him again...",false),RenderWhat.Right,"empty.png")))));
        gameStory.get(0).add(new StoryLine("Well done! you made it through the first level! You can save your game from the next dialogue onwards.",false));

        gameStory.add(new Array<>());
        gameStory.get(1).add(new StoryLine("As you leave behind your village, you come across an old playground where you used to play cricket in... you get nostalgia!",new StoryLine("You remember how you used to practice to get good in the game and never gave up.", new StoryLine("You remember how you and your friends would go get a pav bhaji after a long sweaty game with all those brusies.",new StoryLine("Player : I really miss my childhood... I wish I could taste that chilled purified water back home.", new StoryLine("As you enjoy the view a football hits you in the back.",new StoryLine("Player : Hey, do you not see I am standing here? The court is just ahead go play there!",false),RenderWhat.Right,"kid-stand.png")))),RenderWhat.BG,"ground.jpeg"));
        gameStory.get(1).add(new StoryLine("A kid from afar smirks at you menacingly",new StoryLine("You wave your profiler tool in your watch to get information on the kid", new StoryLine("Foolish of you as the requests can't time travel into the future to a database, maybe you are not the brightest person to time travel.",new StoryLine("The kid is clearly a football fanatic. You should try dodging with your old goofy ahhh",new StoryLine("Kid : You look so gay in that appeal.",new StoryLine("You think to yourself- this kid probably boasting his delinquent connections and taking advantage of it.", new StoryLine("You think to yourself- this kid has clearly bad influence from his toxic environment and doesn't knows what right for him. Calling people chakka as a derogetory word...",new StoryLine("Player : Hey kid would you kick the ball at me once again?",true)))))))));
        gameStory.get(1).add(new StoryLine("Kid : you are surprisingly well, you don't come from here do you? where are you from?",new StoryLine("Player : That is least of your concern, why did you call me gay in the first place? do you think that's funny?", new StoryLine("Kid : Well, why not? They are the bottom feeders of the society, I don't feel safe around them, they are accused to be traffickers.",new StoryLine("You think to yourself- he is correct around this time the crime rates of such things were more...",new StoryLine("Suddenly another football comes at you!",new StoryLine("Kid : How pitiful of you to side on with those weird creeps, are you one of them?",true)))))));
        gameStory.get(1).add(new StoryLine("Player : Look the only reason they resort on doing bad work is because we don't give them opportunity! You should look at the world from their perspective as well!",new StoryLine("The kid is confused... he has some self doubts but comes back at giving you another fight",true)));
        gameStory.get(1).add(new StoryLine("The kid is now exhausted from giving his all",new StoryLine("Kid : You know what? maybe its right... I have never tried to see from other people's perspective, they have a different past...", new StoryLine("Kid : I have read in many articles that families have disowned the kids that came out to be not straight... what would happen if I was in their place...",new StoryLine("Player : Now, do you get it? It may look cool and all to say these slangs to make you look a little different from the crowd.",new StoryLine("Player : but that will absolutely will not change anything as people won't seem to care about it.", new StoryLine("Player : All that matters is what can you give to your country, so you will be remembered and loved.",new StoryLine("Kid : Blah blah blah shut up with your nonsense... (The kid tries to catch a breath from your long fairy tale)",new StoryLine("Kid : You know what? there is doctor that is psychotic you should really see him, he is able to treat the non binary people.", new StoryLine("Player : So... where exactly can I find this fellow?",new StoryLine("You received a map from the kid",new StoryLine("Kid : Well... this is all I can offer, it was nice to meet you. I have to get back home now",new StoryLine("With this you head to the clinic the boy's gifted map",new StoryLine("and with this the second level comes to an end!",false),RenderWhat.Right,"empty.png")))))))))))));

        gameStory.add(new Array<>());
        gameStory.get(2).add(new StoryLine("As you walk for hours through the city you finally find the clinic that has been known for making people straight",new StoryLine("You enter the clinic... the clinic is clean and shiny, you wonder how this doctor supposedly treats the people? could it be hypnosis?",new StoryLine("As you wait inside the clinic you look around yourself watching parents with their kids all quiet",new StoryLine("you act like your wife making you fit in with the other peoples there, and the clerk has noticed you. Its your turn to get treated",new StoryLine("As you enter the treatment room you find a doctor holding a syringe.",new StoryLine("Doctor : Come lay flat on the bed child, this shall not hurt you at all",new StoryLine("Player : What is this scam that you are running? Are you even a certified doctor?",new StoryLine("The doctor shows you all his degree in medical science. You are now convinced",new StoryLine("As the doctor approaches towards you, you get ready for the duel",true))))),RenderWhat.Right,"doctor-stand.png"))),RenderWhat.BG,"clinic.jpeg"));
        gameStory.get(2).add(new StoryLine("Player : What are all these pills you are giving me? T-E-S-T-O-S-T-E-R-O... Testosterone?",new StoryLine("Doctor : Wow you are quiet the reader... (I hope he doesn't realize what it means...)",true)));
        gameStory.get(2).add(new StoryLine("Player : Your cover has finally blown up doctor! admit it, you are fooling people instead of giving them the right advice!",new StoryLine("Doctor : Idiot... people here don't want to listen to the science... they just want the truth and I simply give an alternative solution",new StoryLine("Doctor : It's a basic supply and demand thing. You should not meddle with this. Get lost",new StoryLine("As the doctor approaches you to take you out of the clinic the duel begins!",true)))));

    }


    public void drawChoice(SpriteBatch batch,StoryLine line){
        float alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 1.5 * Math.PI));
        choiceFont.setColor(1,1,1,(choice==choiceState.A)?alpha:1);
        choiceFont.draw(batch,line.choiceA.message,choiceABounds.x,choiceABounds.y+choiceABounds.height-10f,choiceABounds.width, Align.center,true);
        choiceFont.setColor(1,1,1,(choice==choiceState.B)?alpha:1);
        choiceFont.draw(batch,line.choiceB.message,choiceBBounds.x,choiceBBounds.y+choiceBBounds.height-10f,choiceBBounds.width, Align.center,true);
    }
    public void drawText(SpriteBatch batch, StoryLine line){
        dialogueMessage=line.message;
        if(drawTextTime<0.01){
            drawTextTime+=Gdx.graphics.getDeltaTime();
        }else {
            drawTextTime=0;
            if(typewriterIndex<dialogueMessage.length()){
                typewriterIndex++;
                typewriter=dialogueMessage.substring(0,typewriterIndex);
            }else {
                if(lineSkip){
                    if(((gameStory.get(currentLevel).get(storyLineIndex).depth!=lineDepth+1)&&gameStory.get(currentLevel).get(storyLineIndex).depth!=0)){
                        lineDepth++;
                    }
                    else {
                        lineDepth=0;
                        storyLineIndex++;
                    }
                    if(storyLineIndex==gameStory.get(currentLevel).size){
                        currentLevel++;
                        storyLineIndex=0;
                        currentWave=0;
                        print("level : "+currentLevel);
                    }

                    lineSkip=false;
                    textDuration=0f;
                    typewriterIndex=0;
                    drawTextTime=10f;
                    if(line.fightState){
                        currentWave++;
                        initializeLevel(currentLevel,currentWave);
                        print(currentLevel+" : "+currentWave);
                        fight=true;
                        drawingText=false;
                        drawingDialogue=false;
                    }
                    return;
                }
            }
        }
        batch.draw(background,1280/2f-450,0,900,800);
        if(drawingText)dialogueFont.draw(batch,typewriter,270,245,745, Align.topLeft,true);
    }

    public static int calculateDepth(StoryLine storyLine){
        int depth=0;
        for(StoryLine current = storyLine;current!=null;current=current.choiceA){
            depth++;
//                print(current.message);
        }
        return depth;
    }


    public static void saveGame(int saveIndex){
        gameSaves[saveIndex].putInteger("level",currentLevel);
        gameSaves[saveIndex].putFloat("score",score);
        gameSaves[saveIndex].putInteger("kills",kills);
        gameSaves[saveIndex].flush();
        gameSaved=true;
        gameSavedTime=0;
    }
    public static void handlePause(){
        switch (pauseButtonActiveIndex) {
            case 0: {
                gameState = GameState.Play;
                gameStarted=true;
            }
            break;
            case 1: {
                saveGame(saveIndex);
            }
            break;
            case 2: {
                gameState=GameState.Instructions;
            }break;
            case 3: {
                gameState=GameState.Menu;
                gameStarted=false;
            }break;
            case 4: {
                Gdx.app.exit();
            }break;

        }
    }
    public static void handleMenu() {
        switch (menuButtonActiveIndex) {
            case 0: {
                gameState = GameState.Load;
            }
            break;
            case 1: {
                gameState = GameState.Instructions;
                gameStarted=false;
            }
            break;
            case 2:{
                clearProgress();
            }break;
            case 3: {
                Gdx.app.exit();
            }break;
        }
    }
    public static Boolean checkExitKey(int keycode){
        return keycode==Input.Keys.X ||keycode==Input.Keys.ESCAPE || keycode==Input.Keys.CONTROL_RIGHT;
    }

    public static void clearProgress(){
        for(Preferences prefs : gameSaves)prefs.clear();
    }

    public static TextureRegion[] extractSprites(String name,int width,int height){
        TextureRegion sheet =new TextureRegion(new Texture(files(name)));
        return  sheet.split(width,height)[0];
    }


    public static void initializeLevel(int currentLevel,int currentWave){
        enemyList.clear();
        enemyList.addAll(enemyWaves.get(currentLevel)[currentWave].enemies);
    }


    public static void initialize(){
//        initializeEnemyType();
        initializeStory();
//        initializeLevel(currentLevel,currentWave);
        initializeImages();
    }
    public static void drawPlayer(SpriteBatch batch){
        frameIndex = (int) (playerTime / playerFPS) % playerAnimation.get(playerAnimationId).getKeyFrames().length;
        playerFrame = playerAnimation.get(playerAnimationId).getKeyFrames()[frameIndex];
        playerTime+=Gdx.graphics.getDeltaTime();
        if(playerTurnLeft)player.rotate(-3.5f);
        if(playerTurnRight)player.rotate(3.5f);
        shootTimeOut+=Gdx.graphics.getDeltaTime();

        if(fireProjectile){
            if(frameIndex==7 && shootTimeOut>0.05) {
                projectileList.add(new Projectile(player.getX(),player.getY(),player.getRotation()));
                shootTimeOut=0;
            }
            if(playerAnimationId!=1){
                playerAnimationId=1;
                playerFPS=0.02f;
            }
            if(!fireKey&&frameIndex>12){
                fireProjectile=false;
                playerTime=0;
            }
        }else{
            if(playerAnimationId==1){
                playerAnimationId=0;
            }
        }

        if(playerAnimationId==2 && frameIndex>3){
            playerAnimationId=1;
            print("player hurt");
            playerHurt=false;
            playerFPS=0.02f;
        }

        if(playerForward||playerBackward){
            float radians=MathUtils.degreesToRadians*(player.getRotation()+90);
            float amplitude=3*(playerForward?-1:1);
            player.translate(amplitude* MathUtils.cos(radians),amplitude* MathUtils.sin(radians));
            for(ArenaBounds bounds:arenaBounds){
                if(bounds.bounds.overlaps(player.getBoundingRectangle())){
                    player.translate(-4f*amplitude* MathUtils.cos(radians),-4f*amplitude* MathUtils.sin(radians));
                }
            }
        }
        player.setRegion(playerFrame);
        player.setSize(playerFrame.getRegionWidth(), playerFrame.getRegionHeight());
        player.setOrigin(player.getWidth() / 2, player.getHeight() / 2);
        player.draw(batch);
        playerBounds=new Circle(player.getX(),player.getY(),player.getRegionWidth()/2f);
    }


    public static void createWaves() {
        initializeEnemyType();

        timeElapsed=0;
        enemyList.clear();
        enemyWaves.clear();

        enemyWaves.addAll(
                new EnemyWave[]{
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                },false),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                        new EnemyClass(Dog,0,2,false,0,MathUtils.random(1,2.5f)),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,1,1,false,0,MathUtils.random(1.5f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                        new EnemyClass(Dog,2,MathUtils.random(0,1),true,0,MathUtils.random(1f,3f)),
                                },true)
                },
                new EnemyWave[]{
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Kid,1,0,false,0,1f),
                                        new EnemyClass(Kid,1,0,false,90,1f),
                                        new EnemyClass(Kid,1,0,false,180,1f),
                                        new EnemyClass(Kid,1,0,false,270,1f),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,1,0,false,0,1f),
                                        new EnemyClass(Kid,1,0,false,90,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                        new EnemyClass(Kid,2,1,false,0,1f),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,2,0,false,0,1f),
                                        new EnemyClass(Kid,2,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,0,false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,3,MathUtils.random(0,1),false,0,1f),
                                        new EnemyClass(Kid,1,1,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,0,1f),
                                        new EnemyClass(Kid,0,0,false,90,1f),
                                        new EnemyClass(Kid,0,0,false,90,1f),
                                        new EnemyClass(Kid,1,1,false,180,1f),
                                },true),
                },
                new EnemyWave[]{
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,1,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                        new EnemyClass(Doctor,0,1,false,0,1f),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,0,false,0,1.5f),
                                },true),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,1,0,false,0,1.5f),
                                        new EnemyClass(Doctor,2,1,false,0,1.5f),
                                        new EnemyClass(Doctor,2,1,false,0,1.5f),
                                        new EnemyClass(Doctor,2,1,false,0,1.5f),
                                        new EnemyClass(Doctor,2,1,false,0,1.5f),
                                },false),
                        new EnemyWave(
                                new EnemyClass[]{
                                        new EnemyClass(Doctor,3,1,false,0,1.5f),
                                        new EnemyClass(Doctor,3,1,false,120,1.5f),
                                        new EnemyClass(Doctor,3,1,false,240,1.5f),
                                        new EnemyClass(Doctor,3,1,false,240,1.5f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                        new EnemyClass(Doctor,4,0,false,-90,1.75f),
                                },true),
                }
        );

//        print(enemyWaves.get(1)[0].enemies[0].animationList.size);


//    enemyWaves.addAll(
//
//            new EnemyWave(new EnemyClass[]{
//                    new EnemyClass(Kid,0,0,300,30,0,3f),
//                    new EnemyClass(Kid,0,0,300,30,0,3f)
//            },false),
//
//            new EnemyWave(new EnemyClass[]{
//                    new EnemyClass(Kid,0,0,300,30,0,3f),
//                    new EnemyClass(Kid,0,0,300,30,0,3f)
//            },false)
//    );


    }

    public static void initializeEnemyType(){

        Array<EnemyAnimation[]> kidAanimation=new Array<>(),dogAnimation = new Array<>(),doctorAnimation=new Array<>();

        doctorAnimation.clear();
        doctorAnimation.addAll(
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.ThrowVer,30,1),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.ThrowVer,30,-1),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(FollowPlayer.type,30,35),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Attack,25f,25),
                        new EnemyAnimation(EnemyActionType.Move,5f,5),
                        new EnemyAnimation(EnemyActionType.Look,2f,0),
                        new EnemyAnimation(EnemyActionType.Look,1f,1),
                        new EnemyAnimation(StayAround.type,30,250),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Fall,10f,5),
                }
        );
        Doctor=new EnemyType(extractSprites("doctor.png",64,64),doctorAnimation,"doctor");


        dogAnimation.clear();
        dogAnimation.addAll(
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Fall,30,0),
                        new EnemyAnimation(EnemyActionType.Fall,30,0)
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Attack,20f,200),
                        new EnemyAnimation(EnemyActionType.Attack,100f,35),
                        new EnemyAnimation(EnemyActionType.Look,6,1),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Target,10,4),
                }
        );


        Dog=new EnemyType(extractSprites("dog.png",32,32),dogAnimation,"dog");


        kidAanimation.clear();

        kidAanimation.addAll(
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Bounce,200,4),
                        new EnemyAnimation(EnemyActionType.Move,2,2),
                        new EnemyAnimation(EnemyActionType.Look,4,1)
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.Move,6,4),
                        new EnemyAnimation(EnemyActionType.Look,0.5f,2),
                        new EnemyAnimation(EnemyActionType.Move,6,4),
                        new EnemyAnimation(EnemyActionType.Look,0.5f,2),
                        new EnemyAnimation(EnemyActionType.Move,6,4),
                        new EnemyAnimation(EnemyActionType.Look,3,0),
                        new EnemyAnimation(StayAround.type,20,200),
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.ThrowHor,20,1)
                },
                new EnemyAnimation[]{
                        new EnemyAnimation(EnemyActionType.ThrowHor,20,-1)
                }
        );

        Kid=new EnemyType(extractSprites("kid.png",64,64),kidAanimation,"kid");

    }


    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(files("cursor.png"));
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        input = new InputProcessor();
        Gdx.input.setInputProcessor(input);
        Controllers.addListener(new controllerInput());
        mouseControlActive=(Gdx.app.getType()!=Application.ApplicationType.Android)&&(Gdx.app.getType()!=Application.ApplicationType.iOS);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);
        camera.setToOrtho(false, 1280, 720);
        viewport.apply();

        layout = new GlyphLayout();
        scoreLayout=new GlyphLayout();
        titleFont = new BitmapFont(files("joystix.fnt"));
        titleFont.getData().setScale(1.5f);
//		titleFont.getData().scale(2f);

//        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        menuBG = new Texture(files("thumb.png"));
        cursorTexture = new Texture(files("cursor.png"));
        gamepadConnect = new Texture(files("gamepad-connected.png"));
        htp=new Texture(files("htp.png"));
        dialogueFont = new BitmapFont(files("dialog.fnt"));
        choiceFont= new BitmapFont(files("choice.fnt"));
        background = new Texture(files("dialogue.png"));
        arena=new Texture(files("arena.png"));
        bgTexture=new Texture(files("bg.png"));
        gameSave=new Texture(files("game-saved.png"));

        for(String name: arenaBoundNames) arenaBounds.add(new ArenaBounds(name));

        dialogueFont.getData().setScale(0.87f);
        choiceFont.getData().setScale(1.25f);

        choiceABounds=new Rectangle(120,720/2f,1280/3f,100);
        choiceBBounds=new Rectangle(1280-120-1280/3f,720/2f,1280/3f,100);

        loadButtonSheet=extractSprites("loadSheet.png",64,64);
        mobileButtonSheet=extractSprites("buttons.png",64,64);
        gameButtonSheet=extractSprites("buttons-2.png",64,64);

        healthBars.add(new HealthBar(1280/2f -96,635,extractSprites("healthBarSheet.png",64,16),0));
        healthBars.add(new HealthBar(1280/2f -96,635,extractSprites("healthBarSheet.png",64,16),1));

        playerSheet=new TextureRegion(new Texture(files("player.png")));
        TextureRegion[][] totalFrames=playerSheet.split(32,32);

        int[] playerIndex = {5, 14, 5};
        int startIndex = 0;
        for (int frameCount : playerIndex) {
            TextureRegion[] frames = new TextureRegion[frameCount];
            System.arraycopy(totalFrames[0], startIndex, frames, 0, frameCount);
            startIndex += frameCount;
            playerAnimation.add(new Animation<>(0.1f, frames));
        }

        FollowPlayer=new EnemyAction(new Array<>(new EnemyActionType[]{EnemyActionType.Look,EnemyActionType.Attack}));
        StayAround=new EnemyAction(new Array<>(new EnemyActionType[]{EnemyActionType.Around,EnemyActionType.Turn}));

        player=new Sprite(playerAnimation.get(0).getKeyFrame(0));
        player.setPosition(1280/2f,720/2f);
        player.setOriginCenter();

        for(int i=0;i<3;i++){
            gameSaves[i]=Gdx.app.getPreferences("saves_"+i);
            loadButtonArray.add(new LoadButton(200+i*400,i,loadButtonSheet,gameSaves[i]));
        }


        int index = 0;
        for (String name : menuButtonNames) {
            menuButtonArray.add(new MenuButton(320 - index * 80, name, index));
            index++;
        }

        index=0;
        for(String name:overButtonNames){
            overButtonList.add(new OverButton(400 - index * 120, name, index));
            index++;
        }

        index=0;
        for(String name : pauseButtonNames){
            pauseButtons.add(new PauseButton(480-index*90,name,index));
            index++;
        }

        gameButtonList.add(new GameButton(60,0,gameButtonSheet));
        gameButtonList.add(new GameButton(180,1,gameButtonSheet));

        index=0;
        for(String name :mobileButtonNames){
            mobileButtonList.add(new MobileButton(name,mobileButtonSheet,index));
            index++;
        }
        playerBounds=new Circle(player.getX(),player.getY(),player.getRegionWidth()/2f);
        transition=new Sprite(new Texture(files("transition.png")));
        transition.setPosition(0,0);
        transition.setSize(1280,720);

        leftChar=new Sprite(new Texture(files("empty.png")));
        rightChar=new Sprite(new Texture(files("empty.png")));

        createStory();
        createWaves();
        initialize();

//        leftChar.setSize(leftChar.getWidth(), leftChar.getHeight());

        leftChar.setSize(leftChar.getWidth(), leftChar.getHeight());
        leftChar.setPosition(0,-20);

        rightChar.setSize(rightChar.getWidth(), rightChar.getHeight());
        rightChar.setPosition(1280-rightChar.getWidth(),-20);

        rightChar.setFlip(true,false);

        scoreLayout=new GlyphLayout(titleFont,(int) score+"");

//        for(EnemyAnimation[] anim : Dog.animations){
//            for( EnemyAnimation a : anim){
//                print(a.type+"");
//            }
//        }
//        for(EnemyAnimation[] anim : Kid.animations){
//            for( EnemyAnimation a : anim){
//                print(a.type+"");
//            }
//        }
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
//        print(currentLevel+" : "+currentWave);
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
                batch.draw(bgTexture, 0, 0, 1280, 720);
                titleFont.draw(batch,"PAUSE MENU",1280/2f-280,600);

                for(PauseButton btn : pauseButtons){
                    btn.render(batch,timeElapsed);
                }
            }break;

            case Play: {

                batch.draw(gameBG,0,0,1280,720);
                leftChar.draw(batch);
                rightChar.draw(batch);

                for(GameButton button :gameButtonList){
                    button.render(batch);
                }

                if(fight){


                    for(HealthBar bar : healthBars)bar.render(batch);
                    batch.draw(arena,1280/2f-640/2f,720/2f-480/2f,640,480);

                    titleFont.draw(batch,(int) score+"",1280/2f-scoreLayout.width/2f,scoreLayout.height*2);

                    drawPlayer(batch);

                    for(ExplosionEffect effect:explosionList){
                        effect.render(batch);
                        if(effect.die){
                            explosionList.removeValue(effect,true);
                        }
                    }
                    for(EnemyClass enemy : enemyList){
                        enemy.render(batch);
                        if(enemy.health<=0){
                            enemyList.removeValue(enemy,true);
                            scoreLayout=new GlyphLayout(titleFont,(int) score+"");
                            explosionList.add(new ExplosionEffect(enemy.bounds.x,enemy.bounds.y,1f));
                        }
                        if(enemy.bounds.overlaps(playerBounds)&&enemy.attackMode&&playerAnimationId!=2){
                            health-=1;
                            playerAnimationId=2;
                            playerHurt=true;
                            Gdx.input.vibrate(300);
                            if(Controllers.getControllers().size>0)Controllers.getControllers().first().startVibration(300,0.7f);
                            enemyList.removeValue(enemy,true);
                            explosionList.add(new ExplosionEffect(enemy.bounds.x,enemy.bounds.y,1f));
                            playerFPS=0.1f;
                        }
                    }

                    for(Projectile proj : projectileList){
                        proj.render(batch);
                        for(ArenaBounds bounds : arenaBounds)
                            if(proj.obj.getBoundingRectangle().overlaps(bounds.getBounds())){
                                projectileList.removeValue(proj,true);
                                explosionList.add(new ExplosionEffect((Objects.equals(bounds.name, "right"))?proj.obj.getX()+proj.obj.getWidth():proj.obj.getX(),(Objects.equals(bounds.name, "up"))?proj.obj.getY()+proj.obj.getHeight():proj.obj.getY(),0.2f));
                            }
                        for(EnemyClass enemy: enemyList){
                            if(enemy.getBounds(proj.getPoint())){
                                enemy.health-=1.5f;
                                score+=5;
                                projectileList.removeValue(proj,true);
                                explosionList.add(new ExplosionEffect(proj.obj.getX(),proj.obj.getY(),0.2f));
                            }
                        }
                    }

                    if(!mouseControlActive)for(MobileButton btn : mobileButtonList){
                        btn.render(batch);
                        btn.button.setScale(btn.active?4.2f:4f);
                    }

                    if(health<1){
                        gameState=GameState.Over;
                    }

                    if(enemyList.size<1 &&!enemyWaves.get(currentLevel)[currentWave].endFight){
                        currentWave++;
                        print(currentLevel+" : "+currentWave);
                        initializeLevel(currentLevel,currentWave);
                    }
                    if(enemyList.size<1 && health>0&&enemyWaves.get(currentLevel)[currentWave].endFight) {
//                        print(MathUtils.round(transitionAlpha));
                        transitionAlpha+=Gdx.graphics.getDeltaTime()/2f;
                        transition.setAlpha(transitionAlpha);
                        transition.draw(batch);
                        if(transitionAlpha>0.95) {
                            drawingDialogue = true;
                            drawingText = true;
                            fight = false;
                            drawTextTime = 10f;
                            transitionAlpha=1;
                        }
                    }
                }
                if(drawingDialogue){
                    currentLine = gameStory.get(currentLevel).get(storyLineIndex);
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

                    if(currentLine.renderWho!=null)initializeWho(currentLine);

                    drawText(batch,currentLine);

                    if(transitionAlpha>0){
                        transitionAlpha-=Gdx.graphics.getDeltaTime()/2f;
                        transition.setAlpha(transitionAlpha);
                        transition.draw(batch);
                    }
                }
            }break;

            case Load:{
                batch.draw(bgTexture, 0, 0, 1280, 720);
                titleFont.draw(batch,"SELECT SAVE",1280/2f-280,600);
                for(LoadButton btn : loadButtonArray){
                    btn.render(batch,timeElapsed);
                }
            }break;
            case Over:{
                batch.draw(bgTexture, 0, 0, 1280, 720);
                titleFont.draw(batch,"GAME OVER",1280/2f-220,600);
                for(OverButton btn : overButtonList){
                    btn.render(batch,timeElapsed);
                }
            }break;
        }

        if (controllerConnected && controllerConectTime < 3f) {
            batch.draw(gamepadConnect, 1100, 620);
            controllerConectTime += Gdx.graphics.getDeltaTime();
        }

        if (gameSaved && gameSavedTime < 3f) {
            batch.draw(gameSave, 950, 570);
            gameSavedTime += Gdx.graphics.getDeltaTime();
            if(gameSavedTime>3f){
                gameSaved=false;
            }
        }

        batch.end();

//
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//
//        for(EnemyClass enemy : enemyList){
//            shapeRenderer.setColor(Color.GREEN);
//            shapeRenderer.circle(enemy.bounds.x,enemy.bounds.y,enemy.bounds.radius);
//            shapeRenderer.setColor(Color.BLUE);
//            shapeRenderer.rect(enemy.object.getX(),enemy.object.getY(),enemy.object.getWidth(),enemy.object.getHeight());
//        }
//        shapeRenderer.rect(choiceABounds.x,choiceABounds.y,choiceABounds.width,choiceABounds.height);
//        shapeRenderer.rect(choiceBBounds.x,choiceBBounds.y,choiceBBounds.width,choiceBBounds.height);

//		for(MenuButton button : menuButtonArray){
//			shapeRenderer.setColor(button.active?Color.BLUE:Color.GREEN);
//			Rectangle bounds = button.bounds;
//			shapeRenderer.rect(bounds.x,bounds.y,bounds.width,bounds.height);
//		}
//        shapeRenderer.end();

    }

    private void initializeWho(StoryLine currentLine) {
        switch(currentLine.renderWho){
            case BG :{
                gameBG=new Texture(files(currentLine.renderAddress));
            }break;
            case Left:{
                leftChar=new Sprite(new Texture(files(currentLine.renderAddress)));
            }break;
            case Right :{
                rightChar=new Sprite(new Texture(files(currentLine.renderAddress)));
            }break;
        }
        leftChar.setSize(leftChar.getWidth(), leftChar.getHeight());
        leftChar.setPosition(0,-20);

        rightChar.setSize(rightChar.getWidth(), rightChar.getHeight());
        rightChar.setPosition(1280-rightChar.getWidth(),-20);

        rightChar.setFlip(true,false);

    }

    @Override
    public void dispose() {
        for(Projectile pro:projectileList)pro.dispose();
        for(MenuButton btn : menuButtonArray)btn.dispose();
        for(OverButton btn : overButtonList)btn.dispose();
        for(ExplosionEffect eff:explosionList)eff.dispose();
        for(PauseButton btn : pauseButtons)btn.dispose();
        for(HealthBar bar : healthBars)bar.dispose();
        for(GameButton btn : gameButtonList)btn.dispose();
        for(LoadButton btn : loadButtonArray)btn.dispose();
        for(EnemyWave[] enemyWave : enemyWaves)
            for(EnemyWave enemy : enemyWave)
                enemy.dispose();

        for(EnemyClass enem : enemyList) enem.dispose();
        for(MobileButton btn : mobileButtonList)btn.dispose();

        transition.getTexture().dispose();
        for(TextureRegion tex : loadButtonSheet)tex.getTexture().dispose();
        for(TextureRegion tex : mobileButtonSheet)tex.getTexture().dispose();
        for(TextureRegion tex : gameButtonSheet)tex.getTexture().dispose();
        for(TextureRegion tex : Kid.texture)tex.getTexture().dispose();
        for(TextureRegion tex : Dog.texture)tex.getTexture().dispose();
        for(TextureRegion tex : Doctor.texture)tex.getTexture().dispose();

        playerSheet.getTexture().dispose();
        background.dispose();
        dialogueFont.dispose();
        choiceFont.dispose();
        menuBG.dispose();
        cursorTexture.dispose();
        gamepadConnect.dispose();
        gameSave.dispose();
        htp.dispose();
        arena.dispose();
        bgTexture.dispose();
        gameBG.dispose();
        titleFont.dispose();
        player.getTexture().dispose();
        batch.dispose();
        leftChar.getTexture().dispose();
        rightChar.getTexture().dispose();
    }




    public static void setChoiceDepth(){
        if(choice==choiceState.A){
            gameStory.get(currentLevel).get(storyLineIndex).choiceA.depth=calculateDepth(gameStory.get(currentLevel).get(storyLineIndex).choiceA);
        }else{
            gameStory.get(currentLevel).get(storyLineIndex).choiceB.depth=calculateDepth(gameStory.get(currentLevel).get(storyLineIndex).choiceB);
        }
        lineDepth=0;
    }

    public static void initializeImages(){
//        print("hello");
        switch (currentLevel){
            case 0:{
                gameBG=new Texture(files("empty.png"));
                leftChar=new Sprite(new Texture(files("empty.png")));
                rightChar=new Sprite(new Texture(files("empty.png")));
            }break;
            case 1:{
                gameBG=new Texture(files("dog-bg.jpeg"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("empty.png")));
            }break;
            case 2:{
                gameBG=new Texture(files("ground.jpeg"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("empty.png")));
            }break;
            case 3:{
                gameBG=new Texture(files("empty.jpeg"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("empty.png")));
            }break;
            case 4:{
                gameBG=new Texture(files("city.png"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("scammer-stand.png")));
            }break;
            case 5:{
                gameBG=new Texture(files("campaign.jpeg"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("politician-stand.png")));
            }break;
            case 6:{
                gameBG=new Texture(files("talkshow.jpeg"));
                leftChar=new Sprite(new Texture(files("player-stand.png")));
                rightChar=new Sprite(new Texture(files("bot-stand.png")));
            }break;
        }

        leftChar.setSize(leftChar.getWidth(), leftChar.getHeight());
        leftChar.setPosition(0,-20);

        rightChar.setSize(rightChar.getWidth(), rightChar.getHeight());
        rightChar.setPosition(1280-rightChar.getWidth(),-20);

        rightChar.setFlip(true,false);

    }

    public static class ArenaBounds{
        Rectangle bounds;
        String name;
        public ArenaBounds(String name){
            this.name=name;
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
    public static class Projectile{
        public Sprite obj;
        public float x,y,rotation,amplitude=5,radians,time=0;
        public Projectile(float x,float y,float rotation){
            this.obj=new Sprite();
            this.obj.setPosition(x,y);
            this.x=x;
            this.y=y;
            this.rotation=rotation;
            this.obj.setRotation(rotation);
            obj.setRegion(new TextureRegion(new Texture(files("fire.png"))));
            obj.setSize(obj.getRegionWidth(), obj.getRegionHeight());
            obj.setOrigin(obj.getWidth() / 2, obj.getHeight() / 2);
            this.radians=MathUtils.degreesToRadians*(rotation+90);
        }
        public Vector2 getPoint(){
            return new Vector2(obj.getX(),obj.getY());
        }
        public void render(SpriteBatch sb){
            time+=Gdx.graphics.getDeltaTime();
            if(time>0.05f){
                time=0;
                obj.scale(0.025f);
            }
            obj.translate(amplitude* MathUtils.cos(radians),amplitude* MathUtils.sin(radians));
            obj.draw(sb);
        }
        public void dispose(){
            obj.getTexture().dispose();
        }
    }

    public static class EnemyWave{
        public EnemyClass[] enemies;
        public boolean endFight;
        public EnemyWave(EnemyClass[] enemies,Boolean endFight){
            this.enemies=enemies;
            this.endFight=endFight;
        }
        public void dispose(){
            for(EnemyClass enemy : enemies){
                if(enemy.object.getTexture()!=null)enemy.object.getTexture().dispose();
                if(enemy.target!=null)enemy.target.getTexture().dispose();
            }
        }
    }
    public static class StoryLine {
        public String message;
        public Boolean fightState=false;
        public int depth=0;
        public StoryLine choiceA=null,choiceB=null;
        public RenderWhat renderWho=null;
        public String renderAddress=null;

        enum choiceState{
            Nill,
            A,
            B,
        }
        choiceState choice=null;
        public StoryLine(String message,Boolean fightState) {
            this.message = message.toUpperCase();
            this.fightState=fightState;
        }
        public StoryLine(String message, StoryLine choiceA) {
            this.message = message.toUpperCase();
            this.choiceA = choiceA;
//            print(depth);
        }

        public StoryLine(String message, StoryLine choiceA,RenderWhat renderWho,String input) {
            this.message = message.toUpperCase();
            this.choiceA = choiceA;
            this.renderWho=renderWho;
            this.renderAddress=input;
//            print(depth);
        }

        public StoryLine(String message, StoryLine choiceA, StoryLine choiceB) {
            this.message = message.toUpperCase();
            this.choiceA = choiceA;
            this.choiceB = choiceB;
            choice=choiceState.Nill;
        }

    }



    public static class HealthBar{
        public Sprite bar;
        public int index;
        public float width,scaleFactor=3f;
        public HealthBar(float x,float y,TextureRegion[] sheet,int index){
            bar=new Sprite(sheet[index]);
            this.index=index;
            bar.setPosition(x,y);
            bar.setSize(bar.getWidth()*scaleFactor,bar.getHeight()*scaleFactor);
            width=bar.getWidth();
        }
        public void render(SpriteBatch sb){
            if(index==1)bar.setSize(health*width/10f,bar.getHeight());
            bar.draw(sb);
        }
        public void dispose(){
            bar.getTexture().dispose();
        }
    }

    public static class ExplosionEffect{
        private final ParticleEffect effects;
        public Boolean die=false;
        public ExplosionEffect(float x, float y,float scale){
            effects = new ParticleEffect();
            effects.load(files("explosionFX.p"),files(""));
            effects.setPosition(x, y);
            effects.scaleEffect(scale);
            effects.start();
        }
        public void render(SpriteBatch sb){
            effects.update(Gdx.graphics.getDeltaTime());
            effects.draw(sb,Gdx.graphics.getDeltaTime());
            if(effects.isComplete())die=true;
        }
        public void dispose(){
            effects.dispose();
        }

    }
    public static class EnemyType{
        public TextureRegion[] texture;
        public Array<EnemyAnimation[]> animations;
        public String name;
        public EnemyType(TextureRegion[] spriteSheet,Array<EnemyAnimation[]> animations,String name){
            this.name=name;
            print(animations.size);
            this.texture=spriteSheet;
            this.animations=animations;
        }


    }

    public static class EnemyAction{
        public Array<EnemyActionType> type;
        public EnemyAction(Array<EnemyActionType> type){
            this.type=type;
        }
    }

    public static class EnemyAnimation{
        public Array<EnemyActionType> type=new Array<>();
        public float duration,parameter=2;
        public Boolean clear=false;
        public EnemyAnimation(EnemyActionType type,float duration,float parameter){
            this.duration=duration;
            this.parameter=parameter;
            this.type.add(type);
        }
        public EnemyAnimation(Array<EnemyActionType> type,float duration,float parameter){
            this.duration=duration;
            this.parameter=parameter;
            this.type=type;
        }
        public EnemyAnimation(Array<EnemyActionType> type,float duration){
            this.duration=duration;
            this.type=type;
        }

        public EnemyAnimation(EnemyActionType type,float duration){
            this.duration=duration;
            this.type.add(type);
        }
        public void update(){
            duration-=Gdx.graphics.getDeltaTime();
            if(duration<0){
                clear=true;
            }
        }
    }

    public static class EnemyClass{
        private float alpha=0;
        private float deltaX,initialVelocityY=0;
        private float deltaY;
        private float scaleFactor;
        public float time=0,parameter=0,delay=-1;
        public int animationIndex;
        public String name;
        public Sprite object,target=null;
        public Circle bounds;
        private final Vector2 position;
        private final Vector2 velocity;
        private Vector2 gravity=new Vector2(0,0);
        public Array<EnemyAnimation> animationList= new Array<>();
        public Boolean attackMode=true,update=true;
        private float health=3,targetAlpha=0,objectScale=0;
        public EnemyClass(EnemyType type,int animationIndex ,int index,Boolean inside,float rotation,float scaleFactor){
            object=new Sprite(type.texture[index]);
            object.setRotation(rotation);
            this.animationList.addAll(type.animations.get(animationIndex));
            object.setSize(object.getWidth()*scaleFactor,object.getHeight()*scaleFactor);
            object.setOriginCenter();
            this.name=type.name;
            this.animationIndex=animationIndex;
            this.scaleFactor=scaleFactor;
            this.bounds=new Circle(object.getX()+ object.getWidth()/2f,object.getY()+object.getHeight()/2f,object.getRegionWidth()*scaleFactor/2f);
            int decideSide=MathUtils.random(0,3);
            if(inside){
                switch(decideSide){
                    case 0:{
                        object.setPosition(320,MathUtils.random(120,600-object.getHeight()));
                    }break;
                    case 1:{
                        object.setPosition(960-object.getWidth(),MathUtils.random(120,600-object.getHeight()));
                    }break;
                    case 2:{
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),600-object.getHeight());
                    }break;
                    case 3:{
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),120);
                    }break;
                }
            }else{
                switch(decideSide){
                    case 0:{
                        object.setPosition(320-object.getWidth(),MathUtils.random(120,600-object.getHeight()));
                    }break;
                    case 1:{
                        object.setPosition(960,MathUtils.random(120,600-object.getHeight()));
                    }break;
                    case 2:{
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),600);
                    }break;
                    case 3:{
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),120-object.getHeight());
                    }break;
                }
            }

            switch (type.name){
                case "dog":{
                    if(animationIndex==0){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320+object.getWidth(),960-object.getWidth()),600);
                        delay=MathUtils.random(5,30);
                        object.scale(MathUtils.random(0f,1.5f));
                        gravity = new Vector2(0, -9.8f*MathUtils.random(0.0f,3f));
                    }
                    if(animationIndex==2){
                        attackMode=false;
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320+object.getWidth(),960-object.getWidth()),MathUtils.random(120,600-object.getHeight()));
                        target=new Sprite(type.texture[3]);
                        objectScale=scaleFactor+MathUtils.random(0.5f,2.0f);
                        target.setSize(object.getWidth()/scaleFactor,object.getHeight()/scaleFactor);
                        target.setPosition(object.getX()+16,object.getY()+16);
                        target.setAlpha(0);
                        alpha=0;
                        this.scaleFactor=(int)scaleFactor;
                        delay=MathUtils.random(5,30);
                    }
                }break;
                case "kid":{
                    if(animationIndex==0){
//                        delay=MathUtils.random(3,10);
                        object.setAlpha(0f);
                        delay=1f;
//                        print(animationList.peek().type+"");
                    }
                    if(animationIndex==1){
                        object.setAlpha(0f);
//                        delay=MathUtils.random(7,20);
                    }
                    if(animationIndex==2){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320-object.getWidth()*MathUtils.random(1,3),320-object.getWidth()),MathUtils.random(120,180));
                        delay=MathUtils.random(3,30);
                        gravity = new Vector2(0, -9.8f);
                        initialVelocityY=MathUtils.random(0f,8.0f);
                    }
                    if(animationIndex==3){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(960+object.getWidth()*MathUtils.random(1,3),960+object.getWidth()),MathUtils.random(120,180));
                        delay=MathUtils.random(3,40);
                        gravity = new Vector2(0, -9.8f);
                        initialVelocityY=MathUtils.random(2f,8.0f);
                    }
                }break;

                case "doctor":{
                    if(animationIndex==0){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),120-object.getHeight());
                        delay=MathUtils.random(3,30);
                        gravity = new Vector2(0, -9.8f);
                        initialVelocityY=MathUtils.random(2f,6.0f);
                    }
                    if(animationIndex==1){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320,960-object.getWidth()),600);
                        delay=MathUtils.random(3,30);
                        gravity = new Vector2(0, 9.8f);
                        initialVelocityY=MathUtils.random(2f,6.0f);
                    }
                    if(animationIndex==4){
                        update=false;
                        object.setAlpha(0f);
                        object.setPosition(MathUtils.random(320+object.getWidth(),960-object.getWidth()),600);
                        delay=MathUtils.random(5,30);
                        object.scale(MathUtils.random(0f,1.2f));
                        gravity = new Vector2(0, -9.8f*MathUtils.random(2.0f,9.0f));
                    }
                }break;

            }
            position = new Vector2(object.getX(), object.getY());
            velocity = new Vector2(0, 0);

//            object.setPosition(500,400);
//            print(object.getX()+" : "+object.getY());
        }


        public void render(SpriteBatch batch) {
            time+=Gdx.graphics.getDeltaTime();

            if(animationList.notEmpty()){

                if(alpha!=1f&&delay<0){
                    alpha+=Gdx.graphics.getDeltaTime();
                    if(MathUtils.floor(alpha)==1)alpha=1f;
                    object.setAlpha(alpha);
                }

                if(update&&delay<=0){
                    animationList.peek().update();
                }

                delay-=Gdx.graphics.getDeltaTime();

                if(delay<=0)for(EnemyActionType type :  animationList.peek().type){
//                    print(type.name());
                    parameter=animationList.peek().parameter;
                    switch(type){
                        case FaceCenter:{
                            faceCenter();
                        }break;
                        case Target:{
                            target();
                        }break;
                        case Fall:{
                            fall();
                        }break;
                        case Look :{
                            facePlayer();
                        }break;
                        case Move:{
                            move(parameter);
                        }break;
                        case ThrowHor: {
                            throwHorizontal(parameter);
                        }break;
                        case ThrowVer:{
                            throwVertical(parameter);
                        }break;
                        case Around:{
                            stayAroundPlayer(parameter);
                        }break;
                        case Turn:{
                            rotate();
                        }break;
                        case Rotate:{
                            rotate(parameter);
                        }break;
                        case PointAway:{
                            pointAway(parameter);
                        }break;
                        case Attack:{
                            attack(parameter);
                        }break;
                        case Bounce:{
                            rebound(parameter);
                        }break;
                    }
                }

                if(animationList.peek().duration<0){
                    animationList.pop();
                }
            }else{
                health=0;
            }
            bounds=new Circle(object.getX()+ object.getWidth()/2f,object.getY()+object.getHeight()/2f,object.getRegionWidth()*scaleFactor/2f);
            object.draw(batch);
            if(target!=null)target.draw(batch);
        }

        public Boolean getBounds(Vector2 point){
            return bounds.contains(point);
        }

        //methods
        public void pointAway(float parameter){
            float playerCenterX = player.getX() + player.getWidth() / 2;
            float playerCenterY = player.getY() + player.getHeight() / 2;
            float objectCenterX = object.getX() + object.getWidth() / 2;
            float objectCenterY = object.getY() + object.getHeight() / 2;
            deltaX = playerCenterX - objectCenterX;
            deltaY = playerCenterY - objectCenterY;
            object.setRotation((float) Math.toDegrees((float) Math.atan2(deltaY, deltaX)));
            object.rotate(MathUtils.random(-parameter,parameter));
        }
        public void target(){
            if(delay<0 && delay>-4 && targetAlpha!=1){
                targetAlpha+=Gdx.graphics.getDeltaTime()/4f;
                if(MathUtils.round(targetAlpha)==1)targetAlpha=1;
                target.setAlpha(targetAlpha);
            }
            if(delay<-4){
                target.setAlpha(targetAlpha);
                if(targetAlpha>0.05f)targetAlpha-=Gdx.graphics.getDeltaTime();
                if(!attackMode)attackMode=true;
                objectScale-=Gdx.graphics.getDeltaTime();
                object.setScale(objectScale);
                if(alpha<0.98) {
                    alpha += Gdx.graphics.getDeltaTime();
                    object.setAlpha(alpha);
                }
                if(object.getScaleX()<target.getScaleX())health=-1;
            }
        }

        public void faceCenter(){


        }
        public void facePlayer(){
            float playerCenterX = player.getX() + player.getWidth() / 2;
            float playerCenterY = player.getY() + player.getHeight() / 2;
            float objectCenterX = object.getX() + object.getWidth() / 2;
            float objectCenterY = object.getY() + object.getHeight() / 2;
            deltaX = playerCenterX - objectCenterX;
            deltaY = playerCenterY - objectCenterY;
            object.setRotation((float) Math.toDegrees((float) Math.atan2(deltaY, deltaX)));
        }

        public void fall (){
            velocity.add(gravity.x * Gdx.graphics.getDeltaTime(), gravity.y * Gdx.graphics.getDeltaTime());
            position.add(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime());
            object.setPosition(position.x, position.y);
            if(object.getY()<90)health=-1;
        }

        public  void stayAroundPlayer(float distance){
            deltaX = (player.getX() + player.getWidth()/2)-(object.getWidth()/2f)+distance*(float) Math.cos(MathUtils.degreesToRadians*(object.getRotation()));
            deltaY = (player.getY() + player.getHeight()/2)-(object.getHeight()/2f)+distance*(float) Math.sin(MathUtils.degreesToRadians*(object.getRotation()));
            object.setPosition(deltaX, deltaY);
        }
        public void move(float speed){
//            print("moving"+delay);
            object.setPosition(object.getX() + speed * MathUtils.cosDeg(object.getRotation()), object.getY() + speed * MathUtils.sinDeg(object.getRotation()));
        }

        public void rotate(float amplitude){
            object.rotate(amplitude);
        }
        public void rotate(){
            object.rotate(1.2f);
        }
        public void attack(float parameter){
            velocity.add(new Vector2(player.getX(), player.getY()).cpy().sub(position).nor().scl(parameter * Gdx.graphics.getDeltaTime()));
            velocity.clamp(0, 500f);
            position.add(velocity.cpy().scl(Gdx.graphics.getDeltaTime()));
            object.setPosition(position.x, position.y);
        }


        public void rebound(float parameter){
            move(parameter);
            for(ArenaBounds bounds : arenaBounds){
                if(object.getBoundingRectangle().overlaps(bounds.getBounds())){
                    float randomAngle=MathUtils.random(-30,30);
                    switch(bounds.name){
                        case "left":{
                            object.setRotation(180 - object.getRotation()+randomAngle);
                            object.setX(320 + object.getWidth()/2f + 3f);
                        } break;
                        case "right":{
                            object.setRotation(180 - object.getRotation()+randomAngle);
                            object.setX(960 - object.getWidth() -3f);
                        } break;
                        case "up":{
                            object.setRotation(-object.getRotation()+randomAngle);
                            object.setY(600 - object.getHeight()-3f);
                        } break;
                        case "down":{
                            object.setRotation(-object.getRotation()+randomAngle);
                            object.setY(120 + object.getHeight()/2f + 3f);
                        } break;
                    }
                    move(parameter);
                }
            }
        }
        public void waveHorizontal(){
            object.setPosition(time * 100, Gdx.graphics.getHeight() / 2f + 50 * (float)Math.sin(time * 2));
        }
        public void waveVertical(){
            object.setPosition( Gdx.graphics.getWidth() / 2f + 50 * (float)Math.sin(time * 2),time * 100);
        }
        public void tanHorizontal(){
            object.setPosition(time * 100, Gdx.graphics.getHeight() / 2f + 50 * (float)Math.tan(time * 2));
        }
        public void tanVertical(){
            object.setPosition(time * 100, Gdx.graphics.getWidth() / 2f + 50 * (float)Math.tan(time * 2));
        }
        public void throwHorizontal(float parameter){
            object.setPosition((parameter==1?320:960)+250*parameter * -delay, 200 + initialVelocityY*50f * -delay + 0.5f * gravity.y*15f * -delay * -delay);
            if(object.getY()<50)health=-1;
            object.rotate(Math.max(1f, 10f - object.getY()/ 20f));
        }
        public void throwVertical(float parameter){
            object.setY((parameter==1?120-object.getHeight():600+object.getHeight()) + initialVelocityY*50f *parameter* -delay + 0.5f * gravity.y*7f * -delay * -delay);
            if(parameter==1?object.getY()<50:object.getY()>740)health=-1;
            object.rotate(Math.max(1f, (parameter==1?(10 - object.getY() / 20f):(1/(10 - object.getY() / 20f)))));
        }

        public void dispose(){
            if(object.getTexture()!=null)object.getTexture().dispose();
            if(target!=null)target.getTexture().dispose();
        }
    }


    public static class GameButton{
        public float y=720-100f,x;
        public int index;
        private final Sprite object;
        public GameButton(float x, int index, TextureRegion[] buttonSheet){
            this.x=x;
            this.index=index;
            this.object= new Sprite(buttonSheet[index]);
            object.setOriginCenter();
            object.setPosition(x,y);
            object.setScale(1.35f);
        }
        public void dispose(){
            object.getTexture().dispose();
        }
        public void render(SpriteBatch batch) {
            object.draw(batch);
        }
    }

    public static class OverButton{
        public final String name;
        public int index;
        public Rectangle bounds;
        private final float x,y;
        private float alpha;
        private final BitmapFont font;

        public OverButton(float y, String name, int index) {
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
            if (index == overButtonActiveIndex) {
                alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 3 * Math.PI));
            }
            font.setColor(1, 1, 1, index == overButtonActiveIndex ? alpha : 1);
        }
        public void dispose(){
            font.dispose();
        }
        public Boolean isTouching(Vector2 touch) {
            return bounds.contains(touch);
        }
    }


    public static class LoadButton{
        public float y=720/3f,x,progress;
        public int index;
        private BitmapFont font;
        private Sprite object;
        public int level;
        public int kills;
        public float score;
        public LoadButton(float x, int index, TextureRegion[] buttonSheet,Preferences save){
            this.x=x;
            this.level=save.getInteger("level",0);
            this.kills=save.getInteger("klls",0);
            this.score=save.getFloat("score",0);
            this.progress=level*100/6f;
            this.index=index;
            this.font=new BitmapFont(files("joystix.fnt"));
            this.object= new Sprite(buttonSheet[(level==0)?0:1]);
            object.setScale(6f);
            object.setOriginCenter();
            object.setPosition(x,y);

        }
        public void render(SpriteBatch batch, float timeElapsed) {
            float alpha=1;
            object.draw(batch);
            font.draw(batch,(int) progress+"%",x,y-30);
            if (index == loadButtonIndex)alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 1.5 * Math.PI));
            font.setColor(1,1,1,(index==loadButtonIndex)?alpha:1);
            object.setAlpha(index == loadButtonIndex ? alpha : 1);
        }
        public void dispose(){
            font.dispose();
            object.getTexture().dispose();
        }
    }

    public static class MobileButton{
        public Boolean active=false;
        public String name;
        public int index;
        public float x,y;
        public Sprite button;
        public MobileButton(String name,TextureRegion[] texture,int index){
            this.button=new Sprite(texture[Math.min(index, 3)]);
            this.index=index;
            switch(name){
                case "look":{
                    this.button.setPosition(120,120);
                    this.x=120;
                    this.y=120;
                }
                case "icon":{
                    this.button.setPosition(120,120);
                    this.x=120;
                    this.y=120;
                }break;
                case "fire":{
                    this.button.setPosition(1280-160,720-140);
                    this.x=1280-160;
                    this.y=720-140;
                }break;
                case "forward":{
                    this.button.setPosition(1280-160,230);
                    this.x=1280-160;
                    this.y=230;
                }break;
                case "backward":{
                    this.button.setRegion(texture[3]);
                    this.button.setPosition(1280-160,50);
                    this.button.flip(false,true);
                    this.x=1280-160;
                    this.y=50;
                }break;
            }
            this.button.setOriginCenter();
            this.button.setScale(4f);
            this.name=name;
        }
        public void dispose(){
            button.getTexture().dispose();
        }
        public void render(SpriteBatch batch){
            button.draw(batch);
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
        public void dispose(){
            font.dispose();
        }

        public Boolean isTouching(Vector2 touch) {
            return bounds.contains(touch);
        }
    }

    public static class PauseButton {
        public final String name;
        public int index;
        public Rectangle bounds;
        private final float x,y;
        private float alpha;
        private final BitmapFont font;

        public PauseButton(float y, String name, int index) {
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
            if (index == pauseButtonActiveIndex) {
                alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 3 * Math.PI));
            }
            font.setColor(1, 1, 1, index == pauseButtonActiveIndex ? alpha : 1);
        }
        public void dispose(){
            font.dispose();
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
            switch(gameState){
                case Play :{
                    if(fight){
                        if(buttonCode==0){
                            fireKey=true;
                            playerTime=0;
                            fireProjectile=true;
                        }
                        if(buttonCode==11){
                            playerForward=false;
                            playerBackward=true;
                        }
                        if(buttonCode==12){
                            playerForward=true;
                            playerBackward=false;
                        }
                        if(buttonCode==13){
                            playerTurnRight=true;
                            playerTurnLeft=false;
                        }
                        if(buttonCode==14){
                            playerTurnRight=false;
                            playerTurnLeft=true;
                        }
                    }
                }break;

            }
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
                        if (menuButtonActiveIndex < menuButtonNames.length-1) menuButtonActiveIndex++;
                        else controller.startVibration(300, 0.5f);
                    }
                    if (buttonCode == 0) {
//                        controller.startVibration(400, 0.5f);
                        handleMenu();
                    }

                    if (buttonCode == 6) gameState = GameState.Load;
                }break;

                case Pause:{
                    if (buttonCode == 11) {
                        if (pauseButtonActiveIndex > 0) pauseButtonActiveIndex--;
                        else controller.startVibration(300, 0.5f);
                    }
                    if (buttonCode == 12) {
                        if (pauseButtonActiveIndex < pauseButtonNames.length-1)
                            pauseButtonActiveIndex++;
                        else controller.startVibration(300, 0.5f);
                    }
                    if(buttonCode==0){
                        handlePause();
                    }
                    if(buttonCode==3||buttonCode==4){
                        gameState=GameState.Play;
                    }
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
                        saveIndex=loadButtonIndex;
                        loadGame(saveIndex);
                        currentWave=0;
                        drawingDialogue=true;
                        drawingText=true;
                        fight=false;
                        drawTextTime=10f;
                        storyLineIndex=0;
                        createStory();
                        initialize();
                        gameState=GameState.Play;
                        gameStarted=true;
                        currentLevel=1;
                        initializeLevel(1,0);
                    }

                }break;
                case Instructions:{
                    if(buttonCode==1){
                        gameState=gameStarted?GameState.Pause:GameState.Menu;
//                        controller.startVibration(200, 0.7f);
                    }
                }break;
                case Over:{
                    if (buttonCode == 11) {
                        if (overButtonActiveIndex > 0) overButtonActiveIndex--;
                        else controller.startVibration(300, 0.5f);
                    }
                    if (buttonCode == 12) {
                        if (overButtonActiveIndex < overButtonNames.length-1)
                            overButtonActiveIndex++;
                        else controller.startVibration(300, 0.5f);
                    }
                    if(buttonCode==0){
                        handleOver();
                    }
                }break;
                case Play:{

                    if((buttonCode==3||buttonCode==4)){
                        gameState=GameState.Pause;
                    }
                    if(fight){
                        if(buttonCode==0){
                            fireKey=false;
                        }
                        if(buttonCode==11){
                            playerForward=false;
                            playerBackward=false;
                        }
                        if(buttonCode==12){
                            playerForward=false;
                            playerBackward=false;
                        }
                        if(buttonCode==13){
                            playerTurnRight=false;
                            playerTurnLeft=false;
                        }
                        if(buttonCode==14){
                            playerTurnRight=false;
                            playerTurnLeft=false;
                        }
                    }
                    if(choiceMode){
                        if(buttonCode==13){
                            choice=choiceState.A;
                        }
                        if(buttonCode==14){
                            choice=choiceState.B;
                        }
                        if(buttonCode==0){
                            gameStory.get(currentLevel).get(storyLineIndex).choice = choice==choiceState.A?StoryLine.choiceState.A:StoryLine.choiceState.B;
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
                            if (menuButtonActiveIndex < menuButtonNames.length-1) menuButtonActiveIndex++;
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
                case Over:{
                    if (axisCode == 1) {
                        if ((MathUtils.floor(value) == -1)) {
                            if (overButtonActiveIndex > 0) overButtonActiveIndex--;
                            else controller.startVibration(300, 0.5f);
                        }
                        if ((MathUtils.floor(value) == 1)) {
                            if (overButtonActiveIndex < overButtonNames.length-1) overButtonActiveIndex++;
                            else controller.startVibration(300, 0.5f);
                        }
                    }
                }break;
                case Pause:{
                    if (axisCode == 1) {
                        if ((MathUtils.floor(value) == -1)) {
                            if (pauseButtonActiveIndex > 0) pauseButtonActiveIndex--;
                            else controller.startVibration(300, 0.5f);
                        }
                        if ((MathUtils.floor(value) == 1)) {
                            if (pauseButtonActiveIndex < pauseButtonNames.length-1) pauseButtonActiveIndex++;
                            else controller.startVibration(300, 0.5f);
                        }
                    }
                }break;
                case Play:{
                    if(fight){
//                        print(MathUtils.floor(value));
                        if(axisCode==5){

                            fireKey=(MathUtils.floor(value) >0.5);
                            if(fireKey){
                                playerTime=0;
                                fireProjectile=true;
                            }
                        }
                        if(axisCode==0){
                            if (MathUtils.floor(value) < -0.5) {
                                playerTurnLeft = false;
                                playerTurnRight = true;
                            }
                            if (MathUtils.floor(value) > 0.5) {
                                playerTurnRight = false;
                                playerTurnLeft = true;
                            }
                            if (MathUtils.floor(value) == 0) {
                                playerTurnRight = false;
                                playerTurnLeft = false;
                            }
                        }
                        if(axisCode==3){
                            if (MathUtils.floor(value) < -0.5) {
                                playerBackward=true;
                                playerForward=false;
                            }
                            if (MathUtils.floor(value) >0.5) {
                                playerBackward=false;
                                playerForward=true;
                            }
                            if (MathUtils.floor(value) == 0) {
                                playerBackward=false;
                                playerForward=false;
                            }
                        }
                    }
                    if(axisCode==0&&drawingDialogue){
                        if ((MathUtils.floor(value) <-0.5)) {
                            choice=choiceState.A;
                        }
                        if ((MathUtils.floor(value) > 0.5)) {
                            choice=choiceState.B;
                        }
                    }
                }
            }
//            Gdx.app.log("Controller", "axis moved: " + axisCode + " value: " + value);
            return false;
        }
    }




    public static class InputProcessor extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            switch (gameState){
                case Play:{
                    if(fight){
                        if((keycode==Input.Keys.A||keycode==Input.Keys.LEFT) ){
                            playerTurnRight=true;
                        }
                        if((keycode==Input.Keys.D||keycode==Input.Keys.RIGHT)){
                            playerTurnLeft=true;
                        }
                        if((keycode==Input.Keys.W||keycode==Input.Keys.UP)){
                            playerBackward=true;
                        }
                        if((keycode==Input.Keys.S||keycode==Input.Keys.DOWN)){
                            playerForward=true;
                        }
                        if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                            fireKey=true;
                            playerTime=0;
                            fireProjectile=true;
                        }
                    }
                }break;
            }
            return true;
        }


        @Override
        public boolean keyUp(int keycode) {
            switch (gameState) {
                case Menu: {
                    if ((keycode == Input.Keys.UP||keycode == Input.Keys.W) && menuButtonActiveIndex > 0) {
                        menuButtonActiveIndex--;
                    }
                    if ((keycode == Input.Keys.DOWN||keycode == Input.Keys.W) && menuButtonActiveIndex < menuButtonNames.length-1) {
                        menuButtonActiveIndex++;
                    }
                    if (keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE) {
                        handleMenu();
                    }
                }break;

                case Pause:{
                    if ((pauseButtonActiveIndex > 0) && (keycode == Input.Keys.UP||keycode == Input.Keys.W)) pauseButtonActiveIndex--;
                    if ((pauseButtonActiveIndex < pauseButtonNames.length -1) &&(keycode == Input.Keys.DOWN||keycode == Input.Keys.W)) pauseButtonActiveIndex++;
                    if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                        handlePause();
                    }
                    if((keycode==Input.Keys.ESCAPE||keycode==Input.Keys.BACKSPACE)){
                        gameState=GameState.Play;
                    }
                }break;
                case Over:{
                    if ((overButtonActiveIndex > 0) && (keycode == Input.Keys.UP||keycode == Input.Keys.W)) overButtonActiveIndex--;
                    if ((overButtonActiveIndex < overButtonNames.length -1) &&(keycode == Input.Keys.DOWN||keycode == Input.Keys.W)) overButtonActiveIndex++;
                    if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                        handleOver();
                    }
                    if((keycode==Input.Keys.ESCAPE||keycode==Input.Keys.BACKSPACE)){
                        gameState=GameState.Menu;
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
                        saveIndex=loadButtonIndex;
                        loadGame(saveIndex);
                        gameStarted=true;

                        drawingDialogue=true;
                        drawingText=true;
                        fight=false;
                        storyLineIndex=0;
                        createStory();
                        initialize();
                        gameState=GameState.Play;
                        drawTextTime=10f;
                    }
                    if((keycode==Input.Keys.ESCAPE||keycode==Input.Keys.BACKSPACE)){
                        gameState=GameState.Menu;
                    }
                }break;
                case Play:{
                    if((keycode==Input.Keys.ESCAPE||keycode==Input.Keys.BACKSPACE)){
                        gameState=GameState.Pause;
                    }
                    if(fight){
                        if((keycode==Input.Keys.A||keycode==Input.Keys.LEFT) ){
                            playerTurnRight=false;
                        }
                        if((keycode==Input.Keys.D||keycode==Input.Keys.RIGHT)){
                            playerTurnLeft=false;
                        }
                        if((keycode==Input.Keys.W||keycode==Input.Keys.UP)){
                            playerBackward=false;
                        }
                        if((keycode==Input.Keys.S||keycode==Input.Keys.DOWN)){
                            playerForward=false;
                        }
                        if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                            fireKey=false;
                        }
                    }
                    if (choiceMode) {
                        if((keycode==Input.Keys.A||keycode==Input.Keys.LEFT) ){
                            choice=choiceState.A;
                        }
                        if((keycode==Input.Keys.D||keycode==Input.Keys.RIGHT)){
                            choice=choiceState.B;
                        }
                        if(keycode == Input.Keys.ENTER||keycode==Input.Keys.Z || keycode == Input.Keys.SPACE){
                            gameStory.get(currentLevel).get(storyLineIndex).choice = choice==choiceState.A?StoryLine.choiceState.A:StoryLine.choiceState.B;
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
                        gameState=gameStarted?GameState.Pause:GameState.Menu;
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
                case Over:{
                    for(OverButton button : overButtonList){
                        if (button.isTouching(point)) overButtonActiveIndex = button.index;
                    }
                }break;
                case Pause:{
                    for(PauseButton button : pauseButtons){
                        if (button.isTouching(point)) pauseButtonActiveIndex = button.index;
                    }
                }break;
                case Play:{
                    if(fight){
                        if(mouseControlActive) {
                            mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                            camera.unproject(mousePos);
                            player.setRotation((float) (MathUtils.radiansToDegrees * Math.atan2(mousePos.y - player.getY() - player.getOriginY(), mousePos.x - player.getX() - player.getOriginX()) - 90));
                        }
                    }
                    if(choiceMode) {
                        if (choiceABounds.contains(point)) {
                            choice = choiceState.A;
                        }
                        if (choiceBBounds.contains(point)) {
                            choice = choiceState.B;
                        }
                    }
                }break;
            }


            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            touch = new Vector3(screenX, screenY, 0);
            camera.unproject(touch);
            point = new Vector2(touch.x, touch.y);
            switch(gameState){
                case Play :{
                    if(fight){
                        if(mouseControlActive){
                            if(button==Input.Buttons.LEFT) {
                                fireKey = true;
                                playerTime = 0;
                                fireProjectile = true;
                            }
                            if(button==Input.Buttons.RIGHT){
                                playerBackward=true;
                            }
                        }
                        if(!mouseControlActive){
                            for(MobileButton btn:mobileButtonList){
                                if(btn.button.getBoundingRectangle().contains(point)) {
                                    btn.active = btn.button.getBoundingRectangle().contains(point);
                                    switch (btn.name) {
                                        case "forward": {
                                            playerBackward = true;
                                        }
                                        break;
                                        case "backward": {
                                            playerForward = true;
                                        }
                                        break;
                                        case "fire": {
                                            fireKey = true;
                                            playerTime = 0;
                                            fireProjectile = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }break;
            }
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
                case Pause:{
                    for(PauseButton button : pauseButtons){
                        if (button.isTouching(point)) pauseButtonActiveIndex = button.index;
                    }
                }break;
                case Over:{
                    for(OverButton button : overButtonList){
                        if (button.isTouching(point)) overButtonActiveIndex = button.index;
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
                    if(fight){
                        if(mouseControlActive) {
                            mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                            camera.unproject(mousePos);
                            player.setRotation((float) (MathUtils.radiansToDegrees * Math.atan2(mousePos.y - player.getY() - player.getOriginY(), mousePos.x - player.getX() - player.getOriginX()) - 90));
                        }
                        if(!mouseControlActive){
                            for(MobileButton button : mobileButtonList){
                                if(button.button.getBoundingRectangle().contains(point)){

                                    if(Objects.equals(button.name, "icon") ){
                                        player.setRotation((float) (MathUtils.radiansToDegrees * Math.atan2(point.y - button.y - button.button.getOriginY(), point.x - button.x - button.button.getOriginX()) - 90));
                                        button.button.setPosition(point.x-button.button.getRegionWidth()/2f,point.y-button.button.getRegionHeight()/2f);
                                    }
                                }
                            }
                        }
                    }
                    if(choiceMode) {
                        if (choiceABounds.contains(point)) {
                            choice = choiceState.A;
                        }
                        if (choiceBBounds.contains(point)) {
                            choice = choiceState.B;
                        }
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
                case Pause:{
                    for (PauseButton btn : pauseButtons) {
                        if (btn.isTouching(point)) {
                            pauseButtonActiveIndex=btn.index;
                            handlePause();
                        }
                    }
                }break;
                case Over:{
                    for (OverButton btn : overButtonList) {
                        if (btn.isTouching(point)) {
                            overButtonActiveIndex=btn.index;
                            handleOver();
                        }
                    }
                }break;
                case Load:{
                    boolean exit=true;
                    for(LoadButton btn: loadButtonArray){
                        if(btn.object.getBoundingRectangle().contains(point)){
                            saveIndex=loadButtonIndex;
                            loadGame(saveIndex);
                            gameStarted=true;
                            drawingDialogue=true;
                            drawingText=true;
                            fight=false;
                            storyLineIndex=0;
                            createStory();
                            initialize();
                            gameState=GameState.Play;
                            drawTextTime=10f;
                            exit=false;
                        }
                    }
                    if(exit)gameState=GameState.Menu;
                }break;
                case Play:{

                    if(fight){
                        if(mouseControlActive) {
                            if (button == Input.Buttons.LEFT) {
                                fireKey = false;
                            }
                            if (button == Input.Buttons.RIGHT) {
                                playerBackward = false;
                            }
                        }
                        if(!mouseControlActive){
                            for(MobileButton btn:mobileButtonList){
                                btn.active = false;

                                switch (btn.name) {
                                    case "forward": {
                                        playerBackward = false;
                                    }
                                    break;
                                    case "backward": {
                                        playerForward = false;
                                    }
                                    break;
                                    case "fire": {
                                        fireKey = false;
                                    }
                                    break;
                                    case "icon": {

                                        btn.button.setPosition(btn.x,btn.y);
                                    }
                                }

                            }
                        }
                    }
                    for(GameButton btn : gameButtonList){
                        if(btn.object.getBoundingRectangle().contains(point)){
                            if(btn.index==0)gameState=GameState.Pause;
                            else {
                                mouseControlActive=!mouseControlActive;
                                fireKey=false;
                                fireProjectile=false;
                                playerTime=0;
                            }

                        }
                    }
                    if(choiceMode){
                        if(choiceABounds.contains(point)){
                            gameStory.get(currentLevel).get(storyLineIndex).choice= StoryLine.choiceState.A;
                            choiceMode=false;
                            setChoiceDepth();
                        }
                        if(choiceBBounds.contains(point)){
                            gameStory.get(currentLevel).get(storyLineIndex).choice= StoryLine.choiceState.B;
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
                    gameState=gameStarted?GameState.Pause:GameState.Menu;
                }break;
            }
            return true;
        }
    }

}