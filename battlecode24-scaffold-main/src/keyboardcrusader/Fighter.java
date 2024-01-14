package keyboardcrusader;

import battlecode.common.*;
import scala.Int;

public class Fighter {

    //Figthers are grouped into 3 groups
    //Top = 0, Center = 1, Bottom = 2
    static int figthSquad;
    //Location where the bots should be when the dam falls
    static MapLocation setupLocation;
    static MapLocation attackLocation;
    static MapLocation centerSpawn;
    static MapLocation closestEnemyPosF = new MapLocation(0,0);
    public static void think(RobotController rc) throws GameActionException {

        if(setupLocation == null){
            System.out.println("Thinking for the first time on round " + rc.getRoundNum());

            for(int s = 63; s >= 0; s--){
                if(rc.readSharedArray(s) != 0 && s != 0){
                    int deparseX;
                    int deparseY = Integer.parseInt(Integer.toString(rc.readSharedArray(rc.getID()%s+1)).substring(2));
                    if(Integer.toString(rc.readSharedArray(rc.getID()%s+1)).length()==4){
                        deparseX = Integer.parseInt(Integer.toString(rc.readSharedArray(rc.getID()%s+1)).substring(0, 1));
                    }
                    else{
                        deparseX = Integer.parseInt(Integer.toString(rc.readSharedArray(rc.getID()%s+1)).substring(0, 2));
                    }

                    setupLocation = new MapLocation(deparseX, deparseY);
                    System.out.println("RAW DATA: " + rc.readSharedArray(rc.getID()%s+1) + ", destructed data: " + deparseX + ";" + deparseY);
                    System.out.println("Amount of dams found: " + s);
                    s = -1;
                    break;
                } else if (s == 0) {
                    System.out.println("Error shared string is empty No dam information");
                    setupLocation = new MapLocation( rc.getMapWidth()/2, rc.getMapHeight()/2);

                }
            }
        }



        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        for (int i = enemies.length - 1; i >= 0; i--) {
            if (closestEnemyPosF.distanceSquaredTo(rc.getLocation()) > rc.getLocation().distanceSquaredTo(enemies[i].getLocation())) {
                closestEnemyPosF = enemies[i].getLocation();
            }
        }

        if(rc.getLocation().distanceSquaredTo(closestEnemyPosF) <=3){
            if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPosF)).add(rc.getLocation().directionTo(closestEnemyPosF)))){
                rc.build(TrapType.EXPLOSIVE, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPosF)).add(rc.getLocation().directionTo(closestEnemyPosF)));
            }
            else if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPosF)))){
                rc.build(TrapType.EXPLOSIVE, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPosF)));
            }
        }


        if(rc.getRoundNum()<200){
            MicroMovement.moveR(rc, setupLocation);
        }
        else if( rc.getRoundNum() < 600){
            //Smooth push
            MapLocation allySpawns[] = rc.getAllySpawnLocations();
            centerSpawn = new MapLocation(
                    (allySpawns[0].x + allySpawns[1].x + allySpawns[2].x)/3,
                    (allySpawns[0].y + allySpawns[1].y + allySpawns[2].y)/3);

            attackLocation = setupLocation;
            for(int i = ((rc.getRoundNum()-200)/((rc.getMapHeight()+rc.getMapWidth())/4)); i >=0; i --) {
                attackLocation = attackLocation.add(centerSpawn.directionTo(setupLocation));
            }
            MicroMovement.moveR(rc, attackLocation);
        }
        else{
            MicroMovement.moveR(rc, attackLocation);
        }
    }
}
