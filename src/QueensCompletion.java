/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.Solver;
import org.chocosolver.util.ESat;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author cb454580
 */
public class QueensCompletion extends AbstractProblem {
    
    int n; //taille de l'echiquier -> ex : n=8, tableau 8*8
    int k; //nb reines à placer
    int sol[]; //contient la pré-solution
    IntVar[] vars;
    
    /**
     * Constructeur de la classe
     * @param N taille de l'echiquier
     * @param K nombres de reines à placer
     */
    QueensCompletion(int N,int K){
        n = N;
        k = K;
        sol = new int[N];
    }
    
     // A compléter
    public void configureSearch()
    {

    }

    /**
     * Creation du Solver
     */
    public void createSolver()
    {
        solver = new Solver("Queens Completion");
    }

    /**
     * Permet de soit trouver une solution, soit de trouver toutes les solutions
     */
    public void solve()
    {
        //solver.findAllSolutions();
        solver.findSolution();
    }

   /**
    * Solveur -> Ajout contraintes
    */
    public void buildModel() {
            // Création du tableau contenant les références des variables du
            // problème.
            vars = new IntVar[n];
            // Création des variables ayant toutes pour domaine 1..n.
            for (int i = 0; i < vars.length; i++) {
                    if (sol[i] == 0) {
                            vars[i] = VariableFactory.enumerated("Q_" + i, 1, n, solver);
                    } else {
                            vars[i] = VariableFactory.fixed("Q_" + i, sol[i], solver);
                    }
            }
                    
            // Contrainte imposant que les variables aient toutes des valeurs différentes.
            solver.post(IntConstraintFactory.alldifferent(vars, "AC"));
            // Technique de filtrage utilisée (Arc Consistency).
            for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                            int k = j - i;
                            // Contraintes imposant qu’une paire de reine ne doit pas se trouver sur une même diagonale.
                            solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", -k));
                            solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", k));
                    }
            }
    }
    
    /**
     * Fonction d'affichage
     */
    public void prettyOut()
    {
        if (solver.isFeasible().equals(ESat.TRUE)) {
            System.out.println("Une solution :");
                for(int i = 0; i<vars.length; i++){
                    for (int j = 0; j < vars.length; j++) {
                        if(j == solver.getSolutionRecorder().getLastSolution().getIntVal(vars[i])-1){
                            System.out.print("|1");
                        }else{
                            System.out.print("|0");
                        }
                    }
                    System.out.print("|\n");
                } 
        } else {
                System.out.println("Aucune solution");
        }
        
    }
    
    public int trouver(){
        if (solver.isFeasible().equals(ESat.TRUE))
            return 1;
        else
            return 0;
    }
    
    public String res(int i){
        String res = "Test n°"+i+" /N = "+n+" / K = "+k+" : ";
        
        if (solver.isFeasible().equals(ESat.TRUE))
            res +=  "Solution trouvée";
        else
            res += "Pas de solution trouvé";
        
        return res+"\n";
    }
     
        
    /**
     * Genere le tableau et placer les K reines en respectant les contraintes
     */
    public boolean generate()
    {
        //initialisation des variables
        int reinePlacer = 0;
        int grille[][] = new int[n][n];
        boolean generated = true;
        
        long startTime = System.currentTimeMillis(); //fetch starting time
        long maxTime = 10000;
        
        while(reinePlacer!=k && (System.currentTimeMillis()-startTime)< maxTime){
            //initialise la grille
            int grid[][] = new int[n][n] ;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    grid[i][j] = 0;
                }
            }

            //initialise deux tableaux permettant de savoir si une reine se trouve deja sur cette ligne/colonne
            ArrayList rdmLine = new ArrayList();
            ArrayList rdmColumn = new ArrayList();
            for (int i = 0; i < n; i++) {
                rdmLine.add(i);
                rdmColumn.add(i);
            }


            //crée une grille avec k queens placées sur des lignes differentes et colonnes differentes
            Random randomGenerator = new Random();
            //System.out.println("Generation");
            boolean impossible = false;
            //pour chaque reine a placer
            for (int i = 0; i < k; i++) {
                boolean notok = true;
                int ligneCourante = -1;
                int colonneCourante = -1;
                int nbDeplacement = 0;

                while(notok){
                    int rdm;
                    int rdm2;
                    // tire au sort colonne et ligne parmi les lignes et colonnes non utilisées
                    if(colonneCourante == -1){
                        int rdmTL = randomGenerator.nextInt(rdmLine.size());
                        rdm = (int) rdmLine.get(rdmTL);

                        int rdmTC = randomGenerator.nextInt(rdmColumn.size());
                        rdm2 = (int) rdmColumn.get(rdmTC);
                    // on decale d'une case vers la droite (permutation)   
                    }else{
                        rdm = ligneCourante;
                        rdm2 = (colonneCourante+1) % n;
                        nbDeplacement++;
                    }
                    // si on fait plus de n deplacement on a testé toutes les cases de la ligne
                    if(nbDeplacement<n && rdmColumn.contains(rdm2)){
                        grid[rdm][rdm2] = 1; 
                        boolean diagBD = true; //Bas droite
                        boolean diagBG = true; //Bas gauche
                        boolean diagHD = true; //Haut droite 
                        boolean diagHG = true; //Haut gauche
                        boolean probleme = false;
                        int compteur = 1;

                        while(diagBD || diagBG || diagHD || diagHG){

                            //diagHG
                            if(rdm-compteur>=0 && rdm-compteur<n && rdm2-compteur>=0 && rdm2-compteur<n){
                                if(grid[rdm-compteur][rdm2-compteur] == 1){
                                    probleme = true;
                                }
                            }
                            else{
                                diagHG = false;
                            }

                            //diagHD
                            if(rdm-compteur>=0 && rdm-compteur<n && rdm2+compteur>=0 && rdm2+compteur<n){
                                if(grid[rdm-compteur][rdm2+compteur] == 1){
                                    probleme = true;
                                }
                            }
                            else{
                                diagHD = false;
                            }

                            //diagBG
                            if(rdm+compteur>=0 && rdm+compteur<n && rdm2-compteur>=0 && rdm2-compteur<n){
                                if(grid[rdm+compteur][rdm2-compteur] == 1){
                                    probleme = true;
                                }
                            }
                            else{
                                diagBG = false;
                            }

                            //diagBD
                            if(rdm+compteur>=0 && rdm+compteur<n && rdm2+compteur>=0 && rdm2+compteur<n){
                                if(grid[rdm+compteur][rdm2+compteur] == 1){
                                    probleme = true;
                                }
                            }
                            else{
                                diagBD = false;
                            }

                            compteur++;
                        }
                        //si il n'y a aucun conflit, on ajoute la reine et on retire les indexes de ligne et colonne des tableaux rdmLine et rdmColumn
                        if(!probleme){
                            notok = false;
                            rdmLine.remove(rdmLine.indexOf(rdm));
                            rdmColumn.remove(rdmColumn.indexOf(rdm2));
                            reinePlacer++;
                        }
                        else{
                            grid[rdm][rdm2] = 0;
                            colonneCourante = rdm2;
                            ligneCourante= rdm;
                        }

                    }else{
                        colonneCourante = -1;
                        ligneCourante = -1;
                        nbDeplacement = 0;
                        impossible = true;
                        break;
                    }                           
                }
                
                if(impossible)
                    break;

            }
            
            if(reinePlacer == k)
                for (int i = 0; i < n; i++) {
                    String s = "|";
                    for (int j = 0; j < n; j++) {
                        s += grid[i][j]+"|";
                    }
                    System.out.println(s);
                    grille = grid;
                }
            else
                reinePlacer = 0;
            
        }
        
        if((System.currentTimeMillis()-startTime)>= maxTime){
            generated = false;
        }
            
        for(int i = 0; i<n;i++){
            for(int j=0; j<n; j++){
                if(grille[i][j] == 1){
                    sol[i] = j+1;
                    System.out.println("Q_"+i+" -> "+sol[i]);
                }
            }
        }
        return generated;
    }
   
    
    /**
     * Permet de gérer les différentes instances de génération + exportes les fichiers en CSV
     * @param args 
     */
    public static void main(String[] args)
    {
        try {
            FileWriter fw = new FileWriter("Resultat2.txt",true);
            FileWriter fwCsv = new FileWriter("Resultat2.csv",true);
            
            for(int n = 20;n<21;n++){
                for(int k=18;k<n;k++){
                    int nbTrouver = 0;
                    int nbFail = 0;
                    int nbInstances = 30;
                    boolean detail = false;

                    for(int i=0;i<nbInstances;i++){
                        QueensCompletion qc = new QueensCompletion(n,k);
                        if(qc.generate()){
                            qc.execute();
                            if(detail)
                                fw.append(qc.res(i));

                            nbTrouver += qc.trouver();
                        }else{
                            nbFail++;
                            fw.append("Generation fail pour N = "+n+" et K = "+k+"\n");
                        }   

                    }

                    fw.append("Nombre de solutions trouvées pour N = "+n+" et K = "+k+" : "+nbTrouver+" / "+nbInstances + "\n");
                    fwCsv.append(n+","+k+","+nbTrouver+","+nbFail+";");
                }
                fwCsv.flush();
                fw.flush();
            }

            fwCsv.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(QueensCompletion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
                
    }

}
