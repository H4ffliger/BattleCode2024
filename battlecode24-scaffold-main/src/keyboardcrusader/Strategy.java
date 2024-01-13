package keyboardcrusader;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import scala.Int;

import java.util.ArrayList;
import java.util.List;

import static keyboardcrusader.MicroMovement.rngMapLocation;
import static keyboardcrusader.MicroMovement.rngMapLocationCooldown;
import static keyboardcrusader.RobotPlayer.rng;
public class Strategy {


    //Map orientation
    //0= left - right
    //1= top - down
    //2= topleft - bottomright
    //3= topright - bottomleft
    //ToDo: add diagonal map locations
    static int mapOrientation = 0;
    static MapLocation enemyMapLocations[];

    static List<MapLocation> damInfo = new ArrayList<>();


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
                if (fillUpTheWaterLocation.x == 0 && fillUpTheWaterLocation.y == 0) {
                    MicroMovement.moveR(rc, rngMapLocation);

                } else {
                    if (rc.canFill(fillUpTheWaterLocation)) {
                        rc.fill(fillUpTheWaterLocation);
                    }
                    MicroMovement.moveR(rc, fillUpTheWaterLocation);
                }
            }
            //Eat Crums
            else {
                if (rc.senseNearbyCrumbs(-1).length > 0) {
                    MicroMovement.moveR(rc, rc.senseNearbyCrumbs(-1)[0]);
                } else {
                    MicroMovement.moveR(rc, rngMapLocation);
                }
            }
            rngMapLocationCooldown--;
        }

        //Spotting
        //ToDo: It's a bit buggy
        else if (rc.getRoundNum() < 170) {
            rc.setIndicatorString("Spotter");

            if (rc.getRoundNum() >= 160) {
                //System.out.println("Amount of dams to add: " + damInfo.size());
                for(int x  = damInfo.size()-1; x >= 0; x--){
                    boolean isInList = false;
                    int emptyIndex = -1;
                    System.out.println("Dam positions to add to the shared array: " + damInfo.size());
                    for(int s = 63; s >=0; s--) {
                        if(rc.readSharedArray(s) == 0){
                            emptyIndex = s;
                        }
                        if(rc.readSharedArray(s) == Integer.parseInt(Integer.toString(damInfo.get(x).x) + "0" + Integer.toString(damInfo.get(x).y))){
                            isInList = true;
                        }
                    }
                    if(isInList == false && emptyIndex != -1){
                        //System.out.println("Current amount of dam info: " + emptyIndex);
                        String prepX;
                        String prepY;
                        if(damInfo.get(x).x < 10){
                            prepX = "0" + Integer.toString(damInfo.get(x).x);
                        }
                        else {
                            prepX = Integer.toString(damInfo.get(x).x);
                        }
                        if(damInfo.get(x).y < 10){
                            prepY = "0" + Integer.toString(damInfo.get(x).y);
                        }
                        else {
                            prepY = Integer.toString(damInfo.get(x).y);
                        }
                        rc.writeSharedArray(emptyIndex, Integer.parseInt(prepX + "0" + prepY));
                    }
                }
            }
            else {
                for (int x = rc.getMapWidth() - 1; x >= 0; x -= rng.nextInt(rc.getMapWidth()/15) + 1) {
                    for (int y = rc.getMapHeight() - 1; y >= 0; y -= rng.nextInt(rc.getMapHeight()/15) + 1) {
                        try {
                            MapInfo mapInfo = rc.senseMapInfo(new MapLocation(x, y));
                            if (mapInfo.isDam()) {
                                if (damInfo.contains(mapInfo.getMapLocation()) == false) {
                                    damInfo.add(mapInfo.getMapLocation());
                                    //System.out.println("Found dam at position " + x + " " + y);
                                    //System.out.println("New dam size " + damInfo.size());
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
                //Give the robots their autonomy to think for them self, now they only get their commands with the shared array
                else {
            /*if(rc.getID()%10 == 2){
                robotClass = 3;
            }*/
                    if (rc.getID() % 10 < 2) {
                        robotClass = 1;
                        Healer.think(rc);
                        rc.setIndicatorString("Healer");
                    } else if (rc.getID() % 10 <= 9) {
                        robotClass = 0;
                        Fighter.think(rc);
                        rc.setIndicatorString("Fighter");
                    }

                }
            }

}
