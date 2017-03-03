package main;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		//get Jeux instance
		Jeux jeux = Jeux.getInstance();
		//start game
		try {
			jeux.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}