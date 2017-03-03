package main;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

public interface IJeux {

	/**
	 * This method starts the game
	 * @throws IOException 
	 */
	void start() throws IOException;

	/**
	 * This method returns the an initialized Pot Commun.
	 * 
	 * @return the initialized Pot Commun.
	 */
	TreeMap<Character, Integer> initPotCommun();

	/**
	 * This Method returns the Pot Commun.
	 * 
	 * @return the Pot Commun.
	 */
	TreeMap<Character, Integer> getPotCommun();

	/**
	 * This method returns a Set object containing Mots Communs.
	 * 
	 * @return a Set object containing Mots Communs
	 */
	Set<String> getNomsCommunsSet();

	/**
	 * This method generates a random char between 'a' and 'z' inclusive and
	 * returns it.
	 * 
	 * @return randomly generated char.
	 */
	char genererCharAleatoire();

	/**
	 * This method declares the winner of the game if any and informs Humain
	 * players whether they won or lost the game.
	 * @throws IOException 
	 */
	void declarerGagnant() throws IOException;

	/**
	 * This method initializes the players
	 * @throws IOException 
	 */
	void initJoueurs() throws IOException;

	/**
	 * This method initializes a humain player via user input received through a
	 * socket.
	 * 
	 * @return the player as specified by the client.
	 * @throws IOException 
	 */
	JoueurHumain initJoueurHumain() throws IOException;

	/**
	 * This method creates and returns a CPU player generated by the
	 * application.
	 * 
	 * @return the created CPU player.
	 */
	JoueurCPU initJoueurCPU();

	/**
	 * This method returns a formatted String object representing the content of
	 * the Pot Commun ready for display in the Command Line.
	 * 
	 * @return
	 */
	String getPotCommunCentent();

	/**
	 * This method calls JoueurHumain.formerMot() or JoueurCPU.formerMot()
	 * depending on Runtime and returns the result of the latter methods.
	 * 
	 * @return true if word successfully add, otherwise, it returns false.
	 */
	boolean formerMot();

	/**
	 * This method allows for forming a word by taking a word from another
	 * player and adding some characters to it from Pot Commun.
	 * 
	 * @return rue if word successfully add, otherwise, it returns false.
	 * @throws IOException 
	 */
	boolean formerMotAutreJoueur() throws IOException;

	/**
	 * This method calls the getListeMotStr() of the current player and sends
	 * its result to the client representing the player to display
	 * @throws IOException 
	 */
	void afficherListeMotsCurJoueur() throws IOException;

	/**
	 * This method takes a username as an argument and returns the player that
	 * has that username if any.
	 * 
	 * @param username
	 *            the username of the sought-after player.
	 * @return return the player that has the username passed in argument if it
	 *         exists, returns null otherwise.
	 */
	Joueur chercherJoueurParUsername(String username);

	/**
	 * This method reads a username from the client machine and sends back a
	 * formatted String containing the word list of the player who has this
	 * username if it exists otherwise it returns a message informing the client
	 * that no such player exist.
	 * @throws IOException 
	 */
	void afficherListeMotsJoueurParUsername() throws IOException;

	/**
	 * This methods checks whether the word passed in argument can be created
	 * using the characters that exist in the Pot Commun.
	 * 
	 * @param mot
	 * @return true if word can be formed.
	 */
	boolean peutFormerMot(String mot);

	/**
	 * This method checks whether characters passed in the chars argument exist
	 * in the Pot Commun.
	 * 
	 * @param chars
	 * @return true if all the elements in chars argument exist in the Pot
	 *         Commun.
	 */
	boolean peutFormerMot(Character[] chars);

	/**
	 * This method deletes all the characters that exist in String s passed as
	 * parameter from Pot Commun.
	 * 
	 * @param s
	 */
	void deleteCharactersFromPotCommun(String s);

	/**
	 * This method deletes all the characters that exist in tab array passed as
	 * parameter from Pot Commun.
	 * 
	 * @param s
	 */
	void deleteCharactersFromPotCommun(Character[] tab);

	/**
	 * This method adds a random character to Pot Commun.
	 */
	void ajouterLettreAleatoireAuPotCommun();

	/**
	 * This methods sends a formatted String representing Pot Commun to the
	 * client to display.
	 * @throws IOException 
	 */
	void envoyerPotCommun() throws IOException;

	/**
	 * This method reads the "Mots communs" present in external txt file and
	 * then adds them to the list of "Noms Commun" List.
	 */
	void lireNomsCommuns();

}
