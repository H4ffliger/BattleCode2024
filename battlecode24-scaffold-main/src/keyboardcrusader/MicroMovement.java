package keyboardcrusader;

import battlecode.common.*;
import examplefuncsplayer.RobotPlayer;

import java.util.ArrayList;
import java.util.List;

import static keyboardcrusader.Fighter.centerSpawn;
import static keyboardcrusader.RobotPlayer.directions;
import static keyboardcrusader.Strategy.robotClass;
import static keyboardcrusader.RobotPlayer.rng;

public class MicroMovement {
    static int fightMode = 0;
    static int specialMovement = 0;
    static boolean lastDirectionChangeWasLeft = false;

    static Direction specialDirection;
    static Direction lastDirection;
    static MapLocation lastLocation;
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
                fightMode = 4;
            }
            if(robotClass >0){
                fightMode = 10;
            }
        }

        if (fightMode > 0) {
            if(robotClass == 0) {
                if(rc.getHealth() <600){
                    RobotAction.attackClosestEnemy(rc);
                    if(Math.sqrt(rc.getLocation().distanceSquaredTo(closestEnemyPos)) < 4) {
                        fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                    }
                    RobotAction.attackClosestEnemy(rc);
                }
                else {
                    RobotAction.attackClosestEnemy(rc);
                    if (Math.sqrt(rc.getLocation().distanceSquaredTo(closestEnemyPos)) < 2) {
                        fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                        RobotAction.attackClosestEnemy(rc);
                    } else {
                        if (rc.getRoundNum() % 3 == 1) {
                            RobotAction.attackClosestEnemy(rc);
                            fineMovement(rc, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPos)));
                            RobotAction.attackClosestEnemy(rc);
                        } else {
                            RobotAction.attackClosestEnemy(rc);
                            if(Math.sqrt(rc.getLocation().distanceSquaredTo(closestEnemyPos)) > 3){
                                fineMovement(rc, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPos)));
                            }
                            //fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                            RobotAction.attackClosestEnemy(rc);
                        }
                        //Wait for enemy to engage
                        //fineMovement(rc, rc.getLocation().add(rc.getLocation().directionTo(closestEnemyPos)));
                        RobotAction.attackClosestEnemy(rc);
                    }
                    RobotAction.healLowestRobot(rc);
                }

            }
            else if(robotClass == 1){
                RobotAction.healLowestRobot(rc);
                if (rc.getLocation().distanceSquaredTo(closestEnemyPos) <= 6) {
                    fineMovement(rc, rc.getLocation().add(closestEnemyPos.directionTo(rc.getLocation())));
                    if(!RobotAction.healLowestRobot(rc)){
                        RobotAction.attackClosestEnemy(rc);
                    }
                }
                else{
                    MapLocation mp = RobotAction.getLowerRobot(rc);
                    if(mp != null){
                        fineMovement(rc, mp);
                    }
                }
            }
        }
        else{

            //Remove water if not in fight
            MapLocation fillUpTheWaterLocation = new MapLocation(0, 0);
            MapInfo infos[] = rc.senseNearbyMapInfos(3);
            for (int i = infos.length - 1; i >= 0; i--) {
                if (infos[i].isWater()) {
                    if (fillUpTheWaterLocation.distanceSquaredTo(rc.getLocation()) > rc.getLocation().distanceSquaredTo(infos[i].getMapLocation()))
                        fillUpTheWaterLocation = infos[i].getMapLocation();
                }
            }
            if(rc.canFill(fillUpTheWaterLocation)){
                rc.fill(fillUpTheWaterLocation);
            }
            if(mapLocation.distanceSquaredTo(rc.getLocation()) > 20){
                Direction d = rc.getLocation().directionTo(mapLocation);
                mapLocation = rc.getLocation().add(d).add(d).add(d).add(d).add(d).add(d).add(d);
            }

            fineMovement(rc, mapLocation);
            RobotAction.healLowestRobot(rc);
            }
        fightMode -= 1;
    }

    public static void fineMovement(RobotController rc, MapLocation mapLocation) throws GameActionException {








        //AntiStuck mechanics edge
        //ToDo: Wenn Ziel zu weit weg, dann einen Zwischenpunkt festlegen


        int totalMovesToCalculate = 7;
        int movesInAdvance;
        movesInAdvance = (int) Math.sqrt(rc.getLocation().distanceSquaredTo(mapLocation))-1;

/*
        if(rc.getRoundNum()%10==1){
            if(rc.getLocation().distanceSquaredTo(latestSelfLocation) <6 && rc.getLocation().distanceSquaredTo(mapLocation) >= 10){
                antiStuckSteps = (rc.getMapWidth()+rc.getMapHeight())/5;
            }
            latestSelfLocation = rc.getLocation();
        }

        //If the target location is more 4 tiles away, we have to go to a more basic pathfinding system

        if(antiStuckSteps >= 0){

            if(specialDirection == null){
                specialDirection = rc.getLocation().directionTo(mapLocation);
                lastDirection = specialDirection;
                lastLocation = rc.getLocation();
            }
            Direction d = rc.getLocation().directionTo(mapLocation);
            if(rc.canMove(lastDirection)){
                rc.move(lastDirection);
            }
            else {
                Direction tempDir = lastDirection;
                for(int l = 2; l>=0; l--){
                    tempDir = tempDir.rotateRight();
                    if(rc.canMove(tempDir)){
                        lastDirectionChangeWasLeft = false;
                        lastDirection = tempDir;
                    }
                }

            specialMovement --;
            }
            antiStuckSteps --;
            //Check if it's possible to move directly to the given location
        }*/


        if(movesInAdvance <= 0){
            movesInAdvance = 0;
        }
        if(movesInAdvance >2){
            movesInAdvance = 2;
        }
        List<Direction> calculateLocations = new ArrayList<>();
        int biasScore[] = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        MapInfo isPassable[] = rc.senseNearbyMapInfos(2);
        //Bytecode Limit
        List<MapLocation> passable = new ArrayList<>();
        for (int i = isPassable.length - 1; i >= 0; i--) {
            if (isPassable[i].isPassable() == false) {
                passable.add(isPassable[i].getMapLocation());
            }
        }

        for(int s = 0; s < 8; s++) {
            calculateLocations.add(Direction.values()[s]);
            for(int p = totalMovesToCalculate; p >= 0; p--){
                Direction tempFutureGuessLocation = calculateLocations.get(s);
                if(rc.canMove(tempFutureGuessLocation)){
                    MapLocation tempMp = rc.getLocation().add(tempFutureGuessLocation);
                    for(int a = movesInAdvance; a >= 0; a--) {
                        Direction rngDirection = Direction.values()[s];
                        boolean canMove = true;
                        for (int i = passable.size() - 1; i >= 0; i--) {
                            if (tempMp.add(rngDirection) ==passable.get(i)) {
                                canMove = false;
                            }
                        }
                        if(canMove){
                            tempMp = tempMp.add(rngDirection);
                        }
                    }
                    if(tempMp.distanceSquaredTo(mapLocation) < biasScore[s]){
                     biasScore[s] = tempMp.distanceSquaredTo(mapLocation);
                    }
                }
                else {
                    //No possible moves in this direction
                    break;
                }
            }
        }
        int indexBestMove = 0;
        int bestMoveScore = 1000;
        int indexSecondBestMove = 0;
        int secondbestMoveScore = 1000;
        for(int i = biasScore.length-1; i >=0; i--){
            if(bestMoveScore > biasScore[i]){
                bestMoveScore = biasScore[i];
                indexBestMove = i;
            }
        }
        for(int i = biasScore.length-1; i >=0; i--){
            if(secondbestMoveScore > biasScore[i] && bestMoveScore != biasScore[i]){
                secondbestMoveScore = biasScore[i];
                indexSecondBestMove = i;
            }
        }


        if(rc.canMove(Direction.values()[indexBestMove])){
            rc.move(Direction.values()[indexBestMove]);
        }
        else if(rc.canMove(Direction.values()[indexSecondBestMove])){
            rc.move(Direction.values()[indexSecondBestMove]);

        }
        else{
            rc.setIndicatorString("Pathfinding error, best and second best move not possible");
            //System.out.println("Pathfinding, best and second best move not possible");
        }
        rc.setIndicatorLine(rc.getLocation(), mapLocation, 255, 0, 0);

    }
}
