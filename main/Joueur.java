package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public abstract class Joueur implements IJoueur {

	protected IJeux jeux;

	// Le username du joueur
	protected String username;
	// arrayList containing the words formed by this Joueur.
	protected ArrayList<String> motList;

	public Joueur(IJeux jeux, String username) {
		this.jeux = jeux;
		this.username = username;
		this.motList = new ArrayList<>(10);
	}

	@Override
	public abstract boolean formerMot();

	@Override
	public boolean formerMot(String mot) {

		if (mot == null) {
			throw new NullPointerException("Argument 'mot' ne peut pas etre null.");
		}

		if (!jeux.getNomsCommunsSet().contains(mot)) {
			return false;
		}

		HashMap<Character, Integer> map = new HashMap<>();
		TreeMap<Character, Integer> pot = jeux.getPotCommun();

		for (char c : mot.toCharArray()) {
			if (map.containsKey(c)) {
				map.put(c, map.get(c) + 1);
			} else {
				map.put(c, 1);
			}
		}

		for (char c : map.keySet()) {
			if (map.get(c) > pot.get(c)) {
				return false;
			}
		}

		for (char c : map.keySet()) {
			pot.put(c, pot.get(c) - map.get(c));
		}

		this.motList.add(mot);
		return true;
	}

	@Override
	public boolean checkWin() {
		int nbMot = 0;
		for (String mot : motList) {
			nbMot += mot.trim().split(" ").length;
		}
		return (nbMot >= Jeux.NUM_MAX_MOT);
	}

	@Override
	public String getListeMotStr() {
		int i = 0;
		if ((this.motList == null) || (this.motList.isEmpty())) {
			return "";
		}
		StringBuilder sb = new StringBuilder(100);

		for (String str : this.motList) {
			sb.append(str).append(',');
			if (++i % 5 == 0) {
				sb.append('\n');
			}
		}
		sb.setCharAt(sb.lastIndexOf(","), '.');

		return new String(sb);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Joueur other = (Joueur) obj;
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	// -----------------------------------------getters_and_users

	public String getUsername() {
		return username;
	}

	public ArrayList<String> getMotList() {
		return motList;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMotList(ArrayList<String> motList) {
		this.motList = motList;
	}

}
