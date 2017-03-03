package main;

public class JoueurCPU extends Joueur {
	

	public JoueurCPU(IJeux jeux, String username) {
		super(jeux, username);

	}

	@Override
	public boolean formerMot() {
		boolean b;
		for (String mot : jeux.getNomsCommunsSet()) {
			b = formerMot(mot);
			if (b) {
				return true;
			}
		}
		return false;
	}

}
