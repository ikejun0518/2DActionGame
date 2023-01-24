import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;

public class Game extends JFrame {
	final int windowWidth = 800;
	final int windowHeight = 500;

	public static void main(String args[]) {
		new Game();
	} 

	public Game() {
		Dimension dimOfScreen = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(dimOfScreen.width/2 - windowWidth/2, dimOfScreen.height/2 - windowHeight/2, windowWidth, windowHeight);
		setResizable(false);
		setTitle("Software Development II");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		MyJPanel panel = new MyJPanel();
		Container c = getContentPane();
		c.add(panel);
		setVisible(true);
		addKeyListener(panel);
	}
	public class MyJPanel extends JPanel implements KeyListener {
		
		//全体の設定に関する変数
		Dimension dimOfPanel;
		ImageIcon iconMe, iconEnemy1, iconEnemy2, iconblock;
		Image imgMe, imgEnemy1, imgEnemy2, imgBlock, imgGoal;
		Timer timer1, timer2;
		boolean gameclear = false;
		
		//自キャラに関する変数
		int myWidth, myHeight;
		int myX, myY;
		int jumptime = 0;
		boolean left = false;
		boolean right = false;
		boolean dawn = true;
		boolean earth = false;

		//ゴールに関する変数
		int goalX, goalY;
		int goalWidth, goalHeight;

		//ブロックに関する変数
		int numOfBlogk = 195;
		int blockWidth, blockHeight;
		int[] blockX = new int[numOfBlogk];
		int[] blockY = new int[numOfBlogk];
 
		//敵キャラに関する変数
		int numOfEnemy1 = 2;
		int enemy1Width, enemy1Height;
		int enemy2Width, enemy2Height;
		int enemy2X,enemy2Y;
		int enemy2pattern;
		int enemy2Horizon, enemy2vertical;
		int actTime = 0;
		int missileX, missileY;
		boolean enemy2act;
		boolean isMissileActive;
		int[] enemy1X = new int[numOfEnemy1];
		int[] enemy1Y = new int[numOfEnemy1];
		int[] enemyHorizon = new int[numOfEnemy1];

		public MyJPanel() {
			//全体の設定
			timer1 = new Timer(50,mTimerListener1);
			timer2 = new Timer(50,mTimerListener2);
			timer1.start();

			//画像の読み込み
			imgMe = getImg("mychara.jpg");
			myWidth = imgMe.getWidth(this);
			myHeight = imgMe.getHeight(this);

			imgBlock = getImg("block.jpg");
			blockWidth = imgBlock.getWidth(this);
			blockHeight = imgBlock.getHeight(this);

			imgEnemy1 = getImg("enemychara1.jpg");
			enemy1Width = imgEnemy1.getWidth(this);
			enemy1Height = imgEnemy1.getHeight(this);

			imgEnemy2 = getImg("enemychara2.jpg");
			enemy2Width = imgEnemy2.getWidth(this);
			enemy2Height = imgEnemy2.getHeight(this);

			imgGoal = getImg("goal.jpg");
			goalWidth = imgGoal.getWidth(this);
			goalHeight = imgGoal.getHeight(this);

			//初期化
			initblock();
			initmychara();
			initEnemy();
			initGoal();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		//キー入力処理
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				//左キー,A入力
				case KeyEvent.VK_LEFT: 
					left = true;
					break;
				case KeyEvent.VK_A:
					left = true;
					break;
				//右キー,D入力
				case KeyEvent.VK_RIGHT:
					right = true;
					break;
				case KeyEvent.VK_D:
					right = true;
					break;
				//スペースキー入力
				case KeyEvent.VK_SPACE:
					jump();
					break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				//左キー,A
				case KeyEvent.VK_LEFT: 
					left = false;
					break;
				case KeyEvent.VK_A:
					left = false;
					break;
				//右キー,D
				case KeyEvent.VK_RIGHT:
					right = false;
					break;
				case KeyEvent.VK_D:
					right = false;
					break;
				//スペースキー
				case KeyEvent.VK_SPACE:
					break;
			}
		}
		
		//パネル上の描画
		public void paintComponent(Graphics g) {
			dimOfPanel = getSize();
			super.paintComponent(g);

			//各要素の描画
			drawblock(g);
			drawmychara(g);
			drawEnemy1(g);
			drawEnemy2(g);
			drawMissile(g);
			drawGoal(g);
		}

		//ゲーム全体のActionListener
		public ActionListener mTimerListener1 = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				actTime--;
				repaint();
			}
		};

		//ジャンプ用のActionListener
		public ActionListener mTimerListener2 = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
					jumptime--;
					if(jumptime > 0) {
						myY -= 10;
						repaint();
					} else {
						dawn = true;
						timer2.stop();
					}
			}
		};


		//画像ファイルからImageクラスへの変換
        public Image getImg(String filename) {
            ImageIcon icon = new ImageIcon(filename);
            Image img = icon.getImage();
            return img;
		}

		//ブロックの初期化
		public void initblock() {
			for (int i=0; i<40; i++) {
				blockX[i] = i*blockWidth;
				blockY[i] = 400;
			}
			for (int i=40; i<51; i++) {
				int a = i - 40;
				blockX[i] = 180 + a*blockWidth;
				blockY[i] = 380;
			}
			for (int i=51; i<67; i++) {
				int a = i - 51;
				blockX[i] = 360;
				blockY[i] = 360 - a*blockHeight;
			}
			for (int i=67; i<82; i++) {
				int a = i - 67;
				blockX[i] = 380;
				blockY[i] = 360 - a*blockHeight;
			}
			blockX[82] = 300;
			blockY[82] = 340; 
			for (int i=83; i<88; i++) {
				int a = i - 83;
				blockX[i] = 160 + a*blockWidth;
				blockY[i] = 300;
			}
			for (int i=88; i<91; i++) {
				int a = i - 88;
				blockX[i] = 60 + a*blockWidth;
				blockY[i] = 260;
			}

			for (int i=91; i<93; i++) {
				int a = i - 91;
				blockX[i] = a * blockWidth;
				blockY[i] = 220;
			}

			blockX[93] = 80;
			blockY[93] = 180;

			blockX[94] = 140;
			blockY[94] = 140;

			blockX[95] = 200;
			blockY[95] = 100;

			blockX[96] = 240;
			blockY[96] = 50;

			blockX[97] = 300;
			blockY[97] = 100;

			for (int i=98; i<115; i++) {
				int a = i - 98;
				blockX[i] = 400 + a	* blockWidth;
				blockY[i] = 80;
			}

			for (int i=115; i<132; i++) {
				int a = i - 115;
				blockX[i] = 400 + a	* blockWidth;
				blockY[i] = 100;
			}

			for (int i=132; i<150; i++) {
				int a = i - 132;
				blockX[i] = 440 + a	* blockWidth;
				blockY[i] = 220;
			}

			for (int i=150; i<168; i++) {
				int a = i - 150;
				blockX[i] = 440 + a	* blockWidth;
				blockY[i] = 240;
			}

			for (int i=168; i<186; i++) {
				int a = i - 168;
				blockX[i] = 440 + a	* blockWidth;
				blockY[i] = 260;
			}

			blockX[186] = 720;
			blockY[186] = 60;

			blockX[187] = 780;
			blockY[187] = 200;

			blockX[188] = 440;
			blockY[188] = 200;

			for (int i=189; i<195; i++) {
				int a = i - 189;
				blockX[i] = 780;
				blockY[i] = 280 + a * blockHeight;
			}

		}

		//ゴールの初期化
		public void initGoal() {
			goalX = 760;
			goalY = 380;
		}

		//自キャラの初期化
		public void initmychara() {
			myX = 0;
			myY = 300;
		}

		//敵キャラの初期化
		public void initEnemy() {
			enemy1X[0] = 500;
			enemy1Y[0] = 200;
			enemy1X[1] = 600;
			enemy1Y[1] = 60;
			enemy2X = 700;
			enemy2Y = 360;
			enemy2act = true;
			enemy2Horizon = 1;
			enemy2vertical = 1;
			for(int i=0; i<numOfEnemy1; i++) {
				enemyHorizon[i] = 1;
			}
		}

		//ブロックの描画
		public void drawblock(Graphics g) {
			for (int i=0; i<numOfBlogk; i++) {
				g.drawImage(imgBlock, blockX[i], blockY[i], this);
			}
		}

		//自キャラの描画
		public void drawmychara(Graphics g) {
			if(dawn) {
				myY += 10;
			}
			if(left) {
				if(myX <= 0) {
					myX = 0;
				} else {
					myX -= 5;
				}
			}
			if(right) {
				if(myX+myWidth > windowWidth) {
					myX = windowWidth - myWidth;
				} else {
					myX += 5;
				}
			}

			//ブロックへの当たり判定
			for(int i=0; i<numOfBlogk; i++) {
				//上面
				if (myY+myHeight > blockY[i] &&
					myY+myHeight < blockY[i] + blockWidth && 
					myX+myWidth > blockX[i] && 
					myX < blockX[i]+blockWidth) {
						earth = true;
						myY = blockY[i] - myWidth;
				}
				//右面
				if (myX+myWidth > blockX[i] &&
					myX+myWidth < blockX[i]+blockWidth &&
					myY+myHeight > blockY[i] &&
					myY < blockY[i]+blockHeight) {
						right = false;
						myX = blockX[i] - myWidth;
					}
				//左面
				if (myX < blockX[i]+blockWidth &&
					myX > blockX[i] &&
					myY+myHeight > blockY[i] &&
					myY < blockY[i]+blockHeight) {
						left = false;
						myX = blockX[i] + blockWidth;
					}
				
				//下面
				if (myY < blockY[i] + blockHeight &&
					myY > blockY[i] && 
					myX+myWidth > blockX[i] && 
					myX < blockX[i]+blockWidth) {
						jumptime = 0;
						myY = blockY[i] + blockHeight;
				}
			}

			g.drawImage(imgMe, myX, myY, this);
		}

		//ゴールの描画
		public void drawGoal(Graphics g) {
			//ゴールの判定
			if( myX >= goalX &&
				myY >= goalY) {
					gameclear = true;
					gameclear();      
				} 

			g.drawImage(imgGoal, goalX, goalY, this);
		}

		//enemy1の描画
		public void drawEnemy1(Graphics g) {
			for (int i=0; i<numOfEnemy1; i++) {
				enemy1Y[i] += 0.1;
				//移動
				enemy1X[i] += 3*enemyHorizon[i];
				//blockへの当たり判定
				for(int j=0; j<numOfBlogk; j++) {
					//上面
					if (enemy1Y[i]+enemy1Height > blockY[j] &&
						enemy1Y[i]+enemy1Height < blockY[j] + blockWidth && 
						enemy1X[i] > blockX[j] && 
						enemy1X[i] < blockX[j]+blockWidth) {
							enemy1Y[i] = blockY[j] - enemy1Height;
						}
					//左面
					if (enemy1X[i] < blockX[j]+blockWidth &&
						enemy1X[i] > blockX[j] &&
						enemy1Y[i]+enemy1Height > blockY[j] &&
						enemy1Y[i] < blockY[j]+blockHeight) {
							enemy1X[i] = blockX[j] + blockWidth;
							enemyHorizon[i] *= -1;
						}
					//右面
					if (enemy1X[i]+enemy1Width > blockX[j] &&
						enemy1X[i]+enemy1Width < blockX[j]+blockWidth &&
						enemy1Y[i]+enemy1Height > blockY[j] &&
						enemy1Y[i] < blockY[j]+blockHeight) {
							enemy1X[i] = blockX[j] - myWidth;
							enemyHorizon[i] *= -1;
						}
				}

				//mycharaへの当たり判定
				//上面
				if (enemy1Y[i]+enemy1Height > myY &&
					enemy1Y[i]+enemy1Height < myY + myWidth && 
					enemy1X[i] > myX && 
					enemy1X[i] < myX+myWidth) {
						System.exit(i);
					}
				//左面
				if (enemy1X[i] < myX+myWidth &&
					enemy1X[i] > myX &&
					enemy1Y[i]+enemy1Height > myY &&
					enemy1Y[i] < myY+myHeight) {
						System.exit(i);
					}
				//右面
				if (enemy1X[i]+enemy1Width > myX &&
					enemy1X[i]+enemy1Width < myX+myWidth &&
					enemy1Y[i]+enemy1Height > myY &&
					enemy1Y[i] < myY+myHeight) {
						System.exit(i);
					}
				


				g.drawImage(imgEnemy1, enemy1X[i], enemy1Y[i], this);
			}
		}

		//enemy2の描画
		public void drawEnemy2(Graphics g) {
			//mycharaへの当たり判定
			//上面
			if (myY+myHeight > enemy2Y &&
			myY+myHeight < enemy2Y + enemy2Width && 
			myX+myWidth > enemy2X && 
			myX < enemy2X+enemy2Width) {
				System.exit(DISPOSE_ON_CLOSE);
			}
			//右面
			if (myX+myWidth > enemy2X &&
				myX+myWidth < enemy2X+enemy2Width &&
				myY+myHeight > enemy2Y &&
				myY < enemy2Y+enemy2Height) {
					System.exit(DISPOSE_ON_CLOSE);
				}
			//左面
			if (myX < enemy2X+enemy2Width &&
				myX > enemy2X &&
				myY+myHeight > enemy2Y &&
				myY < enemy2Y+enemy2Height) {
					System.exit(DISPOSE_ON_CLOSE);
				}
			
			//下面
			if (myY < enemy2Y + enemy2Height &&
				myY > enemy2Y && 
				myX+myWidth > enemy2X && 
				myX < enemy2X+enemy2Width) {
					System.exit(DISPOSE_ON_CLOSE);
			}

			//ブロックへの当たり判定
			for(int i=0; i<numOfBlogk; i++) {
				//上面
				if (enemy2Y+enemy2Height > blockY[i] &&
					enemy2Y+enemy2Height < blockY[i] + blockWidth && 
					enemy2X+enemy2Width > blockX[i] && 
					enemy2X < blockX[i]+blockWidth) {
						enemy2Y = blockY[i] - enemy2Height;
						enemy2vertical *= -1;
				}
				//右面
				if (enemy2X+enemy2Width > blockX[i] &&
					enemy2X+enemy2Width < blockX[i]+blockWidth &&
					enemy2Y+enemy2Height > blockY[i] &&
					enemy2Y < blockY[i]+blockHeight) {
						enemy2X = blockX[i] - enemy2Width;
						enemy2Horizon *= -1;
					}
				//左面
				if (enemy2X < blockX[i]+blockWidth &&
					enemy2X > blockX[i] &&
					enemy2Y+enemy2Height > blockY[i] &&
					enemy2Y < blockY[i]+blockHeight) {
						enemy2X = blockX[i] + blockWidth;
						enemy2Horizon *= -1;
					}
				
				//下面
				if (enemy2Y < blockY[i] + blockHeight &&
					enemy2Y > blockY[i] && 
					enemy2X+enemy2Width > blockX[i] && 
					enemy2X < blockX[i]+blockWidth) {
						enemy2Y = blockY[i] + blockHeight;
						enemy2vertical *= -1;
				}
			}


			//enemy2の行動パターン
			if(enemy2act==true) {
				enemy2act = false;
				actTime = 10;

				Random rand = new Random();
				enemy2pattern = rand.nextInt(3);
			}

			
			switch(enemy2pattern) {
				//上下に移動
				case 0:
				if(actTime>0) {
					enemy2Y += enemy2vertical * 10;
					break;
				} else {
					enemy2act = true;
					break;
				}
				//左右に移動
				case 1:
				if (actTime>0) {
					enemy2X += enemy2Horizon * 10;
					break;
				} else {
					enemy2act = true;
					break;
				}
				//ミサイル発射
				case 2:
				if (actTime>0) {
					if (!isMissileActive) {
						missileX = enemy2X;
						missileY = enemy2Y + enemy2Height/ 2;
						isMissileActive = true;
					}
					break;
				}
				enemy2act = true;
				break;	
			}
			g.drawImage(imgEnemy2, enemy2X, enemy2Y, this);
		}
		//ミサイルの描画
		public void drawMissile(Graphics g) {
			if (isMissileActive) {

				//ミサイルの配置
				g.setColor(Color.red);
				g.fillRect(missileX, missileY, 5, 2);
				missileX -=15;
	
				//ミサイルの当たり判定	
				if( (missileX) >= myX && 
					(missileX <= myX + myWidth) && 
					(missileY >= myY) && 
					(missileY <= myY + myHeight)) {
						System.exit(DISPOSE_ON_CLOSE);
					}
					
				//ミサイルがウィンドウ外に出たときのミサイル初期化
				if(missileX < 0) {
					isMissileActive = false;
				}
			}
		}

		//ジャンプ
		public void jump() {
			if(earth){
				jumptime = 6;
				earth = false;
				dawn = false;
				timer2.start();
			} 

		}

		//ゲームクリアダイアログ
		public void gameclear() {
			timer1.stop();
			if(gameclear) {
				JOptionPane.showMessageDialog((null), "GameClear!");
			}
			System.exit(DISPOSE_ON_CLOSE);
		}
	}
}       