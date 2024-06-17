package com.tutorial.mario;
// Video 8 OfficialCoding Network
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tutorial.mario.entity.Entity;
import com.tutorial.mario.entity.mob.Player;
import com.tutorial.mario.gfx.Sprite;
import com.tutorial.mario.gfx.SpriteSheet;
import com.tutorial.mario.gfx.gui.Launcher;
import com.tutorial.mario.input.KeyInput;
import com.tutorial.mario.input.MouseInput;
import com.tutorial.mario.tile.Tile;
import com.tutorial.mario.tile.Wall;

import com.tutorial.mario.entity.mob.TowerBoss;

import java.lang.Object;
import java.util.Comparator;
import java.util.LinkedList;

public class Game extends Canvas implements Runnable {

	public static final int WIDTH = 270;
	public static final int HEIGHT = WIDTH/14*10;
	public static final int SCALE = 4;
	public static final String TITLE = "Super Umario Bros.";
	
	private Thread thread;
	private boolean running = false;
	
	private static BufferedImage[] levels;
	
	public static BufferedImage titleBackground;
	public static BufferedImage finishBackground;
	
	
	public static TowerBoss towerBossKilled;

	public static int level = 0;
	
	//Integers
	public static int coins= 0; //creates counter for coins. Picking up one adds to the counter and shows it on the screen
	public static int lives = 3;
	public static int deathScreenTime = 0;
	public static int fallY = 0;
	
	
	
	public static boolean showDeathScreen = true;
	public static boolean gameOver = false;
	public static boolean playing = false;
	

	public static Handler handler;
	public static SpriteSheet sheet;
	public static Camera cam;
	public static Launcher launcher;
	public static MouseInput mouse;
	
	//Tile
	public static Sprite grass;
	public static Sprite pipe;
	public static Sprite powerUp;
	public static Sprite usedPowerUp;
	
	public static Sprite mushroom;
	public static Sprite lifeMushroom;
	public static Sprite coin;
	
	//Entities
	public static Sprite player;
	public static Sprite goomba;
	public static Sprite[] flag;
	public static Sprite towerBoss;
	
	//Audio/Sounds
	public static Sound jump;
	public static Sound playerDie;
	public static Sound bgMusic;
	public static Sound oneUp;
	public static Sound powerAppear;
	public static Sound getPower;
	public static Sound pipeEnter;
	public static Sound stomp;
	public static Sound clearStage;
	
	
	public Game() 
	{
		Dimension size = new Dimension(WIDTH*SCALE,HEIGHT*SCALE);
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
	}
	
	private void init() {
		handler = new Handler();
		sheet  = new SpriteSheet("/spritesheet.png");
		cam = new Camera();
		launcher = new Launcher();
		mouse = new MouseInput();
		
		addKeyListener(new KeyInput());
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		
		grass = new Sprite(sheet,1,1);
		pipe = new Sprite(sheet, 7, 1);
		powerUp = new Sprite(sheet, 5, 1);
		usedPowerUp = new Sprite(sheet, 6, 1);
		
		player = new Sprite(sheet,2,1);
		mushroom = new Sprite(sheet,3, 1);
		lifeMushroom = new Sprite(sheet, 10, 1);
		coin = new Sprite (sheet, 8, 1);
		
		flag = new Sprite[3];
		goomba = new Sprite(sheet, 4, 1);
		towerBoss = new Sprite(sheet, 9, 1);
		
		levels = new BufferedImage[3];

		
		for(int i = 0; i < flag.length; i ++) {
			flag [i] = new Sprite(sheet, i+1,2);
		}
		
		try {
			levels[0] = ImageIO.read(getClass().getResource("/level.png"));
			levels[1] = ImageIO.read(getClass().getResource("/towerBossLevel.png"));
			levels[2] = ImageIO.read(getClass().getResource("/gameFinished.png"));
			titleBackground = ImageIO.read(getClass().getResource("/title_bg.png"));
			finishBackground = ImageIO.read(getClass().getResource("/hooray.png"));
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		//instantiating sounds
		jump = new Sound("/audio/playerJump.wav");
		playerDie = new Sound("/audio/playerDie.wav");
		bgMusic = new Sound("/audio/theme.wav");
		oneUp = new Sound("/audio/oneUp.wav");
		getPower = new Sound("/audio/powerupGet.wav");
		pipeEnter = new Sound("/audio/pipe.wav");
		powerAppear = new Sound("/audio/powerupAppears.wav");
		stomp =  new Sound("/audio/stomp.wav");
		clearStage= new Sound("/audio/stageClear.wav");
		
		
	}
	
	
	private synchronized void start()
	{
		if(running) return;
		running = true;
		thread = new Thread(this, "Thread");
		thread.start();
	}
	
	private synchronized void stop() 
	{
		if(!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() 
	{
		init();
		requestFocus();
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double delta = 0;
		double ns = 1000000000.0/60.0;
		int frames = 0;
		int ticks = 0;
		while (running)
		{
			long now = System.nanoTime();
			delta += (now-lastTime)/ns;
			lastTime = now;
			while (delta>=1)
			{
				tick();
				ticks++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis()-timer>1000) {
				timer += 1000;
				System.out.println(frames + " Frames Per Second " + ticks + " Updates Per Second");
				frames = 0;
				ticks = 0;
			}
			
		}
		stop();
	}
	
	public void render() //renders backgrounds and counts the frames per second
	{
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics(); //Instantiate object to place colors on the graphics
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(!showDeathScreen) {
			g.drawImage(coin.getBufferedImage(),20, 20, 75, 75, null);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier", Font.BOLD, 20));
			g.drawString("x" + coins,100,95);
		}
		if(showDeathScreen) {
			if(!gameOver) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Courier", Font.BOLD, 50));
				g.drawImage(Game.player.getBufferedImage(),500, 300, 100, 100, null);
				g.drawString("x" + lives, 610,400);
				
			}else {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Courier", Font.BOLD, 50));
				g.drawString("Game Over :(" + lives, 300,400);
			}
		}
		if(Game.level == 2) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier", Font.BOLD, 50));
			g.drawImage(Game.finishBackground, 0, 0, Game.getFrameWidth(), Game.getFrameHeight(),null);
			

			
		}
		
		
		
		if(playing)g.translate(cam.getX(), cam.getY());
		if(!showDeathScreen&&playing&&Game.level!=2) handler.render(g);
		else if(!playing) launcher.render(g);
		g.dispose();
		bs.show();
		
	}
	
	public void tick()
	{
		if(playing)handler.tick();
		
		for(int i=0; i<handler.entity.size();i++) {
			Entity e = handler.entity.get(i);

			if(e.getId() == Id.player) {
				cam.tick(e);
			}
		}
		if(showDeathScreen &&!gameOver&&playing) deathScreenTime++;
		if(deathScreenTime>=180) {
			if(!gameOver) {
				showDeathScreen = false;
				deathScreenTime = 0;
				handler.clearLevel();
				handler.createLevel(levels[level]);
				bgMusic.play(1);
				
				
			}else if (gameOver) {
				showDeathScreen = false;
				deathScreenTime = 0;
				playing = false;
				gameOver = false;
				
				
			}
			
			
		}
		
	}
	
	public static int getFrameWidth() {
		return WIDTH*SCALE;
	}
	
	
	public static int getFrameHeight() {
		return HEIGHT*SCALE;
	}
	
	public static void switchLevel() {
		
		Game.level++;
		
		
		handler.clearLevel();
		handler.createLevel(levels[level]);
		
		if(Game.level == 2) {
			Game.bgMusic.stop();
			Game.jump.stop();
			Game.clearStage.play(0);
		}
	}
	
	public static Rectangle getVisibleArea(){
		for(int i = 0; i< handler.entity.size(); i++) {
			Entity e = handler.entity.get(i);
			if(e.getId() == Id.player) return new Rectangle(e.getX()-(getFrameWidth()/2-5), e.getY()-(getFrameHeight()/2-5), getFrameWidth()+10, getFrameHeight()+10);
		}
		return null;
	}
	
	
	
	public static void main(String[] args)
	{
		Game game = new Game();
		JFrame frame = new JFrame(TITLE);
		frame.add(game);
		frame.pack();
		frame.setResizable(false);;
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		game.start();
	}


	

	
	
}
