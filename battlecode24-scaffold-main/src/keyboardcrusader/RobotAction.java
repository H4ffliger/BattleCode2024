package keyboardcrusader;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class RobotAction {




    public static void attackLowestHealthEnemy(RobotController rc) throws GameActionException {
        RobotInfo lowestEnemyPlayer = null;
        RobotInfo[]  enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        if(enemies.length > 0){
            lowestEnemyPlayer = enemies[0];
            for(int i = enemies.length-1; i >=0; i--){
                if(rc.canAttack(enemies[i].getLocation())){
                    if(enemies[i].getHealth() < lowestEnemyPlayer.getHealth()){
                        lowestEnemyPlayer = enemies[i];
                    }
                }
            }
            if(rc.canAttack(lowestEnemyPlayer.getLocation())){
                rc.attack(lowestEnemyPlayer.getLocation());
            }
        }
    }


    public static void healLowestRobot(RobotController rc) throws GameActionException{
        RobotInfo[]  friends = rc.senseNearbyRobots(-1, rc.getTeam());
        RobotInfo lowestRobotPlayer = null;

        if(friends.length > 0){
            lowestRobotPlayer = friends[0];
            for(int i = friends.length-1; i >=0; i--){
                if(rc.canHeal(friends[i].getLocation()) && friends[i].getHealth()<1000 && lowestRobotPlayer.getHealth() > friends[i].getHealth()){
                    lowestRobotPlayer = friends[i];

                }
            }
            if(rc.canHeal(lowestRobotPlayer.getLocation())){
                rc.heal(lowestRobotPlayer.getLocation());
            }
        }
    }
}
