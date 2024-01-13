package keyboardcrusader;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import static keyboardcrusader.MicroMovement.rngMapLocation;
import static keyboardcrusader.MicroMovement.rngMapLocationCooldown;
import static keyboardcrusader.RobotPlayer.rng;
public class Strategy {


    //Map orientation
    //0= left - right
    //1= top - down
    //ToDo: add diagonal map locations
    static int mapOrientation = 0;
    static MapLocation enemyMapLocations[];


    //Builder
    // ID = 2
    //Healer
    // ID = 1
    //Figher
    // ID = 0
    static int robotClass;

    public static void gameStrategy(RobotController rc) throws GameActionException {

        //When all robots are spawned
        if (rc.getRoundNum() == 50) {
            MapLocation spawnLocations[] = rc.getAllySpawnLocations();
            int yIndicator = -10;
            int xIndicator = -10;
            for (int i = spawnLocations.length - 1; i >= 0; i--) {
                if (xIndicator == -10) {
                    if (spawnLocations[i].y > rc.getMapHeight() / 2) {
                        yIndicator = 1;
                    } else {
                        yIndicator = -1;
                    }
                    if (spawnLocations[i].x > rc.getMapWidth() / 2) {
                        xIndicator = 1;
                    } else {
                        xIndicator = -1;
                    }
                } else {
                    if (spawnLocations[i].y > rc.getMapHeight() / 2) {
                        if (yIndicator != 1) {
                            yIndicator = 0;
                        }
                    } else {
                        if (yIndicator != -1) {
                            yIndicator = 0;
                        }
                    }
                    if (spawnLocations[i].x > rc.getMapWidth() / 2) {
                        if (xIndicator != 1) {
                            xIndicator = 0;
                        }
                    } else {
                        if (xIndicator != -1) {
                            xIndicator = 0;
                        }
                    }
                }
            }
            if (yIndicator != 0) {
                mapOrientation = 1;
                System.out.println("Info: Round 50: Map orientation is estimated to be horizontal");
            } else {
                mapOrientation = 0;
                System.out.println("Info: Round 50: Map orientation is estimated to be vertical");

            }
        }


        //Preround
        if (rc.getRoundNum() < 140) {
            if (rngMapLocationCooldown <= 0) {
                rngMapLocationCooldown = 10;
                rngMapLocation = new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapWidth()));
            }
            //Remove water
            if (rc.getID() % 2 == 1) {
                MapLocation fillUpTheWaterLocation = new MapLocation(0, 0);
                MapInfo infos[] = rc.senseNearbyMapInfos(-1);
                for (int i = infos.length - 1; i >= 0; i--) {
                    if (infos[i].isWater()) {
                        if (fillUpTheWaterLocation.distanceSquaredTo(rc.getLocation()) > rc.getLocation().distanceSquaredTo(infos[i].getMapLocation()))
                            fillUpTheWaterLocation = infos[i].getMapLocation();
                    }
                }
                if(fillUpTheWaterLocation.x ==0 && fillUpTheWaterLocation.y == 0) {
                    MicroMovement.moveR(rc, rngMapLocation);

                }
                else {
                    if(rc.canFill(fillUpTheWaterLocation)){
                        rc.fill(fillUpTheWaterLocation);
                    }
                    MicroMovement.moveR(rc, fillUpTheWaterLocation);
                }
            }
            //Eat Crums
            else {
                if (rc.senseNearbyCrumbs(-1).length > 0) {
                    MicroMovement.moveR(rc, rc.senseNearbyCrumbs(-1)[0]);
                }
                else{
                    MicroMovement.moveR(rc, rngMapLocation);
                }
            }
            rngMapLocationCooldown--;
        }

        //Give the robots their autonomy to think for them self, now they only get their commands with the shared array
        else{
            /*if(rc.getID()%10 == 2){
                robotClass = 3;
            }*/
            if(rc.getID()%10 <2 ){
                robotClass = 1;
                Healer.think(rc);
                rc.setIndicatorString("Healer");
            }
            else if(rc.getID()%10 <= 9 ){
                robotClass = 0;
                Fighter.think(rc);
                rc.setIndicatorString("Fighter");
            }

        }
    }
}
