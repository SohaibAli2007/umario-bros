package com.tutorial.mario.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import com.tutorial.mario.Game;
import com.tutorial.mario.Id;
import com.tutorial.mario.entity.Entity;
import com.tutorial.mario.tile.Tile;



public class KeyInput implements KeyListener{
// On 13:17 of video 11
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i = 0;i<Game.handler.entity.size();i++) {
			Entity en = Game.handler.entity.get(i);	
			if(en.getId()==Id.player) {
				if(en.goingDownPipe) return;
				switch(key) {
				case KeyEvent.VK_W:
					for(int q = 0; q<Game.handler.tile.size();q++) {
						Tile t = Game.handler.tile.get(q);
						if(t.getId()==Id.pipe) {
							if(en.getBoundsTop().intersects(t.getBounds())) {
								if(!en.goingDownPipe) {
									Game.pipeEnter.play(0);
									en.goingDownPipe = true;
								}
							}
						}
					}
					if(!en.jumping) {
						en.jumping = true;
						en.gravity = 8.0;
						Game.jump.play(0);
					}
					break;
				case KeyEvent.VK_S:
					for(int q = 0; q<Game.handler.tile.size();q++) {
						Tile t = Game.handler.tile.get(q);
						if(t.getId()==Id.pipe) {
							if(en.getBoundsBottom().intersects(t.getBounds())) {
								if(!en.goingDownPipe) {
									Game.pipeEnter.play(0);
									en.goingDownPipe = true;
								}
							}
						}
					}
					break;
				case KeyEvent.VK_A:
					en.setVelX(-5);
					break;
				case KeyEvent.VK_D:
					en.setVelX(5);
					break;
				case KeyEvent.VK_Q:
					en.die();
				}
			}
			}
			}
			
			
				
			
				
			
	

	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i = 0;i<Game.handler.entity.size();i++) {
			Entity en = Game.handler.entity.get(i);	
			if(en.getId()==Id.player) {
				switch(key) {
				case KeyEvent.VK_W:
					en.setVelY(0);
					break;
				case KeyEvent.VK_S:
					en.setVelY(0);
					break;
				case KeyEvent.VK_A:
					en.setVelX(0);
					break;
				case KeyEvent.VK_D:
					en.setVelX(0);
					break;
				}
			}
			
		}
		
	}

	
	public void keyTyped(KeyEvent arg0) {
		// not using
		
	}
}



