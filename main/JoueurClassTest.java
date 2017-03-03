package main;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class JoueurClassTest {

	TreeMap<Character, Integer> potCommun;
	HashSet<String> nomsCommuns;
	IJeux jeux;
	
	Jeux jeuxImpl = Jeux.getInstance();
	
	JoueurHumain joueurHumain1;
	JoueurHumain joueurHumain2;

	@Before
	public void init() {

		nomsCommuns = new HashSet<>(Arrays.asList("aide", "chef", "enfant", "garde", "gauche", "geste", "gosse",
				"livre", "merci", "mort", "ombre", "part", "poche", "professeur"));

		potCommun = new TreeMap<>();
		for (char c = 'a'; c <= 'z'; c++) {
			potCommun.put(c, 0);
		}

		potCommun.put('o', 2);
		potCommun.put('m', 2);
		potCommun.put('b', 2);
		potCommun.put('r', 2);
		potCommun.put('e', 2);
		potCommun.put('s', 2);
		potCommun.put('u', 2);

		jeux = mock(IJeux.class);
		when(jeux.getPotCommun()).thenReturn(potCommun);
		when(jeux.getNomsCommunsSet()).thenReturn(nomsCommuns);
		
		
	}

	@Test
	public void joueurFormerMotTest() {
		Joueur joueur = new JoueurCPU(jeux, "username");
		boolean bTrue = joueur.formerMot("ombre");
		boolean bFalse = joueur.formerMot("levre");

		assertEquals(true, bTrue);
		assertEquals(1, joueur.getMotList().size());
		assertEquals(false, bFalse);
		assertEquals(1, joueur.getMotList().size());
	}

	@Test
	public void joueurCheckWinTest() {
		Joueur joueurWinner = new JoueurCPU(jeux, "winner");
		Joueur joueurLoser = new JoueurCPU(jeux, "loser");

		ArrayList<String> listeMotsWinner = new ArrayList<>(Arrays.asList("aide", "chef", "enfant", "garde", "gauche",
				"geste", "gosse", "livre", "merci", "mort", "ombre", "part", "poche", "professeur"));
		ArrayList<String> listeMotsLoser = new ArrayList<>(
				Arrays.asList("aide", "chef", "enfant", "garde", "gauche", "geste", "gosse"));

		joueurWinner.setMotList(listeMotsWinner);
		joueurLoser.setMotList(listeMotsLoser);

		assertEquals(true, joueurWinner.checkWin());
		assertEquals(false, joueurLoser.checkWin());

	}

	@Test
	public void joueurCPUFormerMotTest() {
		JoueurCPU joueurCPU = new JoueurCPU(jeux, "username");
		assertEquals(true, joueurCPU.formerMot());
	}
	
	@Test
	public void formerAutreMotTest() {
		joueurHumain1 = new JoueurHumain(jeuxImpl,new Socket());
		joueurHumain2 = new JoueurHumain(jeuxImpl, new Socket());
		joueurHumain2.getMotList().add("prof");

		jeuxImpl.setCurJoueur(joueurHumain1);
		jeuxImpl.setPotCommun(potCommun);
		jeuxImpl.setNomsCommunsSet(nomsCommuns);
		
		boolean b = joueurHumain1.formerMotAutreJoueur("professeur", joueurHumain2);
		
		assertEquals(true, b);
	}

}
