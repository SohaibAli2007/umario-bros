package com.tutorial.mario.entity.mob;

import java.awt.Color;
import java.awt.Graphics;

import com.tutorial.mario.Game;
import com.tutorial.mario.Handler;
import com.tutorial.mario.Id;
import com.tutorial.mario.entity.Entity;
import com.tutorial.mario.states.BossState;
import com.tutorial.mario.states.PlayerState;
import com.tutorial.mario.tile.Tile;


public class Player extends Entity{
	
	private PlayerState state;
	
	private int pixelsTravelled = 0;
	
	public boolean bossKilled = false;

	public Player(int x, int y, int width, int height, Id id, Handler handler) {
		super(x, y, width, height, id, handler);
		
		state = PlayerState.SMALL;

	}


	public void render(Graphics g) {
		g.drawImage(Game.player.getBufferedImage(), x, y, width, height,null );
	}


	public void tick() {
		x+=velX;
		y+=velY;
		for(int i= 0; i<handler.tile.size();i++) {
			Tile t = handler.tile.get(i);
			if(t.isSolid()&&!goingDownPipe) {
				if(getBounds().intersects(t.getBounds()))
				{
					if(t.getId() == Id.flag) {
						Game.switchLevel();
					}
				}
				if(getBoundsTop().intersects(t.getBounds())) {
					setVelY(0);
					if(jumping&&!goingDownPipe) {
						jumping = false;
						gravity = 0.8;
						falling = true;
					}
					if(t.getId()==Id.powerUp) {
						if(getBoundsTop().intersects(t.getBounds())) t.activated = true;
						
					}
					
				}
				if(getBoundsBottom().intersects(t.getBounds())) {
					setVelY(0);
					if(falling) falling = false;
					
				}else if  (!falling&&!jumping) {
						falling = true;
						gravity = 0.8;
						
					}
				if(getBoundsLeft().intersects(t.getBounds())) {
					setVelX(0);
					x = t.getX() + t.height;
					
				}
				if(getBoundsRight().intersects(t.getBounds())) {
					setVelX(0);
					x = t.getX()-t.height;	
				}
			}
				
				}
				
				
		
		
		for(int i = 0; i < handler.entity.size(); i++) {
			Entity e = handler.entity.get(i);
			
			if(e.getId() == Id.mushroom) {
				switch(e.getType()) {
				case 0:
					if(getBounds().intersects(e.getBounds())){
						int tpX = getX();
						int tpY=getY();
						Game.getPower.play(0);
						width+=(width/3);
						height+=(height/3);
						setX(tpX-width);
						setY(tpY-height);
						if(state == PlayerState.SMALL) state = PlayerState.BIG;
						e.die();
					}
					break;
				case 1:
					if(getBounds().intersects(e.getBounds())){
						Game.lives++;
						e.die();
					}
					break;
				}
				
			} else if (e.getId() == Id.goomba || e.getId()==Id.towerBoss) {
				
				if(getBoundsBottom().intersects(e.getBoundsTop())) {
					if(e.getId()!=Id.towerBoss) {
						Game.stomp.play(0);
						e.die();
					}
					else if(e.attackable) {
						e.hp--;
						e.falling = true;
						e.gravity = 3.0;
						e.bossState = BossState.RECOVERING;
						e.attackable = false;
						
						jumping = true;
						falling = false;
						gravity = 3.5;
						e.phaseTime = 0;
					}
					
				}
				else if(getBounds().intersects(e.getBounds())){
					if(state == PlayerState.BIG ) {
						state = PlayerState.SMALL;
						width/=2;
						height/=2;
						x+=width;
						y+=height;
					}
					else if(state == PlayerState.SMALL) {
						die();
					}

				}
			} else if(e.getId() == Id.coin) {
				if(getBounds().intersects(e.getBounds())&&e.getId()==Id.coin) {
					Game.coins++;
					e.die();
				}
			}
		}
		
		if (jumping&&!goingDownPipe) {
			gravity -=0.14;
			setVelY((int)-gravity);
			if(gravity <=0.7) {
				jumping = false;
				falling = true;
			}
		}
		if(falling&&!goingDownPipe) {
			gravity +=0.14;
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
		if(goingDownPipe) {
			for(int i = 0; i<Game.handler.tile.size();i++) {
				Tile t = Game.handler.tile.get(i);
				if(t.getId()==Id.pipe) {
					if(getBounds().intersects(t.getBounds())) {
						switch(t.facing) {
						case 0: //facing upwards
							setVelY(-5);
							setVelX(0);
							pixelsTravelled+=-velY;
							break;
						case 2://facing downwards
							setVelY(5);
							setVelX(0);
							pixelsTravelled+=velY;
							break;
						}
						if(pixelsTravelled >= t.height + height) {
							goingDownPipe = false;
							pixelsTravelled = 0;
						}
						
					}
					
				}
				
			}
		}
		
		
	}

}