package main;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;

public class JeuxClassTest {

	TreeMap<Character, Integer> potCommun;

	HashSet<String> nomsCommuns;

	Jeux jeux;

	JoueurCPU joueurCPU1;
	JoueurCPU joueurCPU2;

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

		jeux = Jeux.getInstance();

		jeux.setNomsCommunsSet(nomsCommuns);
		jeux.setPotCommun(potCommun);
		jeux.setJoueurList(new ArrayList<Joueur>());
		joueurCPU1 = new JoueurCPU(jeux, "cpu 1");
		joueurCPU2 = new JoueurCPU(jeux, "cpu 2");
		jeux.getJoueurList().add(joueurCPU1);
		jeux.getJoueurList().add(joueurCPU2);

		jeux.setCurJoueur(joueurCPU1);
	}

	@Test
	public void checkStringcontainsLettersofOtherStringTest() {
		boolean bTrue = Jeux.checkStringcontainsLettersofOtherString("this is a word", "word this");
		boolean bFalse = Jeux.checkStringcontainsLettersofOtherString("This is a word", "this this");

		assertEquals(bTrue, true);
		assertEquals(bFalse, false);

	}

	@Test
	public void formerMotTest() {

		boolean b = jeux.formerMot();

		assertEquals(true, b);

	}

}
