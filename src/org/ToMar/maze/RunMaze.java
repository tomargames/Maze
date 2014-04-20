package org.ToMar.maze;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author marie
 */
public class RunMaze implements Runnable
{
    private JFrame frame;
    private Thread thread;
    private Maze maze;

    public void setUp(int numRows, int numCols, int size, int scale)
    {
        frame = new JFrame("Maze");
        maze = new Maze(numRows, numCols, size, scale);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(maze, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        thread = new Thread(this);
        thread.start();
    }
    
    public static void main(String[] args)
    {
        new RunMaze().setUp(11, 15, 17, 14);
    }
    
    public void run() 
    {
        while (true)
        {   
            
        }    
    }
}
