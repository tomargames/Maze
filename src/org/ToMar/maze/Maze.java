package org.ToMar.maze;

import java.awt.Graphics;
import java.util.ArrayList;
import org.ToMar.Utils.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author marie
 * This is branching from the Maze class in the Maze package
 */

public class Maze extends Canvas implements MouseListener
{
    private Maze.Cell[] maze;
    private int maxRows;
    private int maxCols;
    private int size;
    private int scale;
    private int exitIdx;
    private int youIdx;
    private int xStart;
    private Maze.Direction youDoor;
    private Maze.Door westDoor;
    private Maze.Door eastDoor;
    private Maze.Door northDoor;
    private Maze.Door southDoor;
    private Polygon frontPanel;
    private Polygon backPanel;
    private Polygon floorPanel;
    private Polygon ceilingPanel;
    private int width;
    private int height;
    private boolean showWalls;
    
    public Maze() {}
    public Maze(int maxRows, int maxCols, int size, int scale)
    {
        this.addMouseListener(this);
        this.scale = scale;
        this.size = size;
        this.maxRows = maxRows;
        this.maxCols = maxCols;
        width = 2 * (maxCols + 4) * size;
        height = (maxRows + 4) * size;
        this.setSize(width, height);
        xStart = (maxCols + 6) * size;
        int[] xFront = {xStart, xStart + (20*scale), xStart + (20*scale), xStart};
        int[] yFront = {5*scale, 5*scale, 17*scale, 17*scale};
        frontPanel = new Polygon(xFront, yFront, 4);
        int[] xBack = {xStart + 4*scale, xStart + 16*scale, xStart + 16*scale, xStart + 4*scale};
        int[] yBack = {4*scale, 4*scale, 11*scale, 11*scale};
        backPanel = new Polygon(xBack, yBack, 4);
        int[] xFloor = {xStart, xStart + 4*scale, xStart + 16*scale, xStart + 20*scale};
        int[] yFloor = {17*scale, 11*scale, 11*scale, 17*scale};
        floorPanel = new Polygon(xFloor, yFloor, 4);
        int[] yCeiling = {5*scale, 4*scale, 4*scale, 5*scale};
        ceilingPanel = new Polygon(xFloor, yCeiling, 4);
        int[] xSouth = {xStart + 8*scale, xStart + 12*scale, xStart + 12*scale, xStart + 8*scale};
        int[] ySouth = {14*scale, 14*scale, 17*scale, 17*scale};
        southDoor = new Maze.Door(xSouth, ySouth, tmColors.DARKGREEN);
        int[] xNorth = {xStart + 8*scale, xStart + 12*scale, xStart + 12*scale, xStart + 8*scale};
        int[] yNorth = {8*scale, 8*scale, 11*scale, 11*scale};
        northDoor = new Maze.Door(xNorth, yNorth, tmColors.DARKBLUE);
        // east door should be along the line from 16,12 to 20,18
        int[] xEast = {xStart + 17*scale, xStart + 19*scale, xStart + 19*scale, xStart + 17*scale};
        int[] yEast = {10*scale, 12*scale, 15*scale + (scale/2), 12*scale + (scale/2)};
        eastDoor = new Maze.Door(xEast, yEast, tmColors.LIGHTPURPLE);
        // west door should be along the line from 0,18 to 4,12
        int[] xWest = {xStart + 1*scale, xStart + 3*scale, xStart + 3*scale, xStart + 1*scale};
        int[] yWest = {12*scale, 10*scale, 12*scale + (scale/2), 15*scale + (scale/2)};
        westDoor = new Maze.Door(xWest, yWest, tmColors.ORANGE);
        reInit(1);
    }
    public void reInit(int level)
    {
        maze = new Maze.Cell[maxRows * maxCols];
        Maze.Direction.setMaxes(maxRows, maxCols);
        for (int i = 0; i < maxRows * maxCols; i++)
        {
            maze[i] = new Maze.Cell(i);
        } 
        ArrayList<Integer> trail = new ArrayList<>();
        int pick = Functions.getRnd(maze.length);
        int newPick;
        int pickRnd = 0;
        int possRnd;
        maze[pick].setVisited(true);
        trail.add(pick);
        while(!trail.isEmpty())
        {    
            ArrayList<Integer> possibles = new ArrayList<>();
            ArrayList<Maze.Direction> dirs = new ArrayList<>();
            for (Maze.Direction dir : Maze.Direction.values())
            {
                newPick = Maze.Direction.getNewIndex(pick, dir);
                if (newPick > -1 && !maze[newPick].isVisited())
                {
                    possibles.add(newPick);
                    dirs.add(dir);
                }    
            }    
            // if there are no possibles, remove it from the trail
            if (possibles.isEmpty())
            {
                trail.remove(pickRnd);
                if (trail.isEmpty())
                {
                    break;
                }    
            }
            else
            {
                possRnd = Functions.getRnd(possibles.size());
                newPick = possibles.get(possRnd);
                maze[newPick].setVisited(true);
                trail.add(newPick);
                switch (dirs.get(possRnd)) 
                {
                    case NORTH:
                        maze[newPick].setSouthWall(false);
                        break;
                    case SOUTH:
                        maze[pick].setSouthWall(false);
                        break;
                    case EAST:
                        maze[pick].setEastWall(false);
                        break;
                    case WEST:
                        maze[newPick].setEastWall(false);
                }
            }
            pickRnd = Functions.getRnd(trail.size());
            pick = trail.get(pickRnd);
        }
        for (int i = 0; i < maze.length; i++)
        {
            maze[i].setVisited(false);
        }    
        this.youIdx = (maxRows % 2 == 1) ? maxCols * maxRows/2 - maxCols/2 : maxRows/2 * maxCols;
        this.exitIdx = youIdx - 1;
        maze[exitIdx].setExit();
        maze[youIdx].setYou(true);
        youDoor = Maze.Direction.WEST;
        showWalls = false;
    }        
    public void paint(Graphics g)
    {
        for (int i = 0; i < maze.length; i++)
        {
            maze[i].draw(this.getGraphics(), showWalls);
        } 
        Maze.Cell north = (Maze.Direction.getNewIndex(youIdx, Maze.Direction.NORTH) > -1) ? maze[Maze.Direction.getNewIndex(youIdx, Maze.Direction.NORTH)] : null;
        Maze.Cell west = (Maze.Direction.getNewIndex(youIdx, Maze.Direction.WEST) > -1) ? maze[Maze.Direction.getNewIndex(youIdx, Maze.Direction.WEST)] : null;
        maze[youIdx].drawCell(this.getGraphics(), north, west);
    }        
    public void update(Graphics g)
	{
		g.setColor(tmColors.CREAM);
		g.fillRect(0, 0, width, height);
		paint(g);
	}
    public void mouseClicked(MouseEvent e)
    {
        if (northDoor.clicked(e))
        {
            processDoor(Maze.Direction.NORTH);
        }    
        else if (southDoor.clicked(e))
        {
            processDoor(Maze.Direction.SOUTH);
        }    
        else if (eastDoor.clicked(e))
        {
            processDoor(Maze.Direction.EAST);
        }    
        else if (westDoor.clicked(e))
        {
            processDoor(Maze.Direction.WEST);
        }
        if (maze[youIdx].isExit())
        {
            showWalls = true;
        }    
        repaint();
    }
    private void processDoor(Maze.Direction dir)
    {
        maze[youIdx].setYou(false);
        youIdx = Maze.Direction.getNewIndex(youIdx, dir);
        youDoor = Maze.Direction.getInverse(dir);
        maze[youIdx].setYou(true);
    }    
            
    public void mousePressed(MouseEvent e)    {    }
    public void mouseReleased(MouseEvent e)    {    }
    public void mouseEntered(MouseEvent e)  {    }
    public void mouseExited(MouseEvent e) {    }
    

    private class Cell 
    {
        private boolean eastWall = true;        // this controls the east wall
        private boolean southWall = true;      // this controls the south wall
        private boolean visited = false;
        private boolean exit = false;
        private boolean you = false;
        private int idx, col, row;
        private String label = "";
    
        public Cell(int idx)
        {
            this.idx = idx;
            this.col = (idx % maxCols) + 1;
            this.row = idx / maxCols + 1;
//          this.label = "" + idx;
        }
        public void drawCell(Graphics og, Maze.Cell north, Maze.Cell west)
        {
            og.setColor(tmColors.PALEPEACH);
       		og.fillPolygon(frontPanel);
            og.fillPolygon(floorPanel);
       		og.fillPolygon(backPanel);
            og.fillPolygon(ceilingPanel);
            og.setColor(tmColors.BLACK);
       		og.drawPolygon(frontPanel);
            og.drawPolygon(floorPanel);
       		og.drawPolygon(backPanel);
            og.drawPolygon(ceilingPanel);
            southDoor.process(this.isSouthWall(), og);
            eastDoor.process(this.isEastWall(), og);
            if (west == null)
            {
                westDoor.process(true, og);
            }    
            else        
            {
                westDoor.process(west.isEastWall(), og);
            }
            if (north == null)
            {
                northDoor.process(true, og);
            }   
            else
            {   
                northDoor.process(north.isSouthWall(), og);
            }
            og.setColor(tmColors.YELLOW);
            switch (youDoor)
            {
                case NORTH:
               		og.fillRoundRect(xStart + 9*scale + (scale/2), 10*scale, scale, scale, 20, 20);
                    break;
                case SOUTH:
               		og.fillRoundRect(xStart + 9*scale + (scale/2), 16*scale, scale, scale, 20, 20);
                    break;
                case EAST:
               		og.fillRoundRect(xStart + 17*scale, 13*scale, scale, scale, 20, 20);
                    break;
                case WEST:
               		og.fillRoundRect(xStart + 2*scale, 13*scale, scale, scale, 20, 20);
                    break;
                default:
                    System.out.println("ERROR!!! Fallthrough in drawCell");
            }
        }        
        public void draw(Graphics og, boolean walls)
        {
            int x = (col + 1) * size;
            int y = (row + 1) * size;
            og.setColor(tmColors.BLACK);
            if (this.isSouthWall())
            {
                if (walls || row == maxRows || visited)
                {    
                    og.drawLine(x, y + size, x + size, y + size);
                }    
            }
            if (this.isEastWall())
            {
                if (walls || col == maxCols || visited)
                {    
                    og.drawLine(x + size, y, x + size, y + size);
                }    
            }
            if (col == 1)
            {
                og.drawLine(x, y, x, y + size);
            }
            if (row == 1)
            {
                og.drawLine(x, y, x + size, y);
            }
            if (visited)
            {
                og.setColor(tmColors.PALECHARTREUSE);
           		og.fillRoundRect(x+1, y+1, size-1, size-1, 0, 0);
            }    
            if (you)
            {
                og.setColor(tmColors.RED);
           		og.fillRoundRect(x + 2, y + 2, size - 2, size - 2,20,20);
            }    
            og.drawString(label, x + 5, y + size-1);
        }
        public boolean isEastWall()
        {
            return eastWall;
        }
        public void setEastWall(boolean eastWall)
        {
            this.eastWall = eastWall;
        }
        public boolean isSouthWall()
        {
            return southWall;
        }
        public void setSouthWall(boolean southWall)
        {
            this.southWall = southWall;
        }
        public boolean isVisited()
        {
            return visited;
        }
        public void setVisited(boolean visited)
        {
            this.visited = visited;
        }    
        public int getIdx()
        {
            return idx;
        }
        public void setIdx(int idx)
        {
            this.idx = idx;
        }
        public String getLabel()
        {
            return label;
        }
        public void setLabel(String label)
        {
            this.label = label;
        }
        public boolean isExit()
        {
            return exit;
        }
        public void setExit()
        {
            this.exit = true;
            this.eastWall = false;
        }
        public boolean isYou()
        {
            return you;
        }
        public void setYou(boolean you)
        {
            this.you = you;
            this.visited = true;
        }
    }
    private class Door 
    {
        private Polygon door;
        private Color bgColor;
        private boolean active;
        
        public Door(int[] xs, int[] ys, Color bgColor)
        {
            door = new Polygon(xs, ys, xs.length);
            this.bgColor = bgColor;
            active = false;
        }
        public void process(boolean wall, Graphics og)
        {
            if (wall)
            {
                this.setActive(false);
            }    
            else        
            {
                this.draw(og);
            }
        }        
        public void draw(Graphics og)
        {
            og.setColor(bgColor);
            og.fillPolygon(door);
            og.setColor(tmColors.BLACK);
            og.drawPolygon(door);
            setActive(true);
        }        
        public boolean isActive()
        {
            return active;
        }
        public void setActive(boolean active)
        {
            this.active = active;
        }
        public boolean clicked(MouseEvent me)
        {
            if (active)
            {    
                if (door.contains(me.getPoint()))
                {
                    return true;
                }
            }
            return false;
        }        
    }        
    public static enum Direction 
    {
        NORTH,
        SOUTH,
        EAST,
        WEST;

        private static int rows = 1;
        private static int cols = 1;

        public static void setMaxes(int rows, int cols)
        {
            Maze.Direction.rows = rows;
            Maze.Direction.cols = cols;
        }        
        public static Maze.Direction getInverse(Maze.Direction dir)
        {
            switch (dir) 
            {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                case WEST:
                    return EAST;
            }
            return NORTH;
        }
        public static int getNewIndex(int currentIndex, Maze.Direction dir) 
        {
            switch (dir) 
            {
                case NORTH:
                    return (currentIndex - cols < 0) ? -1 : currentIndex - cols;
                case SOUTH:
                    return (currentIndex + cols) < cols * rows ? currentIndex + cols : -1;
                case EAST:
                    return ((currentIndex + 1) % cols == 0) ? -1 : currentIndex + 1;
                case WEST:
                    return (currentIndex % cols == 0) ? -1 : currentIndex - 1;
            }
            return -1;
        }
    }	
}