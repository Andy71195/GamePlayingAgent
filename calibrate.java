

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;


class Node implements Cloneable
{
    int board[][];
    int score;
    int count;
    int row;
    int column;
    int nextMove;
    int hasLevelReached=0;
    int picked[][];
    public Node() 
    {
        board=new int[calibrate.boardSize][calibrate.boardSize];
        score=0;
        count=0;
        picked = new int[calibrate.boardSize][calibrate.boardSize];   
    }
    
    public Object clone()throws CloneNotSupportedException
    {
        Node newNode = (Node)super.clone();
        newNode = new Node();
        newNode.board=new int[calibrate.boardSize][calibrate.boardSize];
        for(int i=0;i<=calibrate.boardSize-1;i++)
        {
            for(int j=0;j<=calibrate.boardSize-1;j++)
            {
                newNode.board[i][j]=this.board[i][j];
            }
        } 
        newNode.picked=new int[calibrate.boardSize][calibrate.boardSize];  
        newNode.score=this.score;
        newNode.nextMove=this.nextMove;
        newNode.row=this.row;
        newNode.column=this.column;
        newNode.count=0;
        return newNode;
    }   
}

public class calibrate{
    public static int boardSize=0;
    public static int fruitTypes=0;
    public static double timeGiven=0;
    public static int pruned=0;
    public static int childs = 0;
  
    public static void main(String[] args) throws IOException, CloneNotSupportedException
    {
        PrintWriter pw = new PrintWriter("calibrate.txt","UTF-8");
//        FileReader in = new FileReader("input.txt");
//        BufferedReader br = new BufferedReader(in);
        boardSize = 10;
        fruitTypes = 4;
        timeGiven = 300;
        
        Node node = new Node();
       
       
        node.board= new int[][]{
        {3,1,0,2,3,2,2,3,1,0},
        {0,1,2,1,2,3,2,0,1,3},
        {3,0,2,1,1,1,1,1,1,3},
        {0,2,2,1,0,3,1,1,3,2},
        {0,2,3,0,0,1,1,0,1,2},
        {0,3,2,3,3,2,1,0,1,0},
        {2,0,0,3,0,2,2,0,1,2},
        {2,2,0,2,2,0,0,0,2,1},
        {0,1,3,0,0,0,0,0,2,0},
        {2,2,0,0,0,2,2,2,3,1}
        };
        
        
        
        
        node.nextMove=1;
        double totalTime = 0;
        int depth =1;
        int i =0;
        while(i<7)
        {
        
//        printNode(MinmaxResultNode);
        
        childs =0;
            
        Node node1 = (Node)node.clone();
        
        long startTime = System.currentTimeMillis();
        
        
        Node MinmaxResultNode = minimax(depth + i,node1,-99999999,99999999);
        
        
        double timeTaken = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println(timeTaken);
        totalTime += timeTaken;
        
//        			String s = String.valueOf(timeTaken) + " " + String.valueOf(count);
        
        pw.println(timeTaken + " " + childs);
        i++;
        }
        pw.close();
    }
    
    public static void printNode(Node node) throws IOException 
    {    
        PrintWriter pw = new PrintWriter("output.txt","UTF-8");
        char column = (char)(node.column+65);
        pw.print(column+""+(node.row+1));
        pw.println();
        for(int i=0;i<boardSize;i++)
        {
            for(int j=0;j<boardSize;j++)
            {
                if(node.board[i][j]==-1)
                {
                    pw.print("*");
                }
                else
                {
                    pw.print(node.board[i][j]);
                }
            }
            pw.println("");
        }
        pw.close();
    }
    
    public static Node applyGravity(Node node)
    {       
        for(int col=0;col<=boardSize-1;col++)
        {
            for(int row=boardSize-1;row>=0;row--)
            {
                if(node.board[row][col]!=-1)
                {
                    for(int temp=row;temp<boardSize-1;temp++)
                    {
                        if(node.board[temp+1][col]==-1)
                        {
                            int tempx=node.board[temp+1][col];
                            node.board[temp+1][col]=node.board[temp][col];
                            node.board[temp][col]=tempx;
                        }
                    }
                }  
            }
        }   
        return node;
    }
    
    
    public static Node removeNeighbours(Node node, int row, int col)
    {
        
        int temp = node.board[row][col];
        boolean left=true;
        boolean right=true;
        boolean up=true;
        boolean down=true;
        if(col == boardSize-1)
            right = false;
        if(col == 0)
            left = false;
        if(row == boardSize-1)
            down = false;
        if(row == 0)
            up = false;
        if(right && node.board[row][col+1]==temp)
        {
            node.board[row][col]=-1;
            node.picked[row][col]=-2;
            node.count++;
            
            removeNeighbours(node, row, col+1);
        }
        if(left && node.board[row][col-1]==temp)
        {
            node.board[row][col]=-1;
            node.picked[row][col]=-2;
            node.count++;
            removeNeighbours(node, row, col-1);
        }  
        if(down && node.board[row+1][col]==temp)
        {
            node.board[row][col]=-1;
            node.picked[row][col]=-2;
            node.count++;
            removeNeighbours(node, row+1, col);
        }
        if(up && node.board[row-1][col]==temp)
        {
            node.board[row][col]=-1;
            node.picked[row][col]=-2;
            node.count++;
            removeNeighbours(node, row-1, col);
        }
        node.board[row][col]=-1;
        node.picked[row][col]=-2;
        
        node.score=(node.count+1)*(node.count+1);
        
        return node;
    }
    
    public static boolean checkEndGame(Node node)
    {
        for(int i=0;i<boardSize;i++)
        {
            for(int j=0;j<boardSize;j++)
            {
               
                if(node.board[i][j]!=-1)
                    return false;
            }
        }
        return true;
    }
    
    
    
    public static Node minimax(int depth, Node node,int alpha, int beta) throws CloneNotSupportedException
    {   
        if(checkEndGame(node) || depth == 0)
        {
            return node;
        }
        
        ArrayList<Node> childList= new ArrayList<Node>();
        for(int i = 0; i<boardSize; i++)
        {
            for(int j= 0; j<boardSize; j++)
            {
                if(node.board[i][j]!=-1 && node.picked[i][j]!=-2)
                { 
                    childs++;
                    Node child = (Node)node.clone();
                    child = removeNeighbours(child, i, j);
                    child = applyGravity(child);
                    for(int p=0;p<boardSize;p++)
                    {
                        for(int q = 0; q<boardSize;q++)
                        {
                            if(child.picked[p][q]==-2)
                                node.picked[p][q]= child.picked[p][q];
                        }
                    }
                    child.row = i;
                    child.column = j;
                    childList.add(child);
                }
            }
        }
            Collections.sort(childList, new Comparator<Node>() {
            @Override public int compare(Node smaller, Node bigger) {
                return bigger.score - smaller.score;
            }

            });
        Node bestChild = (Node)node.clone();
        int maximum = Integer.MIN_VALUE, minimum = Integer.MAX_VALUE;
        
        for(Node child : childList){  
                    
                    if(node.nextMove == 1)
                    {
                        child.nextMove=0;
                        child.score= node.score + child.score;
                        Node badaChild = minimax(depth-1, child,alpha,beta);
                        child.score= badaChild.score + child.score;
                        if(child.score > maximum)
                        {
                            maximum = child.score;
                            alpha = Math.max(alpha,maximum);
                            bestChild = child;
                        }
                        if(maximum >= beta)
                        {
                            pruned++;
                            return bestChild;
                        }
                    }
                    else
                    {
                        child.nextMove=1;
                        child.score= node.score - child.score;
                        Node badaChild = minimax(depth-1, child,alpha,beta);
                        child.score = badaChild.score - child.score;
                        if(child.score < minimum)
                        { 
                            
                            minimum= child.score;
                            beta = Math.min(beta, minimum);
                            bestChild = child;
                        }
                        if(alpha >= minimum)
                        {
                            pruned++;
                            return bestChild;
                        }
                    }   
        }
        
        return bestChild;
    }
}