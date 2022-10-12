package Solution;

import Model.Grid;
import Model.Building;

public class MapUtil {

    public static int replaceBuilding(Grid[][] grid, int i, int j, int length, int width, int code) {
        //search a area with all 0
        for (int x = i; x < i + length; x++) {
            for (int y = j; y < j + width; y++) {
                if (grid[x][y].getCode() != 0){
                    return 0;
                }
            }
        }
            //If the area is all 0 , assign the corresponding building code to all location in the area and return 1
        long uniqueKey = UtilRandom.getRandomVal();
        for (int x = i; x < i + length; x++) {
                for (int y = j; y < j + width; y++) {
                    grid[x][y].setCode(code);
                    if(grid[x][y].getUniqueKey() <= 0){
                        grid[x][y].setUniqueKey(uniqueKey);
                    }
                }
            }
        return 1;
    }

    public static boolean containBuilding(Grid[][] map, Building building){
        //map length
        int mapL = map.length;
        //map width
        int mapW = map[0].length;
        //building length
        int buildingL = Integer.parseInt(building.getLength().getText());
        //building width
        int buildingW = Integer.parseInt(building.getWidth().getText());

        //num of building placed
        int buildingNum = 0;

        for (int i = 0;i<=mapL- buildingL;i++){
            for (int j = 0; j <= mapW - buildingW; j++) {
                buildingNum = buildingNum + replaceBuilding(map,i,j,buildingL,buildingW,building.getCode());
                if (buildingNum > 0) return true;

            }
        }
        return false;
    }

    public static Grid[][] copy(Grid[][] grids){
        Grid[][] grid2 = new Grid[grids.length][grids[0].length];

        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[0].length; j++) {
                grid2[i][j] = grids[i][j].copy();
            }
        }
        return grid2;
    }

}
