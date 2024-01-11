package keyboardcrusader;

import battlecode.common.*;
import examplefuncsplayer.RobotPlayer;

import static keyboardcrusader.RobotPlayer.robotClass;

public class MicroMovement {
    static int fightMode = 0;
    MapLocation lastEnemyPos;
    static MapLocation closestEnemyPos = new MapLocation(0,0) ;


    public static void moveR(RobotController rc, MapLocation mapLocation) throws GameActionException {
        RobotInfo[]  enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        RobotInfo[]  friends = rc.senseNearbyRobots(-1, rc.getTeam());

        for(int i = enemies.length-1; i >=0; i--) {
            if (closestEnemyPos.distanceSquaredTo(rc.getLocation()) > rc.getLocation().distanceSquaredTo(enemies[i].getLocation())) {
                closestEnemyPos = enemies[i].getLocation();
            }
        }

        if(enemies.length>0){
            if(robotClass == 1){
                fightMode = 6;
            }
            else{
                fightMode = 1;
            }
        }

        if(fightMode >0){
            if(robotClass == 1){
                RobotAction.healLowestRobot(rc);
            }
            else {

                RobotAction.attackLowestHealthEnemy(rc);
                if (rc.canMove(closestEnemyPos.directionTo(rc.getLocation()))) {
                    rc.move(closestEnemyPos.directionTo(rc.getLocation()));
                    RobotAction.attackLowestHealthEnemy(rc);
                }
            }
        }

        else{
            RobotAction.healLowestRobot(rc);
            Direction d = rc.getLocation().directionTo(mapLocation);
            for(int i = Direction.allDirections().length; i >=0; i--){
                if(rc.canMove(d)){
                    rc.move(d);
                    break;
                }
                d = d.rotateRight();
            }
        }




        fightMode -=1;

    }
}
