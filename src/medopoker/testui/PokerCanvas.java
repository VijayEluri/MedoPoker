/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.testui;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import medopoker.log.Log;
import medopoker.logic.Card;
import medopoker.logic.Util.LogList;
import medopoker.logic.Util.LogList.LogElement;
import medopoker.flow.Player;

/**
 *
 * @author Nejc
 */
public class PokerCanvas  extends GameCanvas implements Runnable {

	private String img_path = "/resources/";
	private Image[][] card_imgs;
	private Image table_img;

	private Thread thread;
	private Graphics g;
	private Image bg;
	int screen_width;
	int screen_height;

	Card[] on_table;
	Card[] hole;
	private LogList log;
	
	final int CARD_WIDTH = 50;
	final int CARD_HEIGHT = 68;


	public PokerCanvas () {
		super(true);
		Log.notify("PokerCanvas constructor");
		g = getGraphics();

		screen_width = this.getWidth();
		screen_height = this.getHeight();
		screen_width = 176;
		screen_height =	220;

		on_table = new Card[]{(new Card(0,1)),(new Card(1,6)),(new Card(0,2)),(new Card(3,12)),(new Card(2,9))};
		hole = new Card[]{(new Card(2,12)), (new Card(1,12))};


		log = new LogList();
		log.append("First!");
		log.append("Second!");
		log.append("Third!");
		log.append("Fourth!");
		log.append("Nejc won the pot!");
		log.append("Mistake! He didn't!");
		log.append("End of Log");
		
		try {
			loadImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void paintEverything() {
		Log.notify("PokerCanvas paintEverything()");
		paintBG();
		paintHole(hole);
		paintCardsToTable(on_table);
		paintLog();
		//paintTable();
		paintPlayer(new Player("Test1", 12), 0);
		paintPlayer(new Player("Test2", 14), 1);
		paintPlayer(new Player("Test3", 8), 2);
		paintPlayer(new Player("Test4", 78), 3);
		paintPlayerAction(1, 0);
		paintPlayerAction(3, 2);
		flushGraphics();
	}

	public void paintHole(Card[] c) {
		Image c1 = card_imgs[c[0].getSuit()][c[0].getRank()];
		Image c2 = card_imgs[c[1].getSuit()][c[1].getRank()];
		g.drawImage(c1, 0, screen_height, Graphics.BOTTOM|Graphics.LEFT);
		g.drawImage(c2, c1.getWidth()/3,screen_height, Graphics.BOTTOM|Graphics.LEFT);
	}

	public void paintCardsToTable(Card[] c) {
		for (int i=0; i<c.length; i++) {
			if (c[i]==null) break;
			Image img = card_imgs[c[i].getSuit()][c[i].getRank()];
			Log.notify("drawing card");
			g.drawImage(img, img.getWidth()/3*i*2, 0, Graphics.TOP|Graphics.LEFT);
			//g.drawImage(img, screen_width/5*i, 0, Graphics.TOP|Graphics.LEFT);
		}
	}

	public void paintPlayer(Player p, int i) {
		// painting player info
		int board_height = screen_height - 2*CARD_HEIGHT;
		int[] anchors = new int[]{4|16, 8|16, 8|32, 4|32};
		Font font_name = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		Font font_money = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		int x = (i>0&&i<3)? screen_width-5 : 5;
		int y = (i<2)? CARD_HEIGHT+5 : CARD_HEIGHT + board_height - 5;
		g.setFont(font_name);
		g.drawString(p.getName(), x, y, anchors[i]);
		g.setFont(font_money);
		g.drawString(p.getMoney()+"", x, y+font_name.getHeight()*(i<2?1:-1), anchors[i]);
	}

	public void paintPlayerAction(int action, int i) {
		String[] actions = {"FOLD", "CHECK", "CALL", "RAISE"};
		int board_height = screen_height - 2*CARD_HEIGHT;
		int[] anchors = new int[]{4|16, 8|16, 8|32, 4|32};
		int x = (i>0&&i<3)? screen_width-25 : 25;
		int y = (i<2)? CARD_HEIGHT+25 : CARD_HEIGHT + board_height - 25;

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		//g.setColor(2,32,0);
		g.setColor(255,51,36);
		g.drawString(actions[action], x, y, anchors[i]);
	}

	public void paintLog() {
		int x_offset = CARD_WIDTH + CARD_WIDTH/3 + 3;
		int y_offset = screen_height - CARD_HEIGHT;
		int width = screen_width-x_offset-2;
		int height = screen_height-y_offset;

		g.setColor(255, 255, 255);
		g.fillRect(x_offset, y_offset, width, height);
		g.setColor(0,0,0);
		g.drawRect(x_offset, y_offset, width, height);

		Font f = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		g.setFont(f);

		int line = height/f.getHeight();
		Log.notify("HEIGHT: "+line);
		LogElement e = log.getFirst();
		do {
			g.drawString(e.value(), x_offset+2, y_offset+5+(line*f.getHeight()), Graphics.BOTTOM|Graphics.LEFT);
			line--;
		} while (((e = e.getNext()) != null) && line!=0 );
	}

	public void paintBG() {
		Log.notify("PokerCanvas paintBG()");
		int img_width = bg.getWidth();
		int img_height = bg.getHeight();
		int x = 0;
		int y = 0;
		while (y < screen_height) {
			x = 0;
			while (x < screen_width) {
				g.drawImage(bg, x, y, 0);
				x+=img_width;
			}
			y+=img_height;
		}
		paintTable();
	}

	public void paintTable() {
		int board_height = screen_height - 2*CARD_HEIGHT;
		int x_center = screen_width/2;
		int y_center = CARD_HEIGHT + (board_height/2);
		g.drawImage(table_img, x_center, y_center, Graphics.VCENTER|Graphics.HCENTER);
	}

	public void paintChoices() {
		int y_offset = screen_height - CARD_HEIGHT/2;
		int width = screen_width-1;
		int height = CARD_HEIGHT/2+5;

		g.setColor(2,32,0);
		g.fillRect(0, y_offset, width, height);
		g.setColor(200,200,200);
		g.drawRect(0, y_offset, width, height);
		drawArrow(5, y_offset+CARD_HEIGHT/4, true);
		drawArrow(width-5, y_offset+CARD_HEIGHT/4, false);

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
		Log.notify("got font");
		g.drawString("CALL", width/2, screen_height-height/4, Graphics.BASELINE|Graphics.HCENTER);
		Log.notify("drew string");
	}
	
	public void drawArrow(int x, int y, boolean left) {
		if (left) {
			g.drawLine(x, y, x+2, y-2);
			g.drawLine(x, y, x+2, y+2);
		} else {
			g.drawLine(x, y, x-2, y-2);
			g.drawLine(x, y, x-2, y+2);
		}
	}

	protected void showNotify() {
		Log.notify("showNotify()");
		paintEverything();
		thread = new Thread(this);
		thread.start();
	}

	private void loadImages() throws IOException {
		card_imgs = new Image[4][13];
		for (int i=0; i<card_imgs.length; i++) {
			for (int j=0; j<card_imgs[i].length; j++) {
				card_imgs[i][j] = Image.createImage(img_path+i+""+j+".png");
			}
		}

		table_img = Image.createImage(img_path+"table.png");
		bg = Image.createImage("/resources/stripe.png");
	}

	public void run() {
		Log.notify("Run()");
		
		try {
			Thread.sleep(1000);
			log.append("Haha!");
			paintLog(); flushGraphics();
			Thread.sleep(1000);
			log.append("Dela!");
			paintLog(); flushGraphics();
			Thread.sleep(1000);
			paintChoices(); flushGraphics();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}


}
