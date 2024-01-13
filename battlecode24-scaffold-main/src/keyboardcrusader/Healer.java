package keyboardcrusader;

import battlecode.common.*;
import static keyboardcrusader.RobotAction.healLowestRobot;
public class Healer {
    //Healers are grouped into 4 groups
    //Top = 0, Center = 1, Bottom = 2
    static int healSquad;
    //Location where the bots should be when the dam falls
    static MapLocation setupLocationH;
    static MapLocation attackLocationH;
    static MapLocation closestEnemyPosF = new MapLocation(0, 0);

    public static void think(RobotController rc) throws GameActionException {

        if (rc.getID() % 21 == 7) {
            healSquad = 0;
            rc.setIndicatorDot(rc.getLocation(), 255, 0, 0);
        } else if (rc.getID() % 21 == 14) {
            healSquad = 1;
            rc.setIndicatorDot(rc.getLocation(), 0, 255, 0);
        } else if (rc.getID() % 21 == 21) {
            healSquad = 2;
            rc.setIndicatorDot(rc.getLocation(), 0, 0, 255);
        }

        //Get the position for the fall of the dam
        if (setupLocationH == null) {
            int xSetupModifier = 2;
            int ySetupModifier = 2;
            if (Strategy.mapOrientation == 0) {
                if(rc.getAllySpawnLocations()[1].x < rc.getMapWidth() / 2){
                    xSetupModifier = -3;
                }
                setupLocationH = new MapLocation(rc.getMapWidth() / 2 + xSetupModifier, rc.getMapHeight() / 4 * healSquad + rc.getMapHeight() / 4);

            } else {
                if(rc.getAllySpawnLocations()[1].y < rc.getMapHeight() / 2){
                    ySetupModifier = -3;
                }
                setupLocationH = new MapLocation(rc.getMapWidth() / 4 * healSquad + rc.getMapWidth() / 4, rc.getMapHeight() / 2 + +ySetupModifier);
            }
        }


        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        //Heal
        healLowestRobot(rc);

        //Healers set traps too
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
            MicroMovement.moveR(rc, setupLocationH);
        }
        else if( rc.getRoundNum() < 600){
            attackLocationH = setupLocationH;
            for(int i = ((rc.getRoundNum()-200)/30); i >=0; i --){
                if(Strategy.mapOrientation == 0){
                    if(rc.getAllySpawnLocations()[1].x < attackLocationH.x){
                        attackLocationH = attackLocationH.add(Direction.EAST);
                    }
                    else{
                        attackLocationH = attackLocationH.add(Direction.WEST);
                    }
                }
                else {
                    if(rc.getAllySpawnLocations()[1].y < attackLocationH.y){
                        attackLocationH = attackLocationH.add(Direction.NORTH);
                    }
                    else {
                        attackLocationH = attackLocationH.add(Direction.SOUTH);

                    }
                }
            }
            MicroMovement.moveR(rc, attackLocationH);
        }
        else{
            MicroMovement.moveR(rc, attackLocationH);
        }




    }
}