package medopoker.flow;

import medopoker.logic.Card;
import medopoker.network.Device;

/**
 *
 * @author Nejc
 */
public class Player {
	private Device device;
	private String name;
	protected float money;

	public Player(String n, float m) {
		name = n;
		money = m;
	}

	public String getName() {
		return name;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float m) {
		money = m;
	}
}
