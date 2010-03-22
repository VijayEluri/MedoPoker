package medopoker.flow;

import medopoker.logic.Card;

/**
 *
 * @author Nejc
 */
public class ServerPlayer extends Player {

	private Card[] currentHole;
	private boolean ingame = true;
	private float money_in = 0;
	
	public ServerPlayer(String n, float f) {
		super(n,f);
	}

	public void dealHole(Card[] hole) {
		currentHole = hole;
	}

	public Card[] getHole() {
		return currentHole;
	}

	public void setIngame(boolean b) {
		ingame = b;
	}

    public boolean isIngame() {
        return ingame;
    }

	public float getMoneyIn() {
		return money_in;
	}

	public void put(float f) {
		money_in+=f;
		money-=f;
	}

	public void win(float f) {
		money+=f;
	}

	public void loose() {
		money_in = 0;
	}

	public void execAction(int a) {
		switch(a) {
			case 0:
				setIngame(false); break;
			case 1:
				break;
		}
	}

	public void execAction(int a, float f) {
		switch(a) {
			case 2: break;
				
		}
	}

}
