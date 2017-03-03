package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class JoueurHumain extends Joueur {

	protected Socket socket;

	public JoueurHumain(IJeux jeux, Socket socket) {
		this(jeux, "", socket);

	}

	public JoueurHumain(IJeux jeux, String username, Socket socket) {
		super(jeux, username);
		if (socket == null) {
			throw new NullPointerException("Argument 'socket' ne peut pas etre null.");
		}
		this.socket = socket;
		try {
			this.socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method obtains the word from the client end and then calls
	 * formerMot(String mot) used the word obtained as argument
	 * 
	 * return true if the word was added successfully to the user word List
	 */
	@Override
	public boolean formerMot() {
		DataInputStream fromClient;
		DataOutputStream toClient;
		boolean b = false;

		try {
			fromClient = new DataInputStream(socket.getInputStream());
			toClient = new DataOutputStream(socket.getOutputStream());

			String mot = fromClient.readUTF();
			b = formerMot(mot);

			toClient.writeBoolean(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public boolean formerMotAutreJoueur(String mot, Joueur joueur) {

		if (mot == null) {
			throw new NullPointerException("Argument 'mot' ne peut pas etre null.");
		}

		if (joueur == null) {
			throw new NullPointerException("Argument 'joueur' ne peut pas etre null.");
		}

		if (!jeux.getNomsCommunsSet().contains(mot)) {
			return false;
		}

		for (String str : joueur.motList) {

			if (Jeux.checkStringcontainsLettersofOtherString(mot, str)) {
				ArrayList<Character> list = new ArrayList<>();

				for (char c : mot.toCharArray()) {
					list.add(c);
				}

				for (Character c : str.toCharArray()) {
					list.remove(c);
				}

				Character[] a = new Character[list.size()];

				a = list.toArray(a);
				if (jeux.peutFormerMot(a)) {
					joueur.motList.remove(str);
					jeux.deleteCharactersFromPotCommun(a);
					this.motList.add(mot);
					this.formerMot(mot);
					return true;
				}
			}
		}
		return false;
	}

}
