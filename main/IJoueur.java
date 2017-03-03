package main;

public interface IJoueur {

	/**
	 * This method is used to add a word to the user's word list. depending on
	 * the implementation it will either read the word from user input at
	 * runtime if it is called on a JoueurHumain object or get the word from the
	 * motsCommunsSet if is called on a JoueurCPU object. After obtaining the
	 * word it then calls formerMot(String mot) passing word as parameter and
	 * then returns the result of the latter.
	 * 
	 * @return boolean Returns true if the word obtained
	 */
	boolean formerMot();

	/**
	 * This method adds the String passed as argument to the list of words of
	 * the Joueur
	 * 
	 * @param mot
	 * @return
	 */
	boolean formerMot(String mot);

	/**
	 * This method checks whether the Joueur has formed enough words to win the
	 * game.
	 * 
	 * @return returns true if the Joueur has formed enough words to win the
	 *         game.
	 */
	boolean checkWin();

	/**
	 * This methods returns a formatted String containing a list of the formed
	 * words by the Joueur.
	 * 
	 * @return a formatted String containing a list of the formed words by the
	 *         Joueur.
	 */
	String getListeMotStr();

}
