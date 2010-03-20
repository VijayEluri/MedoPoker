/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.flow;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import medopoker.log.Log;
import medopoker.logic.Card;
import medopoker.logic.Util.LogList;
import medopoker.logic.Util.LogList.LogElement;

/**
 *
 * @author Nejc
 */
public class PokerCanvas  extends GameCanvas {

	private ClientManager cm;

	private String img_path = "/resources/";
	private Image[][] card_imgs;
	private Image table_img;

	//private Thread thread;
	private Graphics g;
	private Image bg_pattern;
	private Image background;
	int screen_width;
	int screen_height;

	final int CARD_WIDTH = 50;
	final int CARD_HEIGHT = 68;


	public static String[] actions = {"FOLD", "CHECK", "CALL", "RAISE", "SB", "BB"};

	/////////

	public boolean initialized = false;

	public Player[] players = null;
	private Card[] hole = null;
	private Card[] on_table = null;
	private int cards_shown = 0;
	private LogList log;

	private int choice = 0;
	public boolean choices_shown = false;

	private int current_action = -1;
	private int current_player = -1;

	private String pot = null;
	/////////


	public PokerCanvas (ClientManager cm) {
		super(true);
		this.cm = cm;

		Log.notify("PokerCanvas constructor");
		g = getGraphics();

		screen_width = this.getWidth();
		screen_height = this.getHeight();
		//screen_width = 176;
		//screen_height =	220;

		log = new LogList();
		log.append("Game initialized");

		try {
			loadImages();
			createBG();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////////////////

	public void updatePlayers(Player[] ps) {
		players = ps;
		paintMiddle();
		flushGraphics();
	}

	public void newHole(Card[] cs) {
		hole = cs;
		paintHole();
		flushGraphics();
	}

	public void newOnTable(Card[] cs) {
		on_table = cs;
		cards_shown = 3;
		redraw();
	}

	public void showMoreCards() {
		paintCardsToTable(on_table, ++cards_shown);
		flushGraphics();
	}

	public void getAction() {
		choices_shown = true;
		redraw();
	}

	public void currentAction(int a, int p) {
		current_action = a;
		current_player = p;
		paintMiddle();
	}

	public void updatePot(String f) {
		pot = f;
		paintMiddle();
	}

	public void appendToLog(String s) {
		log.append(s);
		paintLog();
		flushGraphics();
	}

	/////////////////////////////////////////////////

	public void redraw() {
		Log.notify("PokerCanvas redraw()");
		if (initialized) {
			paintBG();
			if (hole != null) paintHole();
			if (on_table != null) paintCardsToTable(on_table, cards_shown);
			if (players != null) paintMiddle();
			paintLog();
			if (choices_shown) paintChoices();
			flushGraphics();
		} else {
			g.setColor(0,0,0);
			g.drawString("Loading...", 10, 10, 0);
		}
	}

	private void paintHole() {
		Image c1 = card_imgs[hole[0].getSuit()][hole[0].getRank()];
		Image c2 = card_imgs[hole[1].getSuit()][hole[1].getRank()];
		g.drawImage(c1, 0, screen_height, Graphics.BOTTOM|Graphics.LEFT);
		g.drawImage(c2, c1.getWidth()/3,screen_height, Graphics.BOTTOM|Graphics.LEFT);
		flushGraphics();
	}

	private void paintCardsToTable(Card[] c, int howMany) {
		for (int i=0; i<howMany; i++) {
			if (c[i]==null) break;
			Image img = card_imgs[c[i].getSuit()][c[i].getRank()];
			Log.notify("drawing card "+c[i].toString());
			g.drawImage(img, img.getWidth()/3*i*2, 0, Graphics.TOP|Graphics.LEFT);
			flushGraphics();
		}
	}

	private void paintMiddle() {
		paintBG(CARD_HEIGHT, screen_height-CARD_HEIGHT);
		g.setColor(255,255,255);
		if (pot != null) g.drawString(pot, screen_width/2, screen_height/2+5, Graphics.BASELINE|Graphics.HCENTER);
		for (int i=0; i<players.length; i++) {
			paintPlayer(players[i], i);
			Log.notify("drawing player"+players[i].getName());
		}
		if (current_player != -1) paintPlayerAction(current_action, current_player);
	}

	private void paintPlayer(Player p, int i) {
		// painting player info
		Log.notify("paintPlayer "+p.getName());
		int board_height = screen_height - 2*CARD_HEIGHT;
		int[] anchors = new int[]{4|16, 8|16, 8|32, 4|32};
		Font font_name = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		Font font_money = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		int x = (i>0&&i<3)? screen_width-5 : 5;
		int y = (i<2)? CARD_HEIGHT+5 : CARD_HEIGHT + board_height - 5;
		g.setColor(0,0,0);
		g.setFont(font_name);
		g.drawString(p.getName(), x, y, anchors[i]);
		g.setFont(font_money);
		g.drawString(p.getMoney()+"", x, y+font_name.getHeight()*(i<2?1:-1), anchors[i]);
		flushGraphics();
		Log.notify("all good");
	}

	private void paintPlayerAction(int action, int i) {
		int board_height = screen_height - 2*CARD_HEIGHT;
		int[] anchors = new int[]{4|16, 8|16, 8|32, 4|32};
		int x = (i>0&&i<3)? screen_width-25 : 25;
		int y = (i<2)? CARD_HEIGHT+25 : CARD_HEIGHT + board_height - 25;

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		//g.setColor(2,32,0);
		g.setColor(255,51,36);
		g.drawString(actions[action], x, y, anchors[i]);
		flushGraphics();
	}

	private void paintLog() {
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
		flushGraphics();
	}

	private void createBG() {
		background = Image.createImage(screen_width, screen_height);
		Graphics bg_g = background.getGraphics();
		int img_width = bg_pattern.getWidth();
		int img_height = bg_pattern.getHeight();
		int x = 0;
		int y = 0;
		while (y < screen_height) {
			x = 0;
			while (x < screen_width) {
				bg_g.drawImage(bg_pattern, x, y, 0);
				x+=img_width;
			}
			y+=img_height;
		}

		int x_center = screen_width/2;
		int y_center = screen_height/2;
		bg_g.drawImage(table_img, x_center, y_center, Graphics.VCENTER|Graphics.HCENTER);

	}

	private void paintBG() {
		paintBG(0,screen_height);
	}

	private void paintBG(int from, int to) {
		try {
			Log.notify("drawing bg...");
			g.drawRegion(background, 0, from, screen_width, to-from, 0, 0, from, Graphics.TOP|Graphics.LEFT);
			//g.drawRegion(background, 0, 0, 100, 100, 0, 0, 0, Graphics.TOP|Graphics.LEFT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void paintChoices() {
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
		g.drawString(actions[choice], width/2, screen_height-height/4, Graphics.BASELINE|Graphics.HCENTER);
		Log.notify("drew string");
		flushGraphics();
	}

	private void drawArrow(int x, int y, boolean left) {
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
		redraw();
		//thread = new Thread(this);
		//thread.start();
	}

	private void loadImages() throws IOException {
		card_imgs = new Image[4][13];
		for (int i=0; i<card_imgs.length; i++) {
			for (int j=0; j<card_imgs[i].length; j++) {
				card_imgs[i][j] = Image.createImage(img_path+i+""+j+".png");
			}
		}

		table_img = Image.createImage(img_path+"table.png");
		bg_pattern = Image.createImage("/resources/stripe.png");
	}

	protected void keyPressed(int key) {
		Log.notify("Key pressed: " + key);
		if (choices_shown)
		switch(key) {
			case 52: if(choice!=0) choice--; paintChoices(); break;
			case 54: if(choice!=3) choice++; paintChoices(); break;
			case 53:
				choices_shown = false;
				cm.sendAction(choice+"");
				//paintBG(screen_height-CARD_HEIGHT, screen_height);
				//paintHole();
				//paintLog();
				redraw();
				break;
		}
	}
}
