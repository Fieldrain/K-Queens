/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Arrays;
import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.Solver;
import java.util.Random;

/**
 *
 * @author cb454580
 */
public class QueensCompletion extends AbstractProblem {
    
    int n = 10; //taille du tableau -> ex : n=8, tableau 8*8
    int k = 9; //nb reines déjà placées
    IntVar[] vars;
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
        solver.findAllSolutions();
        //solver.findSolution();
    }

    public void buildModel()
    {
        //VariableFactory.bounded("X", 0, 5, solver);  -> x compris entre 0 et 5            
        
        //Important : les permutations
        
       
        vars = new IntVar[n];
        //permet de donner un nom au variable
        for (int i = 0; i < vars.length; i++)
        {
            //vars[i] = VariableFactory.enumerated("Q_" + i, 1, n, solver);
            //IntVar x = VariableFactory.fixed(1, solver);   //permet de fixer une valeur pour une variable
        }
        for (int i = 0; i < n-1; i++)
        {
            for (int j = i+1; j < n; j++)
            {
                int z = j - i;
                solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", -k));
                solver.post(IntConstraintFactory.arithm(vars[i], "!=", vars[j], "+", k));
            }
        }

        
    }
    
    public void prettyOut()
    {
    }
     
    //fonction a utiliser en premier
    //elle va generer le tableau et placer les K queens en respectant les contraintes
    public void generate()
    {
        int reinePlacer = 0;
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
                }
            else
                reinePlacer = 0;
            
        }
    }
        
    public static void main(String[] args)
    {
        //new QueensCompletion().execute(args);
        QueensCompletion qc = new QueensCompletion();
        qc.generate();
    }

}
