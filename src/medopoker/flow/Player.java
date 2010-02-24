/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.flow;

import medopoker.logic.Card;
import medopoker.network.Device;

/**
 *
 * @author Martin
 */
public class Player {
	private Device device;
	private String name;
	private float money;
	private Card[] currentHole;

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

	public void dealHole(Card[] hole) {
		currentHole = hole;
	}

	public Card[] getHole() {
		return currentHole;
	}
}
