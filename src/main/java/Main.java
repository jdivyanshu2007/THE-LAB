import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class Main{
    public static void main(String[] args){
		BackgroundMusic.play();
        JFrame frame = new JFrame("THE LAB");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel backgroundPanel =  new JPanel(){
            private Image background;
			private boolean backgroundMode = true; //true means light is on 
			private Timer zombieTimer;
			private Timer backgroundTimer;
			private int animationCounter;

            private int playerX = 1010;
            private int playerY = 530;
			private int zombieX = 1623;
			private int zombieY = 925;
			private int zombieSpeed = 2;
            private int speed = 10;

            private BufferedImage SpriteSheet;
			private Image character;

			private BufferedImage ZombieSheet;
			private Image Zombie;

            private BufferedImage[][] walkframe;
			private BufferedImage[][] ZombieWalkframe;

			private int ZombieFrameWidth; 
			private int ZombieFameHeight;


            private int CurrentFrame = 0;

            private int frameHeight;
            private int frameWidth;

            private int frameRow = 0;
            private int frameCol = 0;
			private int zombieFrameRow = 0;
			private int zombieFrameCol = 0;

            private int playerWidth = 80;
            private int playerHeight = 80;


            private Rectangle[] obstacles = {
				new Rectangle(451,658,469,132),//table near gate
				new Rectangle(1286,386,136,390),//long table
				new Rectangle(1800,270,10,655),//rightmost table
				new Rectangle(109,368,10,700),//leftmost table
				new Rectangle(882,221,10,200),//uppermost table downside
				new Rectangle(882,221,900,10),//uppermost table upside
				new Rectangle(125,1006,700,10),//bottommost table leftside
				new Rectangle(1093,1047,700,10),//bottommost table rightside
				new Rectangle(40,489,800,10),//gate
				new Rectangle(1232,635,250,200),//teacher table
				//bottom side chairs left side
				new Rectangle(640,928,25,20),
				new Rectangle(762,927,25,20),
				new Rectangle(209,939,25,20),
				//left side chairs
				new Rectangle(178,847,5,2),
				new Rectangle(178,750,5,2),
				new Rectangle(179,553,5,2),
				new Rectangle(179,653,5,2),
				//right side chairs
				new Rectangle(1737,283,5,2),
				new Rectangle(1739,726,5,2),
				new Rectangle(1739,631,5,2),
				new Rectangle(1742,848,5,2),
				new Rectangle(1742,848,5,2),
				new Rectangle(1741,481,5,2),
				new Rectangle(1740,387,5,2),
				new Rectangle(1502,385,5,2),
				//middle chairs right side
				new Rectangle(1501,491,5,2),
				new Rectangle(1528,591,5,2),
				new Rectangle(1578,737,5,2),
				new Rectangle(1529,791,5,2),

				//middle chairs left side
				new Rectangle(1203,385,5,2),
				new Rectangle(1212,489,5,2),
				new Rectangle(1185,591,5,2),
				new Rectangle(1154,754,5,2),
				new Rectangle(1165,799,5,2),
				new Rectangle(1351,884,5,2),

				//upper side chairs
				new Rectangle(1137,279,5,2),
				new Rectangle(1039,279,5,2),
				new Rectangle(941,279,5,2),
				new Rectangle(1487,279,5,2),
				new Rectangle(1580,279,5,2),
				new Rectangle(1679,279,5,2),

				//random chairs and obstcles
				new Rectangle(772,620,5,2),
				new Rectangle(869,619,5,2),
				new Rectangle(212,537,5,2),
				new Rectangle(1756,1019,5,2),
				new Rectangle(1684,1026,5,2),
				new Rectangle(530,616,5,2),
				new Rectangle(651,617,5,2),

			};
            private int nextX,nextY;
            private int worldWidth = 1920;
            private int worldHeight = 1080;
            
            private boolean isColliding(int nextX , int nextY){
				Rectangle player = new Rectangle(nextX, nextY, playerWidth, playerHeight);
				for(Rectangle obstacle : obstacles){
					if(player.intersects(obstacle)){
						return true;
					}
				}
				return false;
			}
			
			private boolean isOutOfBound(int x , int y){
				return (x < 0 || y < 0 || x + playerWidth > worldWidth || y + playerHeight > worldHeight);
			}



            {
                try{
                    background = ImageIO.read(Main.class.getResource("/background_lab.jpeg"));
                    SpriteSheet = ImageIO.read(Main.class.getResource("/character.png"));
					ZombieSheet = ImageIO.read(Main.class.getResource("./zombie1.png"));
					System.out.println(ZombieSheet.getHeight() + "X" + ZombieSheet.getWidth());
                    frameHeight = SpriteSheet.getHeight()/4;
                    frameWidth = 120;
					ZombieFameHeight = ZombieSheet.getHeight()/4;
					ZombieFrameWidth = (ZombieSheet.getWidth() -300)/4;
                    walkframe = new BufferedImage[4][4];
					ZombieWalkframe = new BufferedImage[4][4];
                    for(int row = 0 ; row < 4 ; row++){
						for(int col = 0 ; col < 4 ; col++){
							walkframe[row][col] = SpriteSheet.getSubimage(
									60+col*frameWidth,
									row*frameHeight,
									frameWidth ,
									frameHeight
							);
							ZombieWalkframe[row][col] = ZombieSheet.getSubimage(
									150+col*ZombieFrameWidth,
									row*ZombieFameHeight,
									ZombieFrameWidth ,
									ZombieFameHeight
							);
						}
					
					}
					zombieTimer = new Timer(30,e->{
						moveZombie();
						repaint();
					});
					backgroundTimer = new Timer(10000,e->{
						try{
							if(backgroundMode){
								background = ImageIO.read(Main.class.getResource("/Dark.jpg"));
								backgroundMode = false;
							}else{
								background = ImageIO.read(Main.class.getResource("/background_lab.jpeg"));
								backgroundMode = true;
							}
						}catch(Exception e1){
							System.out.println(e1);
						}
					});
					backgroundTimer.start();
					zombieTimer.start();
                }catch(IOException e){
                    System.out.println(e.getMessage());
                };
            }


            {
				setFocusable(true);
				addKeyListener(new KeyAdapter(){
					@Override
					public void keyPressed(KeyEvent e){
						switch(e.getKeyCode()){
							case KeyEvent.VK_UP:
								frameRow = 3;
								if(frameCol < 3){
									frameCol++;
								}else{
									frameCol = 0;
								}
								int nextY = playerY - speed;
								if(!isColliding(playerX,nextY) && !isOutOfBound(playerX,nextY)){
									playerY = nextY;
								}
								break;
							case KeyEvent.VK_DOWN:
								frameRow = 0;
								if(frameCol < 3){
									frameCol++;
								}else{
									frameCol = 0;
								}
								nextY = playerY + speed;
								if(!isColliding(playerX,nextY) && !isOutOfBound(playerX,nextY)){
									playerY = nextY;
								}
								break;
							case KeyEvent.VK_LEFT:
								frameRow = 2;
								if(frameCol < 3){
									frameCol++;
								}else{
									frameCol = 0;
								}
								nextX = playerX - speed;
								if(!isColliding(nextX , playerY) && !isOutOfBound(nextX,playerY)){
									playerX  = nextX;
								}
								break;
							case KeyEvent.VK_RIGHT:
								frameRow = 1;
								if(frameCol < 3){
									frameCol++;
								}else{
									frameCol = 0;
								}
								nextX = playerX + speed;
								if(!isColliding(nextX , playerY) && !isOutOfBound(nextX,playerY)){
									playerX  = nextX;
								}
								break;
						}
						repaint();
					}
				}); 
				
			}
			private boolean isZombieColliding(int x, int y) {
					Rectangle zombie = new Rectangle(
						x, y, playerWidth, playerHeight
					);

					for (Rectangle obstacle : obstacles) {
						if (zombie.intersects(obstacle)) {
							return true;
						}
					}

					return false;
			}
			private boolean isZombieOutOfBound(int x, int y) {
				return x < 0 || y < 0 ||
					x + playerWidth > worldWidth ||
					y + playerHeight > worldHeight;
			}
			private void moveZombie() {
				int dx = playerX - zombieX;
				int dy = playerY - zombieY;

				double distance = Math.sqrt(
					(double) dx * dx + (double) dy * dy
				);

				// Stop when the zombie reaches the player.
				if (distance < 60) {
					JOptionPane.showMessageDialog(null,"Game Over");
					playerX = 200;
					playerY = 200;
					zombieX = 100;
					zombieY = 100;
					return;
				}

				// Normalize direction so diagonal movement
				// is not faster than horizontal movement.
				double directionX = dx / distance;
				double directionY = dy / distance;

				int stepX = (int) Math.round(
					directionX * zombieSpeed
				);

				int stepY = (int) Math.round(
					directionY * zombieSpeed
				);

				// Determine which direction the zombie faces.
				if (Math.abs(dx) > Math.abs(dy)) {
					zombieFrameRow = dx > 0 ? 1 : 2;
				} else {
					zombieFrameRow = dy > 0 ? 0 : 3;
				}

				// Try horizontal movement first.
				int nextZombieX = zombieX + stepX;

				if (!isZombieColliding(nextZombieX, zombieY)
						&& !isZombieOutOfBound(
							nextZombieX, zombieY
						)) {
					zombieX = nextZombieX;
				}

				// Then try vertical movement.
				int nextZombieY = zombieY + stepY;

				if (!isZombieColliding(zombieX, nextZombieY)
						&& !isZombieOutOfBound(
							zombieX, nextZombieY
						)) {
					zombieY = nextZombieY;
				}

				// Animate more slowly than the timer updates.
				animationCounter++;

				if (animationCounter >= 5) {
					zombieFrameCol = (zombieFrameCol + 1) % 4;
					animationCounter = 0;
				}
			}
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                double scaleX = (double) getWidth()/worldWidth;
				double scaleY = (double) getHeight()/worldHeight;
				
				double scale = Math.min(scaleX,scaleY);
				
				double offsetX = (int) ((getWidth() - worldWidth * scale) / 2);
				double offsetY = (int) ((getHeight() - worldHeight * scale) / 2);
				
                Graphics2D g2d = (Graphics2D) g.create();
                
                g2d.translate(offsetX,offsetY);
                g2d.scale(scale,scale);
                g2d.drawImage(background, 0, 0, worldWidth, worldHeight, this);
				g2d.drawImage(walkframe[frameRow][frameCol], playerX, playerY, playerWidth, playerHeight, this);
				g2d.drawImage(ZombieWalkframe[zombieFrameRow][zombieFrameCol], zombieX, zombieY, playerWidth, playerHeight, this);
				g2d.setColor(Color.RED);
				for(Rectangle obstacle:obstacles){
					//g2d.draw(obstacle);
				};
				g2d.dispose();
            }

        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }
}
