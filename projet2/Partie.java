/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//package projet2;

package projet2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author admin
 */

public class Partie {

    static final int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS;
    static final Predicate<String> userPred = Pattern.compile("^u(se?r)?", flags).asMatchPredicate();
    static final Predicate<String> randPred = Pattern.compile("^r(a?nd(om)?)?", flags).asMatchPredicate();
    static final Predicate<String> linearPred = Pattern.compile("^li?n(ea)?r?", flags).asMatchPredicate();
    static final Predicate<String> sqrtPred = Pattern.compile("^s(qrt)?", flags).asMatchPredicate();
    static final Predicate<String> logPred = Pattern.compile("^lo?g", flags).asMatchPredicate();
    static final List<Predicate<String>> preds = asList(userPred, randPred, linearPred, sqrtPred, logPred);
    static final List<BiFunction<String, Joueur.EtatJeuJoueur, Joueur>> inits = List.of(
            Joueur.Utilisateur::new,
            Joueur.RandomIA::new,
            Joueur.LinearIA::new,
            Joueur.SqrtIA::new,
            Joueur.LogIA::new
    );

    public static void main(String[] args) {

        int choice = 0;
        int bound = 100;
        int i = 0;
        if (args.length > 0) {
            String arg = args[0];
            try {
                bound = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
            while (i < Partie.inits.size() && !preds.get(i).test(arg)) i++;
            choice = i == 5 ? 0 : i;
        }
        List<Integer> choices = List.of(choice);
        Jeu jeu = new Jeu(bound, choices);
        jeu.jouerPartie();
    }

}

class Jeu {

    final int answer;
    final MultiJoueur joueurs;
    int step;
    final Random r = new Random();

    public Jeu(int initUB, List<Integer> choices) {
        final int lb = 1;
        final int ub = Math.max(1, initUB);
        this.step = (int) Math.sqrt(ub);
        this.answer = r.nextInt(ub + 1);
        List<Joueur> joueurs = choices.stream()
                .map(Partie.inits::get)
                .map(f -> f.apply("", new Joueur.EtatJeuJoueur(lb, ub)))
                .collect(Collectors.toList());
        this.joueurs = new MultiJoueur(joueurs);
    }

    public int nextInt(Joueur.EtatJeuJoueur etatJeuJoueur) {
        return etatJeuJoueur.lb + r.nextInt(etatJeuJoueur.ub - etatJeuJoueur.lb + 1);
    }

    public void jouerPartie() {
        
        Integer guess = 0;
        List<Integer> resultats = Stream.generate(() -> 0)
                .limit(joueurs.nbJoueurs())
                .collect(Collectors.toList());
        
        List<Integer> gagnants;

        do {
            int i = 0;
            for (Joueur joueur : joueurs) {

                System.out.println(joueur.name + " : Entre un entier compris entre " + joueur.ejj.lb + " et " + joueur.ejj.ub + ":");
                guess = joueur.choisirEntier(this);
                while (guess == null || guess < joueur.ejj.lb || guess > joueur.ejj.ub) {
                    if (guess != null)
                        System.out.println(guess + " n'est pas compris entre " + joueur.ejj.lb + " et " + joueur.ejj.ub + ".");
                    System.out.println("Entre un entier compris entre " + joueur.ejj.lb + " et " + joueur.ejj.ub + ":");
                    guess = joueur.choisirEntier(this);
                }
                joueur.ejj.nbTentatives++;
                if (guess < answer) {
                    System.out.println("Trop petit !");
                    joueur.ejj.lb = guess + 1;
                }
                if (guess > answer) {
                    System.out.println("Trop grand !");
                    joueur.ejj.ub = guess - 1;
                    step = (int) Math.sqrt(step);
                }
                
                resultats.set(i, guess);
                i++;
                
            }
            
            gagnants = IntStream.range(0, joueurs.nbJoueurs())
                    .filter(j -> resultats.get(j) == answer).boxed().collect(Collectors.toList());
           
        } while (gagnants.isEmpty());

        for (Integer gagnant : gagnants) {
            Joueur joueur = joueurs.getJoueur(gagnant);
            System.out.println("Bravo " + joueur + " !!!");
            System.out.println(guess + " est la bonne réponse !!!");
            System.out.println("réponse trouvée après " + joueur.ejj.nbTentatives + " tentative(s).");
        }
        
    }


}

class MultiJoueur implements Iterable<Joueur> {
    private final List<Joueur> listJoueur;

    public MultiJoueur(List<Joueur> listJoueur) {
        this.listJoueur = Collections.unmodifiableList(listJoueur);
    }

    public int nbJoueurs() {
        return listJoueur.size();
    }

    public Joueur getJoueur(int i) {
        return listJoueur.get(i);
    }

    @Override
    public Iterator<Joueur> iterator() {
        return listJoueur.iterator();
    }
}
