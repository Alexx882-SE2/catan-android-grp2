package com.group2.catan_android.gamelogic;

import android.os.Build;

import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

    private List<Hexagon> hexagonList;
    private Connection [][] adjacencyMatrix;
    private Intersection [][] intersections;
    private int[][] surroundingHexagons;
    private static final int NON_EXISTING_HEXAGON = 19;


    // Define enum for locations
    enum Location {
        HILLS, FOREST, MOUNTAINS, FIELDS, PASTURE, DESERT
    }

    // Define enum for resource distributions
    enum ResourceDistribution {
        FIELDS(new int[]{1, 0, 0, 0, 0}),
        PASTURE(new int[]{0, 1, 0, 0, 0}),
        FOREST(new int[]{0, 0, 1, 0, 0}),
        HILLS(new int[]{0, 0, 0, 1, 0}),
        MOUNTAINS(new int[]{0, 0, 0, 0, 1}),
        DESERT(new int[]{0, 0, 0, 0, 0});

        private final int[] distribution;

        ResourceDistribution(int[] distribution) {
            this.distribution = distribution;
        }

        public int[] getDistribution() {
            return distribution;
        }
    }
    public static List<Hexagon> generateHexagons() {
        List<Location> locations = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy locations and values lists to ensure original lists remain unchanged
        Collections.addAll(locations, Location.HILLS, Location.HILLS, Location.HILLS, Location.FOREST,
                Location.FOREST, Location.FOREST, Location.FOREST, Location.MOUNTAINS, Location.MOUNTAINS,
                Location.MOUNTAINS, Location.FIELDS, Location.FIELDS, Location.FIELDS, Location.FIELDS,
                Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.PASTURE, Location.DESERT);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        List<Hexagon> hexagons = new ArrayList<>();

        Collections.shuffle(locations);
        Collections.shuffle(values);

        for (Location location : locations) {
            int value;
            if (location == Location.DESERT) {
                value = 0; // Desert location should have value 0
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution;
            switch (location) {
                case FIELDS:
                    resourceDistribution = ResourceDistribution.FIELDS;
                    break;
                case PASTURE:
                    resourceDistribution = ResourceDistribution.PASTURE;
                    break;
                case FOREST:
                    resourceDistribution = ResourceDistribution.FOREST;
                    break;
                case HILLS:
                    resourceDistribution = ResourceDistribution.HILLS;
                    break;
                case MOUNTAINS:
                    resourceDistribution = ResourceDistribution.MOUNTAINS;
                    break;
                default:
                    resourceDistribution = ResourceDistribution.DESERT;
            }

            hexagons.add(new Hexagon(location.name(), resourceDistribution.getDistribution(), value));
        }

        return hexagons;
    }

    public Board(){
        hexagonList = generateHexagons();
        adjacencyMatrix = getAdjacencyMatrix();
        intersections = getIntersections();
        surroundingHexagons = getSurroundingHexagons();
    }

    public void distributeResourcesByDiceRoll(int diceRoll) {
        for (Hexagon hexagon : hexagonList) {
            if (hexagon.getRollValue() == diceRoll) {
                hexagon.distributeResources();
            }
        }
    }

    public List<Hexagon> getHexagonList() {
        return hexagonList;
    }

    public Connection[][] getAdjacencyMatrix() {
        Connection emptyConnection = new Connection();
        adjacencyMatrix = new Connection[54][54];

        int[]rows = {0,0,1,1,2,2,2 ,3,3,4,4,4 ,5,5,6 ,6,7 ,7,8,8,8,9,9 ,9 ,10,10,10,11,11,11,12,12,12,13,13,13,14,14,14,15,15,16,16,17,17,17,18,18,18,19,19,19,20,20,20,21,21,21,22,22,22,23,23,23,24,24,24,25,25,25,26,26,27,27,28,28,28,29,29,29,30,30,30,31,31,31,32,32,32,33,33,33,34,34,34,35,35,35,36,36,36,37,37,38,38,39,39,39,40,40,40,41,41,41,42,42,42,43,43,43,44,44,44,45,45,45,46,46,47,47,48,48,49,49,49,50,50,51,51,51,52,52,53,53};
        int[]cols = {1,8,0,2,1,3,10,2,4,3,5,12,4,6,5,14,8,17,0,7,9,8,10,19,2 ,9 ,11,10,12,21,4 ,11,13,12,14,23,6 ,13,15,14,25,17,27,7 ,16,18,17,19,29,9 ,18,20,19,21,31,11,20,22,21,23,33,13,22,24,23,25,35,15,24,26,25,37,16,28,27,29,38,18,28,30,29,31,40,20,30,32,31,33,42,22,32,34,33,35,44,24,34,36,35,37,46,26,36,28,39,38,40,47,30,39,41,40,42,49,32,41,43,42,44,51,34,43,45,44,46,53,36,45,39,48,47,49,41,48,50,49,51,43,50,52,51,53,45,52};

        for (int i = 0; i < rows.length; i++) {
            adjacencyMatrix[rows[i]][cols[i]] = emptyConnection;
        }

        return adjacencyMatrix;
    }

    public Intersection[][] getIntersections() {
        Intersection intersection = new Intersection();
        intersections = new Intersection[6][11];

        //fill first 3 rows from the top and the last 3 from below at the same time
        for (int i = 0; i <= 2; i++) {
            for (int j = 2 - i; j <= 8 + i; j++) {
                intersections[i][j] = intersection;
                intersections[intersections.length - 1 - i][j] = intersection;
            }
        }

        return intersections;
    }

    private int[][] getSurroundingHexagons() {
        surroundingHexagons = new int[4][54];
        surroundingHexagons[0] = new int[] {0 ,1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53};
        surroundingHexagons[1] = new int[] {0 ,0 ,0 ,1 ,1 ,2 ,2 ,3 ,3 ,3,0 ,1 ,1 ,2 ,2 ,6 ,7 ,7 ,7 ,3 ,4 ,5 ,5 ,5 ,6 ,6 ,11,7 ,7 ,7 ,8 ,8 ,9 ,9 ,10,10,11,11,12,12,12,13,13,14,14,15,15,16,16,16,17,17,18,18};
        surroundingHexagons[2] = new int[] {19,19,1 ,19,2 ,19,19,19,0 ,4,1 ,4 ,2 ,5 ,6 ,19,19,3 ,8 ,4 ,8 ,9 ,9 ,6 ,10,11,19,19,12,8 ,12,9 ,13,10,14,11,15,19,19,16,13,16,14,17,15,18,19,19,19,17,19,18,19,19};
        surroundingHexagons[3] = new int[] {19,19,19,19,19,19,19,19,19,0,4 ,5 ,5 ,6 ,19,19,19,19,3 ,8 ,9 ,9 ,10,10,11,19,19,19,19,12,13,13,14,14,15,15,19,19,19,19,16,17,17,18,18,19,19,19,19,19,19,19,19,19};
        return surroundingHexagons;
    }

    public void addRoad(int playerID, int row, int col){
        // player has enough Resources

        if((adjacencyMatrix[row][col] != null && !(adjacencyMatrix[row][col] instanceof Road)) /*&& isNextToOwnRoad(col,playerID)*/){
            Road road = new Road(playerID);
            adjacencyMatrix[row][col] = road;
            adjacencyMatrix[col][row] = road;
        }
    }

    public void addVillage(int playerID, int row, int col){
        // player has enough Resources

        int intersection = translateIntersectionToAdjacencyMatrix(row,col);
        if((intersections[row][col] != null) && !isNextToBuilding(row,col) /*&& isNextToOwnRoad(intersection,playerID)*/ && !(intersections[row][col] instanceof Building)){
            intersections[row][col] = new Building(playerID,Building.BuildingType.VILLAGE);
            Building village = (Building)intersections[row][col];

            addToHexagons(intersection,village);
        }

    }

    private void addToHexagons(int intersection, Building building){
        int firstHexagon = surroundingHexagons[1][intersection];
        int secondHexagon = surroundingHexagons[2][intersection];
        int thirdHexagon = surroundingHexagons[3][intersection];

        if(firstHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(firstHexagon).addBuilding(building);
        }
        if(secondHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(secondHexagon).addBuilding(building);
        }
        if(thirdHexagon != NON_EXISTING_HEXAGON){
            hexagonList.get(thirdHexagon).addBuilding(building);
        }
    }

    public boolean isNextToBuilding(int row, int col){

        boolean evenCol = col % 2 == 0;
        boolean evenRow = row % 2 == 0;
        boolean nextToBuilding;
        if(col==0){
            nextToBuilding = (intersections[row][col + 1] instanceof Building);
        } else if (col==intersections[0].length) {
            nextToBuilding = (intersections[row][col - 1] instanceof Building);
        }else nextToBuilding = (intersections[row][col - 1] instanceof Building || intersections[row][col + 1] instanceof Building);

        if(nextToBuilding) {
            return true;
        }

        //if even even check or uneven uneven check below, else above if there is a building next to the position where it should be built
        if((evenRow && evenCol) || (!evenRow && !evenCol)){
            if(row!=0 && intersections[row-1][col] instanceof Building){
                nextToBuilding = true;
            }

        } else{
            if(row!= intersections[0].length && intersections[row + 1][col] instanceof Building) {
                nextToBuilding = true;
            }
        }

        return nextToBuilding;
    }

    public boolean isNextToOwnRoad(int intersection, int playerID){
        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((adjacencyMatrix[i][intersection] instanceof Road) && (adjacencyMatrix[i][intersection].getPlayerID() == playerID)){
                return true;
            }
        }
        return false;
    }

    public int translateIntersectionToAdjacencyMatrix(int row, int col) {
        int intersection = 0;

        switch(row){
            case 0: intersection = col - 2; break;
            case 1: intersection = 6 + col; break;
            case 2: intersection = 16 + col; break;
            case 3: intersection = 27 + col; break;
            case 4: intersection = 37 + col; break;
            case 5: intersection = 45 + col; break;
        }
        return intersection;
    }

}
