package com.tutorial.mario.entity.mob;

import java.awt.Graphics;
import java.util.Random;

import com.tutorial.mario.Game;
import com.tutorial.mario.Handler;
import com.tutorial.mario.Id;
import com.tutorial.mario.entity.Entity;
import com.tutorial.mario.tile.Tile;

public class Goomba extends Entity {
	private Random random = new Random();

	public Goomba(int x, int y, int width, int height,Id id, Handler handler) {
		super(x, y, width, height,id, handler);
		int dir = random.nextInt(2);
		
		switch(dir) {
		case 0 :
			setVelX(-1);
			break;
		case 1:
			setVelX(1);
			break;
		}
		
	}
	public void render (Graphics g) {
		g.drawImage(Game.goomba.getBufferedImage(), x, y, width, height,null );
		
	}
	public void tick() {
		x+=velX;
		y+=velY;
		for(int i=0; i<handler.tile.size();i++) {
			Tile t = handler.tile.get(i);
			if(t.isSolid()) {	
				if(getBoundsBottom().intersects(t.getBounds())) {
					setVelY(0);
					if(falling) falling = false;				
				}else if 
					(!falling) {
						falling = true;
						gravity = 0.8;
						
					}
				if(getBoundsLeft().intersects(t.getBounds())) {
					setVelX(3);
					x = t.getX() + t.height;
					
				}
				if(getBoundsRight().intersects(t.getBounds())) {
					setVelX(-3);
					x = t.getX() - t.height;
					
				}
			}
			
		}
		if(falling) {
			gravity +=0.1;
			setVelY((int) gravity);
		}
		if(velX!=0) {
			frameDelay++;
			if(frameDelay>=10) {
				frame++;
				if(frame>3) {
					frame = 0;
				}
				frameDelay = 0;
			}
		}
	}

}
