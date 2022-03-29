package projet2;

import java.util.InputMismatchException;
import java.util.Scanner;

abstract class Joueur {

    enum Choice {USER, RANDOM, LINEAR, SQRT, LOG}

    final String name;
    final EtatJeuJoueur ejj;

    private static int nbJoueurs = 0;

    public Joueur(String name, EtatJeuJoueur etatJeuJoueur) {
        this.name = name.isEmpty() ? defaultName() + (nbJoueurs + 1) : name;
        this.ejj = etatJeuJoueur;
        nbJoueurs++;
    }

    abstract public Integer choisirEntier(Jeu jeu);

    abstract protected String defaultName();

    @Override
    public String toString() {
        return name;
    }

    static class EtatJeuJoueur {

        int lb;
        int ub;
        int nbTentatives = 0;

        public EtatJeuJoueur(int lb, int ub) {
            this.lb = lb;
            this.ub = ub;
        }

    }

    static class LinearIA extends Joueur {

        public LinearIA(String name, EtatJeuJoueur etatJeuJoueur) {
            super(name, etatJeuJoueur);
        }

        @Override
        public Integer choisirEntier(Jeu jeu) {
            return ejj.lb;
        }

        @Override
        final protected String defaultName() {
            return "LinearIA";
        }

    }

    static class LogIA extends Joueur {

        public LogIA(String name, EtatJeuJoueur etatJeuJoueur) {
            super(name, etatJeuJoueur);
        }

        @Override
        public Integer choisirEntier(Jeu jeu) {
            return (ejj.lb + ejj.ub) / 2;
        }

        @Override
        protected String defaultName() {
            return "LogIA";
        }

    }

    static class RandomIA extends Joueur {

        public RandomIA(String name, EtatJeuJoueur etatJeuJoueur) {
            super(name, etatJeuJoueur);
        }

        @Override
        public Integer choisirEntier(Jeu jeu) {
            return jeu.nextInt(ejj);
        }

        @Override
        final protected String defaultName() {
            return "RandomIA";
        }

    }

    static class SqrtIA extends Joueur {

        public SqrtIA(String name, EtatJeuJoueur etatJeuJoueur) {
            super(name, etatJeuJoueur);
        }

        @Override
        public Integer choisirEntier(Jeu jeu) {
            return ejj.lb + jeu.step - 1;
        }

        @Override
        final protected String defaultName() {
            return "SqrtIA";
        }

    }

    static class Utilisateur extends Joueur {

        private final Scanner scanner = new Scanner(System.in);

        public Utilisateur(String name, EtatJeuJoueur etatJeuJoueur) {
            super(name, etatJeuJoueur);
        }

        @Override
        public Integer choisirEntier(Jeu jeu) {
            int guess;
            try {
                guess = scanner.nextInt();
            } catch (InputMismatchException ex) {
                scanner.nextLine();
                System.out.println("Entrée incorrecte. Ré-essaye encore !");
                return null;
            }
            return guess;
        }

        @Override
        final protected String defaultName() {
            return "Joueur";
        }

    }
}
