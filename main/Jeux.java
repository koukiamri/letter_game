package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Jeux implements IJeux {
	public static final int NUM_MAX_MOT = 10;

	// afficher pot commun
	public static final int CMD_PC = 0;
	// afficher liste mots
	public static final int CMD_LM = 1;
	// afficher list mots du joureur specifié
	public static final int CMD_LM_USERNAME = 2;
	// former un a partir du pot commun
	public static final int CMD_FM = 3;
	// former un mot a partir d'un autre joueur
	public static final int CMD_FM_USERNAME = 4;
	// finir tour
	public static final int CMD_FT = 5;

	public static final int GAME_ONGOING = 6;
	public static final int GAME_WIN = 7;
	public static final int GAME_LOSE = 8;

	private ServerSocket serverSocket;
	private HashSet<String> nomsCommunsSet;
	private TreeMap<Character, Integer> potCommun;
	private ArrayList<Joueur> joueurList;
	private int count;
	private Joueur curJoueur;
	private static Jeux instance;
	private Scanner scanner;

	private int cmdInt;

	DataInputStream fromClient;
	DataOutputStream toClient;

	private Jeux() {
		potCommun = initPotCommun();
		count = 0;
		curJoueur = null;

	}

	@Override
	public void start() throws IOException {

		// starts the server.
		serverSocket = new ServerSocket(8001);
		// reads Nom Communs from external txt file.
		lireNomsCommuns();

		// Initialize players
		initJoueurs();

		// enters while loops
		// each loop in this while represents a player's turn
		// the loop exits when one player wins the game
		while (true) {
			// get the current player
			curJoueur = joueurList.get(count % joueurList.size());

			// adds two random characters to Pot Commun
			ajouterLettreAleatoireAuPotCommun();
			ajouterLettreAleatoireAuPotCommun();

			// tests whether the current player is a humain or cpu player

			if (curJoueur instanceof JoueurHumain) {
				// current player is a humain player

				// receives output stream from player socket
				toClient = new DataOutputStream(((JoueurHumain) curJoueur).getSocket().getOutputStream());

				// receives input stream from player socket
				fromClient = new DataInputStream(((JoueurHumain) curJoueur).getSocket().getInputStream());

				// flushes player output stream in order to make sure that
				// all data remaining in the output is sent and clear the
				// output for the coming output
				toClient.flush();

				// informs client that the game is ongoing
				toClient.writeInt(GAME_ONGOING);

				// ends turn number to client
				toClient.writeInt((count / joueurList.size()) + 1);

				// sends a formatted String object representing the content
				// of Pot Commun to the client to display
				toClient.writeUTF(getPotCommunCentent());

				// each loop of this while reads client command and calls
				// appropriate method
				// this while loop keeps going until client sends command
				// signaling he want to end his turn
				tourJoueur: while (true) {

					// receives command from client
					try {
						cmdInt = lireCMDJoueur();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}

					// determines which method to call depending on command
					// sent by client
					switch (cmdInt) {
					case CMD_FM:
						formerMot();
						break;
					case CMD_FM_USERNAME:
						formerMotAutreJoueur();
						break;
					case CMD_LM:
						afficherListeMotsCurJoueur();
						break;
					case CMD_LM_USERNAME:
						afficherListeMotsJoueurParUsername();
						break;
					case CMD_PC:
						envoyerPotCommun();
						break;
					case CMD_FT:
						// breaks while loop
						break tourJoueur;
					}
				}
			} else {
				// the current player is CPU player
				boolean b;
				// keep forming words while possible
				do {
					b = curJoueur.formerMot();
				} while (b);
			}
			// check whether current player had won the game
			// if so, then inform all players of game result and end the
			// external loop
			if (curJoueur.checkWin()) {
				declarerGagnant();
				break;
			}
			// increment count variable
			count++;

		}

	}

	@Override
	public void afficherListeMotsJoueurParUsername() throws IOException {

		String username;
		Joueur joueur;

		// reads username from client
		username = fromClient.readUTF();

		// return player having this username if any
		joueur = chercherJoueurParUsername(username);

		if (joueur == null) {
			// if no player has this username then inform the client
			toClient.writeUTF("Aucuen joueur ayant le username : '" + username + "' existe.");
		} else {
			// else return player word list
			toClient.writeUTF("La liste des mots du joueur '" + username + "' : \n" + joueur.getListeMotStr());
		}

	}

	@Override
	public Joueur chercherJoueurParUsername(String username) {
		if (username == null) {
			throw new NullPointerException("L'argument 'username' ne peut pas etre null.");
		}

		for (Joueur j : joueurList) {
			if (username.equals(j.getUsername())) {
				// player who has the username passed in the argument found and
				// returned
				return j;
			}
		}
		// no player who has this username found, therefore return null
		return null;
	}

	@Override
	public void afficherListeMotsCurJoueur() throws IOException {

		// send a formatted String containing the word list of the client
		toClient.writeUTF(curJoueur.getListeMotStr());

	}

	@Override
	public boolean formerMotAutreJoueur() throws IOException {
		String username;
		String mot;
		boolean b = false;

		// read username variable from client
		username = fromClient.readUTF();

		// read mot variable from client
		mot = fromClient.readUTF();

		// search for player that has this username
		Joueur joueur = chercherJoueurParUsername(username);
		if (joueur == null) {
			toClient.writeBoolean(false);
			return false;
		}

		b = ((JoueurHumain) curJoueur).formerMotAutreJoueur(mot, joueur);
		if (b) {
			// if word added successfully then add a random character to Pot
			// Commun
			ajouterLettreAleatoireAuPotCommun();
			toClient.writeBoolean(true);
			return true;
		}

		toClient.writeBoolean(false);
		return false;

	}

	private int lireCMDJoueur() throws IOException {
		int cmd = -1;

		// read command from client
		cmd = fromClient.readInt();
		return cmd;
	}

	// returns and instance of Jeux
	// Singleton pattern
	public synchronized static Jeux getInstance() {
		if (instance == null) {
			instance = new Jeux();
		}
		return instance;
	}

	@Override
	public TreeMap<Character, Integer> initPotCommun() {
		TreeMap<Character, Integer> map = new TreeMap<>();
		for (char c = 'a'; c <= 'z'; c++) {
			map.put(c, 0);
		}
		return map;

	}

	// generate a character between 'a' and 'z' inclusive
	@Override
	public char genererCharAleatoire() {

		int i = (int) ((Math.random() * 100) % 26);

		return (char) ('a' + i);
	}

	@Override
	public void declarerGagnant() throws IOException {
		Joueur gagnant = null;

		// get the player who won the game and store it in gagnant variable
		for (Joueur j : joueurList) {
			if (j.checkWin()) {
				gagnant = j;
			}
		}

		System.out.println("Tour n°" + (count / joueurList.size() + 1));
		System.out.println("Gagant : " + curJoueur.getUsername());
		System.out.println(gagnant.getListeMotStr());

		for (Joueur j : joueurList) {

			// check whether the player referenced by j is a human player
			if (j instanceof JoueurHumain) {
				// get player output stream
				toClient = new DataOutputStream(((JoueurHumain) j).getSocket().getOutputStream());

				if (j.equals(gagnant)) {
					// if player had won, send him win code : GAME_WIN
					// constant
					toClient.writeInt(GAME_WIN);
				} else {
					// if player had lost, send him lose code : GAME_LOSE
					// constant
					toClient.writeInt(GAME_LOSE);
				}
				// send word list of player to the client
				toClient.writeUTF(j.getListeMotStr());
			}

		}
	}

	@Override
	public void initJoueurs() throws IOException {
		JoueurHumain jh;
		JoueurCPU jcpu;

		char charAleatoire;

		TreeMap<Character, Joueur> treemap = new TreeMap<>();

		System.out.println("Veillez saisir le nombre des joueurs");

		scanner = new Scanner(System.in);
		// read the number of players from console input
		int nbJoueurs;
		while (true) {

			if (scanner.hasNextInt()) {
				nbJoueurs = scanner.nextInt();
				if (nbJoueurs >= 2) {
					break;
				}
			} else {
				scanner.next();
			}
			System.out.println("Veillez saisir un entier supérieur ou egal à 2.");
		}
		// nbJoueurs = scanner.nextInt();

		// humain player number
		int countJH = 0;

		for (int i = 0; i < nbJoueurs; i++) {

			System.out.println("Joueur n°" + (i + 1));
			System.out.print("Type : (Humain : h / CPU : c.\n-$");
			String str;
			do {
				str = scanner.next().toLowerCase();
			} while (!str.equals("h") && !str.equals("c"));

			if (str.equals("c")) {
				// if str == c
				// create a cpu player
				jcpu = new JoueurCPU(this, "CPU " + (i + 1));
				// generate a random char
				charAleatoire = genererCharAleatoire();

				// add charAleatoire, jcpu entry to treemap
				treemap.put(charAleatoire, jcpu);
				// add generated character to pot commun
				potCommun.put(charAleatoire, potCommun.get(charAleatoire) + 1);
			} else {
				// if str == h
				// increment countJH variable
				countJH++;
			}
		}

		// loop around humain players
		for (int i = 0; i < countJH; i++) {
			// initialize humain player
			jh = initJoueurHumain();
			// generate random char
			charAleatoire = genererCharAleatoire();
			// add charAleatoire, jcpu entry to treemap
			treemap.put(charAleatoire, jh);
			// add generated character to pot commun
			potCommun.put(charAleatoire, potCommun.get(charAleatoire) + 1);
		}
		// add initialized players to joueurList
		joueurList = new ArrayList<>(treemap.values());
	}

	@Override
	public JoueurHumain initJoueurHumain() throws IOException {
		JoueurHumain jh = null;

		// create new humain player and wait for client to connect to it
		// by using serverSocket.accept()
		jh = new JoueurHumain(this, serverSocket.accept());

		// get output and input streams stream to client in order to
		// communicate with him
		toClient = new DataOutputStream(jh.getSocket().getOutputStream());
		fromClient = new DataInputStream(jh.getSocket().getInputStream());

		// signal to client that connection was successful
		toClient.writeInt(0);
		// read username from client
		jh.setUsername(fromClient.readUTF());

		// return created client
		return jh;
	}

	@Override
	public JoueurCPU initJoueurCPU() {
		// generate username for cpu player
		String username = "Joueur " + (count + 1);
		// create cpu player
		JoueurCPU jcpu = new JoueurCPU(this, username);
		// return created cpu player
		return jcpu;
	}

	@Override
	public String getPotCommunCentent() {
		// This method returns a formatted String representing Pot Commun
		// content

		StringBuilder sb = new StringBuilder(150);

		int i = 0;

		for (Entry<Character, Integer> e : potCommun.entrySet()) {
			// append map key and value to sb
			sb.append(' ').append(e.getKey()).append(": ").append(e.getValue()).append(',');
			if (++i % 5 == 0) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	// -------------------------------------------getters_and_setters

	public TreeMap<Character, Integer> getPotCommun() {
		return potCommun;
	}

	public ArrayList<Joueur> getJoueurList() {
		return joueurList;
	}

	public int getCount() {
		return count;
	}

	public Joueur getCurJoueur() {
		return curJoueur;
	}

	public void setPotCommun(TreeMap<Character, Integer> potCommun) {
		this.potCommun = potCommun;
	}

	public void setJoueurList(ArrayList<Joueur> joueurList) {
		this.joueurList = joueurList;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setCurJoueur(Joueur curJoueur) {
		this.curJoueur = curJoueur;
	}

	@Override
	public boolean peutFormerMot(String mot) {
		HashMap<Character, Integer> map = new HashMap<>();
		for (char c : mot.toCharArray()) {
			if (map.containsKey(c)) {
				map.put(c, map.get(c) + 1);
			} else {
				map.put(c, 1);
			}
		}

		for (char c : map.keySet()) {
			if (potCommun.get(c) < map.get(c)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean peutFormerMot(Character[] chars) {
		HashMap<Character, Integer> map = new HashMap<>();
		for (char c : chars) {
			if (map.containsKey(c)) {
				map.put(c, map.get(c) + 1);
			} else {
				map.put(c, 1);
			}
		}

		for (char c : map.keySet()) {
			if (potCommun.get(c) < map.get(c)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void deleteCharactersFromPotCommun(String s) {
		// go through characters contained in String s and delete them from Pot
		// Commun if possible
		for (char c : s.toCharArray()) {
			if (potCommun.get(c) > 0) {
				potCommun.put(c, potCommun.get(c) - 1);
			}
		}
	}

	@Override
	public void deleteCharactersFromPotCommun(Character[] tab) {
		// go through tab characters and delete them from Pot Commun if possible
		for (char c : tab) {
			if (potCommun.get(c) > 0) {
				potCommun.put(c, potCommun.get(c) - 1);
			}
		}
	}

	@Override
	public void ajouterLettreAleatoireAuPotCommun() {
		// generate random character
		char c = genererCharAleatoire();
		// add generated character to Pot Commun
		potCommun.put(c, potCommun.get(c) + 1);
	}

	@Override
	public boolean formerMot() {
		// call curJoueur.formerMot() method
		boolean b = curJoueur.formerMot();

		if (b) {
			// if the latter call was successful then add a random character to
			// Pot Commun
			ajouterLettreAleatoireAuPotCommun();
		}
		// returns true if operation was successful
		return b;

	}

	@Override
	public void envoyerPotCommun() throws IOException {
		// send a formatted String to client containing Pot Commun content
		toClient.writeUTF(getPotCommunCentent());
	}

	@Override
	public void lireNomsCommuns() {
		// read noms communs from external txt file
		String filename = "d:\\noms_communs.txt";
		BufferedReader br = null;
		FileReader fr = null;

		try {
			String mot;
			br = new BufferedReader(new FileReader(filename));
			this.nomsCommunsSet = new HashSet<>(300);

			while ((mot = br.readLine()) != null) {
				nomsCommunsSet.add(mot);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	// checks whether all the characters of shorter are contained in longer
	public static boolean checkStringcontainsLettersofOtherString(String longer, String shorter) {
		HashMap<Character, Integer> mapLonger = new HashMap<>();
		HashMap<Character, Integer> mapShorter = new HashMap<>();

		for (char c : longer.toCharArray()) {
			if (mapLonger.containsKey(c)) {
				mapLonger.put(c, mapLonger.get(c) + 1);
			} else {
				mapLonger.put(c, 1);
			}
		}

		for (char c : shorter.toCharArray()) {
			if (mapShorter.containsKey(c)) {
				mapShorter.put(c, mapShorter.get(c) + 1);
			} else {
				mapShorter.put(c, 1);
			}
		}

		if (!mapLonger.keySet().containsAll(mapShorter.keySet())) {
			return false;
		}

		for (char c : mapShorter.keySet()) {
			if (mapLonger.get(c) < mapShorter.get(c)) {
				return false;
			}
		}
		return true;

	}

	public HashSet<String> getNomsCommunsSet() {
		return nomsCommunsSet;
	}

	public void setNomsCommunsSet(HashSet<String> nomsCommunsSet) {
		this.nomsCommunsSet = nomsCommunsSet;
	}

}
