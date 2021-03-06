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
        board=new int[homework.boardSize][homework.boardSize];
        score=0;
        count=0;
        picked = new int[homework.boardSize][homework.boardSize];   
    }
    
    public Object clone()throws CloneNotSupportedException
    {
        Node newNode = (Node)super.clone();
        newNode = new Node();
        newNode.board=new int[homework.boardSize][homework.boardSize];
        for(int i=0;i<=homework.boardSize-1;i++)
        {
            for(int j=0;j<=homework.boardSize-1;j++)
            {
                newNode.board[i][j]=this.board[i][j];
            }
        } 
        newNode.picked=new int[homework.boardSize][homework.boardSize];  
        newNode.score=this.score;
        newNode.nextMove=this.nextMove;
        newNode.row=this.row;
        newNode.column=this.column;
        newNode.count=0;
        return newNode;
    }   
}

public class homework {

    public static int boardSize=0;
    public static int fruitTypes=0;
    public static double timeGiven=0;
    public static int pruned=0;
    public static int childs = 0;
  
    public static void main(String[] args) throws IOException, CloneNotSupportedException
    {
        PrintWriter pw = new PrintWriter("output.txt","UTF-8");
        FileReader in = new FileReader("input.txt");
        BufferedReader br = new BufferedReader(in);
        boardSize = Integer.parseInt(br.readLine());
        fruitTypes = Integer.parseInt(br.readLine());
        timeGiven = Double.parseDouble(br.readLine());
        
        Node node = new Node();
       
        for(int i =0;i<boardSize;i++)
        {   
            String temp[]=br.readLine().split("");
            for(int j=0; j<boardSize;j++)
            {
                if(temp[j].equals("*"))
                {
                    node.board[i][j]=-1;
                    continue;
                }
                node.board[i][j]=Integer.parseInt(temp[j]);  
            }
        }
        node.nextMove=1;
        Node MinmaxResultNode = minimax(3,node,-99999999,99999999);
        printNode(MinmaxResultNode);
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
