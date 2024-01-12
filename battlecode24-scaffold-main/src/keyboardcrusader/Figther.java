package keyboardcrusader;

import battlecode.common.*;

public class Figther {

    //Figthers are grouped into 3 groups
    //Top = 0, Center = 1, Bottom = 2
    static int figthSquad;
    //Location where the bots should be when the dam falls
    static MapLocation setupLocation;
    static MapLocation attackLocation;
    static MapLocation closestEnemyPosF = new MapLocation(0,0);
    public static void think(RobotController rc) throws GameActionException {

        if(rc.getID()%10 ==3 || rc.getID()%10 ==4){
            figthSquad = 0;
            rc.setIndicatorDot(rc.getLocation(), 255,0,0);
        }
        else if(rc.getID()%10 ==5 || rc.getID()%10 ==6 || rc.getID()%10 ==9){
            figthSquad = 1;
            rc.setIndicatorDot(rc.getLocation(), 0,255,0);
        }
        else if(rc.getID()%10 ==7 || rc.getID()%10 ==8){
            figthSquad = 2;
            rc.setIndicatorDot(rc.getLocation(), 0,0,255);
        }


        //Get the position for the fall of the dam
        if(setupLocation == null){
            //
            if(Strategy.mapOrientation == 0){
                setupLocation = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/4*figthSquad+rc.getMapHeight()/4);
            }
            else {
                setupLocation = new MapLocation(rc.getMapWidth()/4*figthSquad+rc.getMapWidth()/4, rc.getMapHeight()/2);
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
            rc.setIndicatorLine(rc.getLocation(), setupLocation, 255, 0, 0);
        }
        else if( rc.getRoundNum() < 600){
            attackLocation = setupLocation;
            for(int i = ((rc.getRoundNum()-200)/30); i >=0; i --){
                if(Strategy.mapOrientation == 0){
                    if(rc.getAllySpawnLocations()[1].x < attackLocation.x){
                        attackLocation = attackLocation.add(Direction.EAST);
                    }
                    else{
                        attackLocation = attackLocation.add(Direction.WEST);
                    }
                }
                else {
                    if(rc.getAllySpawnLocations()[1].y < attackLocation.y){
                        attackLocation = attackLocation.add(Direction.NORTH);
                    }
                    else {
                        attackLocation = attackLocation.add(Direction.SOUTH);

                    }
                }
            }
            MicroMovement.moveR(rc, attackLocation);
            rc.setIndicatorLine(rc.getLocation(),attackLocation , 255, 0, 0);
        }
        else{
            MicroMovement.moveR(rc, attackLocation);
            rc.setIndicatorLine(rc.getLocation(),attackLocation , 255, 0, 0);
        }
    }
}
