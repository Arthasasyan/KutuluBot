import java.util.*;
import java.io.*;
import java.math.*;

class Cell
{
  public int x;
  public int y;
  public char type; // # or . or w
  public Entity onCell;
  public Cell()
  {

  }
  public Cell(int x, int y, char type)
  {
    this.x=x;
    this.y=y;
    this.type=type;
  }
  public void clear()
  {
    this.onCell=null;
  }
  public static boolean isClose(Cell first, Cell second) {
    boolean result = false;
    if(((first.x-1 == second.x) && (first.y==second.y))||(first.x+1 == second.x && first.y==second.y) ||((first.x ==second.x) && (first.y-1==second.y))||(first.x == second.x && first.y+1==second.y))
    result=true;

    return result;
  }

  @Override
  public String toString() {
    return x+" "+y;
  }
}

class Entity
{
  protected String type;
  protected int id;
  protected Cell position;

  public Entity(String type, int id, Cell position)
  {
    this.position=position;
    this.type=type;
    this.id=id;
    this.position.onCell=this;

  }

  public int getId() {
    return id;
  }

  public Cell getPossition() {
    return position;
  }

  public String getType() {
    return type;
  }

  public void setPosition(Cell position) {
    this.position = position;
  }
}

class Explorer extends Entity{
  protected int sanity;
  public Explorer(int id, Cell position, int sanity)
  {
    super("EXPLORER", id, position);
    this.sanity=sanity;
  }

  public int getSanity() {
    return sanity;
  }

  public void setSanity(int sanity) {
    this.sanity = sanity;
  }
}
class MainExplorer extends Explorer
{
  public MainExplorer(int id, Cell position, int sanity)
  {
    super(id,position, sanity);
  }
  public void moveTo(Entity entity)
  {
    this.position.clear();
    System.out.println("MOVE "+entity.getPossition());
    
    
  }
  public void moveTo(Cell cell)
  {
    if(cell.type!='#') { //if correct cell
      this.position.clear();

      System.out.println("MOVE " + cell);
    }
    else
      System.out.println("WAIT");
  }
  public void escapeFrom(Cell[][] map)
  {
    this.position.clear();
    Cell to=this.position;
    if(map[this.position.x+1][this.position.y].onCell==null && map[this.position.x+1][this.position.y].type=='.')
     to= map[this.position.x+1][this.position.y];
    if(map[this.position.x-1][this.position.y].onCell==null && map[this.position.x-1][this.position.y].type=='.')
      to= map[this.position.x-1][this.position.y];
    if(map[this.position.x][this.position.y+1].onCell==null && map[this.position.x][this.position.y+1].type=='.')
      to= map[this.position.x][this.position.y+1];
    if(map[this.position.x][this.position.y-1].onCell==null && map[this.position.x][this.position.y-1].type=='.')
      to= map[this.position.x][this.position.y-1];

    System.out.println("MOVE "+to);

  }
}
class Minion extends Entity{
  private boolean wandering;
  private Explorer target;

  public Minion(int id, Cell position, boolean wandering, Explorer terget)
  {
    super("WANDERER", id, position);
    this.target=terget;
    this.wandering=wandering;
  }

  public boolean isWandering() {
    return wandering;
  }

  public Explorer getTarget() {
    return target;
  }
}

class Player {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    if (in.hasNextLine()) {
      in.nextLine();
    }
    Cell[][] map = new Cell[width][height];
    for (int i = 0; i < height; i++) {
      String line = in.nextLine();
      for(int j=0;j<line.length();j++)
        map[j][i]=new Cell(j,i,line.charAt(j));
      System.err.println(line);
    }


    int sanityLossLonely = in.nextInt(); // how much sanity you lose every turn when alone, always 3 until wood 1
    int sanityLossGroup = in.nextInt(); // how much sanity you lose every turn when near another player, always 1 until wood 1
    int wandererSpawnTime = in.nextInt(); // how many turns the wanderer take to spawn, always 3 until wood 1
    int wandererLifeTime = in.nextInt(); // how many turns the wanderer is on map after spawning, always 40 until wood 1
    Explorer[] explorers = new Explorer[4];
    MainExplorer player = null;



    // game loop
    while (true) {
      int entityCount = in.nextInt(); // the first given entity corresponds to your explorer
      List<Minion> minions = new ArrayList<>();

      { //reading player information
        String entityType = in.next();
        int id = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param0 = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        System.err.println(entityType + " id:" + id + " x:" + x + " y:" + y + " param0:" + param0 + " param1:" + param1 + " param2:" + param2);
        if(player==null) {
          player = new MainExplorer(id, map[x][y], param0);
          explorers[id] = player;
        }
        else
        {
          player.setSanity(param0);
          player.setPosition(map[x][y]);
        }
      }
      for (int i = 1; i < entityCount; i++) {
        String entityType = in.next();
        int id = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param0 = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        System.err.println(entityType+" id:"+id+" x:"+x+" y:"+y+" param0:"+param0+" param1:"+param1+" param2:"+param2);
        if(id<4 && id>=0) {
          if(explorers[id]==null) {
            if(id!=player.getId())
            explorers[id] = new Explorer(id, map[x][y], param0); //entering explorer

          }
          else
          {
            explorers[id].setSanity(param0);
            explorers[id].setPosition(map[x][y]);
          }
        }
        else if(id>=0)
        {
          boolean wandering;
          Explorer target=null;
          if(param1 ==1)
            wandering=true;
          else
            wandering=false;

          if(param2!=-1)
            target=explorers[param2];
          minions.add(new Minion(id,map[x][y],wandering,target)); //entering minion

        }

      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      boolean walked = false;
      for(Minion m:minions) {
        if(Cell.isClose(m.getPossition(),player.getPossition()) && !walked) { //if minion is close
          player.escapeFrom(map);
          walked=true;
          System.err.println("escaping from minion on "+ m.getPossition());
        }
        m.getPossition().clear();

      }
      if(!walked)
      {
        boolean explrorsAlive = false; //checking if any other explorer left
        for(Explorer e:explorers)
        {
          if(e.getSanity()>10 && e.getId()!=player.getId())
          {
            explrorsAlive=true;
            player.moveTo(e);
            System.err.println("moving to "+e.getPossition());
            break;
          }
        }
        if(!explrorsAlive)
          System.out.println("WAIT");
      }
      minions.clear();

    }
  }
}