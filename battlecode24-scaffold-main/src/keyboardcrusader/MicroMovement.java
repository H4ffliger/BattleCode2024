package keyboardcrusader;

import battlecode.common.*;
import examplefuncsplayer.RobotPlayer;
import static keyboardcrusader.Strategy.robotClass;
import static keyboardcrusader.RobotPlayer.rng;

public class MicroMovement {
    static int fightMode = 0;

    static MapLocation rngMapLocation;
    static int rngMapLocationCooldown = 0;
    MapLocation lastEnemyPos;
    static MapLocation closestEnemyPos = new MapLocation(0, 0);

    static MapLocation latestSelfLocation = new MapLocation(0,0);
    static int antiStuckStrategy = 0;
    static int antiStuckSteps = 0;

    public static void moveR(RobotController rc, MapLocation mapLocation) throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());

        for (int i = enemies.length - 1; i >= 0; i--) {
            if (closestEnemyPos.distanceSquaredTo(rc.getLocation()) > rc.getLocation().distanceSquaredTo(enemies[i].getLocation())) {
                closestEnemyPos = enemies[i].getLocation();
            }
        }
        if (enemies.length > 0) {
            if(robotClass == 0) {
                fightMode = 2;
            }
            if(robotClass >0){
                fightMode = 4;
            }
        }


        if (fightMode > 0) {
            if(robotClass == 0) {
                RobotAction.attackLowestHealthEnemy(rc);
                if (rc.getLocation().distanceSquaredTo(closestEnemyPos) <= 3) {
                    fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                    RobotAction.attackLowestHealthEnemy(rc);
                } else {
                    //Wait for enemy to engage
                    //fineMovement(rc, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPos)));
                    RobotAction.attackLowestHealthEnemy(rc);
                }
            }
            else if(robotClass == 1){
                RobotAction.healLowestRobot(rc);
                if (rc.getLocation().distanceSquaredTo(closestEnemyPos) <= 5) {
                    fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                    if(!RobotAction.healLowestRobot(rc)){
                        RobotAction.attackLowestHealthEnemy(rc);
                    }
                }
            }
        }
        else{
            fineMovement(rc, mapLocation);
            RobotAction.healLowestRobot(rc);
            }
        fightMode -= 1;
    }

    public static void fineMovement(RobotController rc, MapLocation mapLocation) throws GameActionException {
        if((rc.getRoundNum()+ rc.getID()) %5  == 1){
            if(latestSelfLocation.distanceSquaredTo(rc.getLocation()) <= 2 && rc.getLocation().distanceSquaredTo(mapLocation) >3){
                if(antiStuckStrategy == 0){
                    antiStuckStrategy = 1;
                }
                else if(antiStuckStrategy == 1){
                    antiStuckStrategy = 2;
                }
                else if(antiStuckStrategy == 2)
                    antiStuckStrategy = rng.nextInt(2);
            }
            latestSelfLocation = rc.getLocation();
        }
        if(antiStuckStrategy == 0){
            Direction generalDirection = rc.getLocation().directionTo(mapLocation);
            if(rc.canMove(rc.getLocation().directionTo(mapLocation))){
                rc.move(rc.getLocation().directionTo(mapLocation));
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateRight())){
                rc.move(rc.getLocation().directionTo(mapLocation).rotateRight());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateRight().rotateRight())){
                rc.canMove(rc.getLocation().directionTo(mapLocation).rotateRight().rotateRight());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateRight().rotateRight().rotateRight())){
                rc.canMove(rc.getLocation().directionTo(mapLocation).rotateRight().rotateRight().rotateRight());
            }
        }
        else if(antiStuckStrategy == 1){
            if(rc.canMove(rc.getLocation().directionTo(mapLocation))){
                rc.move(rc.getLocation().directionTo(mapLocation));
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateLeft())){
                rc.move(rc.getLocation().directionTo(mapLocation).rotateLeft());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateLeft().rotateLeft())){
                rc.canMove(rc.getLocation().directionTo(mapLocation).rotateLeft().rotateLeft());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).rotateLeft().rotateLeft().rotateLeft())){
                rc.canMove(rc.getLocation().directionTo(mapLocation).rotateLeft().rotateLeft().rotateLeft());
            }
        }
        else if(antiStuckStrategy == 2){

            if(rc.canMove(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft().rotateLeft())){
                rc.move(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft().rotateLeft());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft())){
                rc.move(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft());
            }
            else if(rc.canMove(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft().rotateLeft().rotateLeft())){
                rc.canMove(rc.getLocation().directionTo(mapLocation).opposite().rotateLeft().rotateLeft().rotateLeft());
            }
        }
    }
}
