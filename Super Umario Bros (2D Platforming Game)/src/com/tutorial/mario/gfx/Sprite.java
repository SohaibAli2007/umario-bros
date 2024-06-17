package com.tutorial.mario.gfx;

import java.awt.image.BufferedImage;

public class Sprite {
	//Continue from 3:06 Video 15
	public SpriteSheet sheet;
	
	public BufferedImage image;
	
	public Sprite(SpriteSheet sheet, int x, int y) {
		image = sheet.getSprite(x, y);
		
	}
	public BufferedImage getBufferedImage() {
		return image;
	}
	
}
