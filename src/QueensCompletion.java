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
    
    int n; //taille du tableau -> ex : n=8, tableau 8*8
    int k; //nb reines déjà placées
    int sol[]; //contient la pré-solution
    IntVar[] vars;
    
    QueensCompletion(int N,int K){
        n = N;
        k = K;
        sol = new int[N];
    }
    
     // A compléter
    public void configureSearch()
    {

    }


    public void createSolver()
    {
        solver = new Solver("Queens Completion");
    }

    public void solve()
    {
        //solver.findAllSolutions();
        solver.findSolution();
    }

   
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
            
            /*for (int i = 0; i < vars.length; i++)
            {
                vars[i] = VariableFactory.enumerated("Q_" + i, 1, n, solver);
            }*/
                    
            // Ajout d’une contrainte imposant que les variables aient toutes des
            // valeurs différentes.
            solver.post(IntConstraintFactory.alldifferent(vars, "AC"));
            // Technique de filtrage utilisée (Arc Consistency).
            for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                            int k = j - i;
                            // Ajout des contraintes imposant qu’une paire de reine ne doit
                            // pas se trouver sur une même diagonale.
                            solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", -k));
                            solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", k));
                    }
            }
    }
    
    public void prettyOut()
    {
        if (solver.isFeasible().equals(ESat.TRUE)) {
            System.out.println("Une solution :");
                for(int i = 0; i<vars.length; i++){
                    //System.out.println("Q_"+i+" -> "+solver.getSolutionRecorder().getLastSolution().getIntVal(vars[i]));
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
     
    //fonction a utiliser en premier
    //elle va generer le tableau et placer les K queens en respectant les contraintes
    public void generate()
    {
        int reinePlacer = 0;
        int grille[][] = new int[n][n];
        while(reinePlacer!=k){
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
            System.out.println("Generation");
            boolean impossible = false;
            for (int i = 0; i < k; i++) {
                boolean notok = true;
                int ligneCourante = -1;
                int colonneCourante = -1;
                int nbDeplacement = 0;

                while(notok){
                    int rdm;
                    int rdm2;
                    // tire au sort
                    if(colonneCourante == -1){
                        int rdmTL = randomGenerator.nextInt(rdmLine.size());
                        rdm = (int) rdmLine.get(rdmTL);

                        int rdmTC = randomGenerator.nextInt(rdmColumn.size());
                        rdm2 = (int) rdmColumn.get(rdmTC);
                    // on decale    
                    }else{
                        rdm = ligneCourante;
                        rdm2 = (colonneCourante+1) % n;
                        nbDeplacement++;
                    }
                    // si on fait plus de n deplacement on a tester toute case de la ligne
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
            
        for(int i = 0; i<n;i++){
            for(int j=0; j<n; j++){
                if(grille[i][j] == 1){
                    sol[i] = j+1;
                    System.out.println("Q_"+i+" -> "+sol[i]);
                }
            }
        }
        
    }
   
    public static void main(String[] args)
    {
        try {
            FileWriter fw = new FileWriter("Resultat.txt",true);
            
            int nbTrouver = 0;
            int nbInstances = 100;
            boolean detail = false;
            int n = 10;
            int k = 9;
            
            for(int i=0;i<nbInstances;i++){
                QueensCompletion qc = new QueensCompletion(n,k);
                qc.generate();   
                qc.execute();
                
                if(detail)
                    fw.append(qc.res(i));
                
                nbTrouver += qc.trouver();
            }
            
            fw.append("Nombre de solutions trouvées pour N = "+n+" et K = "+k+" : "+nbTrouver+" / "+nbInstances + "\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(QueensCompletion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        //new QueensCompletion().execute(args);
        
    }

}
