import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import processing.core.*;

public class PathingMain extends PApplet {
   private List<PImage> imgs;
   private int current_image;
   private long next_time;
   private PImage background;
   private PImage obstacle;
   private PImage goal;
   private List<Point> path;

   private static final int TILE_SIZE = 32;

   private static final int ANIMATION_TIME = 100;

   public static GridValues[][] grid;
   private static final int ROWS = 9;
   private static final int COLS = 9;

   public enum GridValues {
      BACKGROUND, OBSTACLE, GOAL, SEARCHED
   };

   private Point wPos;

   private boolean drawPath = false;

   public void settings() {
      size(640, 480);
   }

   /* runs once to set up world */
   public void setup() {
      path = new LinkedList<>();
      wPos = new Point(1, 1);
      imgs = new ArrayList<>();
      imgs.add(loadImage("images/wyvern1.bmp"));
      imgs.add(loadImage("images/wyvern2.bmp"));
      imgs.add(loadImage("images/wyvern3.bmp"));

      background = loadImage("images/grass.bmp");
      obstacle = loadImage("images/vein.bmp");
      goal = loadImage("images/water.bmp");

      grid = new GridValues[ROWS][COLS];
      initialize_grid(grid);

      current_image = 0;
      next_time = System.currentTimeMillis() + ANIMATION_TIME;
   }

   /* set up a 2D grid to represent the world */
   /*
    * private static void initialize_grid(GridValues[][] grid)
    * {
    * for (int row = 0; row < grid.length; row++)
    * {
    * for (int col = 0; col < grid[row].length; col++)
    * {
    * grid[row][col] = GridValues.BACKGROUND;
    * }
    * }
    * 
    * //set up some obstacles
    * for (int row = 2; row < 8; row++)
    * {
    * grid[row][row + 5] = GridValues.OBSTACLE;
    * }
    * 
    * for (int row = 8; row < 12; row++)
    * {
    * grid[row][19 - row] = GridValues.OBSTACLE;
    * }
    * 
    * for (int col = 1; col < 8; col++)
    * {
    * grid[11][col] = GridValues.OBSTACLE;
    * }
    * 
    * grid[13][14] = GridValues.GOAL;
    * }
    */

   private static void initialize_grid(GridValues[][] grid) {
      for (int row = 0; row < grid.length; row++) {
         for (int col = 0; col < grid[row].length; col++) {
            grid[row][col] = GridValues.BACKGROUND;
         }
      }
      grid[1][3] = GridValues.OBSTACLE;
      for (int row = 1; row < 6; row++) {
         grid[row][4] = GridValues.OBSTACLE;
      }
      grid[5][2] = GridValues.OBSTACLE;
      grid[5][3] = GridValues.OBSTACLE;

      grid[8][8] = GridValues.GOAL;

   }

   private void next_image() {
      current_image = (current_image + 1) % imgs.size();
   }

   /* runs over and over */
   public void draw() {
      // A simplified action scheduling handler
      long time = System.currentTimeMillis();
      if (time >= next_time) {
         next_image();
         next_time = time + ANIMATION_TIME;
      }

      draw_grid();
      draw_path();

      image(imgs.get(current_image), wPos.x * TILE_SIZE, wPos.y * TILE_SIZE);
   }

   private void draw_grid() {
      for (int row = 0; row < grid.length; row++) {
         for (int col = 0; col < grid[row].length; col++) {
            draw_tile(row, col);
         }
      }
   }

   private void draw_path() {
      if (drawPath) {
         for (Point p : path) {
            fill(128, 0, 0);
            rect(p.x * TILE_SIZE + TILE_SIZE * 3 / 8,
                  p.y * TILE_SIZE + TILE_SIZE * 3 / 8,
                  TILE_SIZE / 4, TILE_SIZE / 4);
         }
      }
   }

   private void draw_tile(int row, int col) {
      switch (grid[row][col]) {
         case BACKGROUND:
            image(background, col * TILE_SIZE, row * TILE_SIZE);
            break;
         case OBSTACLE:
            image(obstacle, col * TILE_SIZE, row * TILE_SIZE);
            break;
         case SEARCHED:
            fill(0, 128);
            rect(col * TILE_SIZE + TILE_SIZE / 4,
                  row * TILE_SIZE + TILE_SIZE / 4,
                  TILE_SIZE / 2, TILE_SIZE / 2);
            break;
         case GOAL:
            image(goal, col * TILE_SIZE, row * TILE_SIZE);
            break;
      }
   }

   public static void main(String args[]) {
      PApplet.main("PathingMain");
   }

   public void keyPressed() {
      if (key == ' ') {
         // clear out prior path and re-initialize grid
         path.clear();
         initialize_grid(grid);

         // EXAMPLE - replace with dfs
         // dfs(wPos, grid, path);
         path = new AStarPathingStrategy().computePath(wPos, new Point(8, 8), p -> isValid(p, grid),
               PathingMain::target, PathingStrategy.CARDINAL_NEIGHBORS);
         System.out.println(path.size());
      } else if (key == 'p') {
         drawPath ^= true;
      } else if (key == 'c') {
         path.clear();
         initialize_grid(grid);
      }
   }

   /*
    * Replace (and rename) the below with a depth first search.
    * This code provided only as an example of moving in
    * in one direction for one tile - it mostly is for illustrating
    * how you might test the occupancy grid and add nodes to path!
    */
   private boolean dfs(Point pos, GridValues[][] grid, List<Point> path) {
      int[] dirX = { 1, -1, 0, 0 };
      int[] dirY = { 0, 0, 1, -1 };

      for (int i = 0; i < dirX.length; i++) {
         Point neighbor = new Point(pos.x + dirX[i], pos.y + dirY[i]);
         grid[pos.y][pos.x] = GridValues.SEARCHED;
         if (isValid(neighbor, grid)) {
            if (grid[neighbor.y][neighbor.x] == GridValues.GOAL) {
               path.add(0, neighbor);
               return true;
            }
            if (dfs(neighbor, grid, path)) {
               path.add(0, neighbor);
               return true;
            }
         }
      }
      return false;
   }

   private boolean isValid(Point neighbor, GridValues[][] grid) {
      return withinBounds(neighbor, grid) &&
            grid[neighbor.y][neighbor.x] != GridValues.OBSTACLE &&
            grid[neighbor.y][neighbor.x] != GridValues.SEARCHED;
   }

   private static boolean withinBounds(Point p, GridValues[][] grid) {
      return p.y >= 0 && p.y < grid.length &&
            p.x >= 0 && p.x < grid[0].length;
   }

   public static boolean adjacent(Point p1, Point p2) {
      return (p1.getX() == p2.getX() && Math.abs(p1.getY() - p2.getY()) == 1) || (p1.getY() == p2.getY()
            && Math.abs(p1.getX() - p2.getX()) == 1);
   }

   public static boolean target(Point p1, Point p2) {
      return (p1.getX() == p2.getX()) && ((p1.getY() == p2.getY()));
   }
}
