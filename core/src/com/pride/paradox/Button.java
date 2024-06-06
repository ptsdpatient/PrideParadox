package com.pride.paradox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Button {
    public static class MenuButton extends Button{
        private float y,x,alpha;
        private GlyphLayout layout;
        private Rectangle bounds;
        private Boolean active=false;
        private final BitmapFont font;
        private final String name;

        public MenuButton(float y, String name, BitmapFont font){
            layout = new GlyphLayout(font,name);
            this.x = (1280 - layout.width) / 2;
            this.y=y;
            this.font = font;
            this.name=name;
            this.bounds= new Rectangle(x,y,layout.width,layout.height);
        }
        public void render(SpriteBatch batch,float timeElapsed){
            font.draw(batch,name,this.x,this.y);
            if(active){
                alpha = (float) (0.5 + 0.5 * Math.sin(timeElapsed * 2 * Math.PI));
                font.setColor(1,1,1,alpha);
            }
        }
        public Boolean isTouching(Vector2 touch){
            return bounds.contains(touch);
        }
    }
}
