package Solution;

import Controller.Controller;
import Model.Grid;
import Model.Building;


import java.util.*;

public class Dynamic {

    public static DP solution(List<Building> buildings, Grid[][] map, int cap){

        int[] area = new int[buildings.size()];
        int[] price = new int[buildings.size()];

        for (int i = 0; i < buildings.size(); i++) {
            area[i] = Integer.parseInt(buildings.get(i).getLength().getText())
                    * Integer.parseInt(buildings.get(i).getWidth().getText());
            price[i] = Integer.parseInt(buildings.get(i).getPrice().getText());
        }


        //Each object in the array holds the map and the value corresponding to the map
        DP[][] dps = new DP[buildings.size()+1][cap +1];
        for (int i = 0; i < dps.length; i++) {
            for (int j = 0; j < (cap +1); j++) {
                dps[i][j] = new DP();
                dps[i][j].setBlock(MapUtil.copy(map));
            }
        }


        for (int i = 1; i <= area.length; i++) {
            for (int j = 1; j <= cap; j++) {
                //Building area larger than current map area
                if(j < area[i-1]){
                    //Put in the map and value when the building is not selected
                    dps[i][j].setValue(dps[i-1][j].getValue());
                    dps[i][j].setCodeList(dps[i-1][j].getCodeList());
                }else {
                    //The value of the current building not placed is greater than the value placed
                    if(dps[i-1][j].getValue() > dps[i][j -area[i-1]].getValue()+price[i-1]){
                        dps[i][j].setValue(dps[i-1][j].getValue());
                        dps[i][j].setCodeList(dps[i-1][j].getCodeList());
                    }else {
                        //Remaining space
                        dps[i][j].getCodeList().addAll(dps[i][j -area[i-1]].getCodeList());
                        dps[i][j].setValue(dps[i][j -area[i-1]].getValue()+price[i-1]);
                        dps[i][j].addCode(buildings.get(i-1).getCode());
                    }
                }

            }
        }
        //no repeat
        Set<DP> dpSet = new HashSet<>();
        //check shape
        for (DP[] dp1: dps) {
            for (DP dp : dp1) {
                //building ava?
                boolean available = true;
                //Remove non-matching shapes
                List<Integer> codeList = dp.getCodeList();
                Grid[][] dpGrid = dp.getBlock();
                for (Integer code : codeList) {
                    if(!MapUtil.containBuilding(dpGrid, Controller.customBuilding.get(code))){
                        available = false;
                        break;
                    }
                }
                if (available){
                    dpSet.add(dp);
                }
            }
        }
        //index get building
        List<DP> dpList = new ArrayList<>(dpSet);
        buildings.sort(new Comparator<Building>() {
            @Override
            public int compare(Building o1, Building o2) {
                return Integer.parseInt(o2.getPrice().getText()) - Integer.parseInt(o1.getPrice().getText());
            }
        });
            //Compensation
        for (DP dp : dpList) {
            for(Building building : buildings){
                Grid[][] grids = dp.getBlock();
                while (MapUtil.containBuilding(grids,building)){
                    dp.addCode(building.getCode());
                    dp.setValue(dp.getValue()+Integer.parseInt(building.getPrice().getText()));
                }
            }
        }

        dpList.sort(new Comparator<DP>() {
            @Override
            public int compare(DP o1, DP o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        return dpList.get(0);

    }

}
