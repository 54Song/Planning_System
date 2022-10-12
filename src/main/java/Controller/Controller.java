package Controller;

import Model.Grid;
import Model.Building;
import Solution.DP;
import Solution.Dynamic;
import Solution.UtilRandom;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Controller {
    //grid width
    private static final int GRID_WIDTH = 25;
    //map
    private Grid[][] map;
    //map copied
    private Grid[][] map2;
    //judgement button
    private int currentValue = 0;
    //Map
    @FXML
    private AnchorPane mapView;
    //building table
    @FXML
    private TableView customBuildingTable;

    @FXML
    private TableColumn nameCol,lengthCol,widthCol,priceCol,colorCol;

    public static Map<Integer, Building> customBuilding = new HashMap<>();

    @FXML
    private Label showMax,max;


    public void initialize(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        widthCol.setCellValueFactory(new PropertyValueFactory<>("width"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        customBuildingTable.setEditable(true);
        showMax.setFont(new Font(20));
        max.setFont(new Font(20));
    }

    public void deleteMap(){
        showMax.setText("0");
        mapView.getChildren().clear();
    }

    public void addBuilding(){
        ColorPicker picker = new ColorPicker();
        customBuildingTable.getItems().add(new Building("",0,0,0,picker));
    }

    public void deleteBuilding(){
        Object item = customBuildingTable.getSelectionModel().getSelectedItem();
        if(item == null){
            return;
        }
        customBuildingTable.getItems().remove(item);
    }

    public void chooseRoad(){
        currentValue = 1;
    }

    public void chooseDelete(){
        currentValue = 2;
    }

    public void chooseProhibit(){
        currentValue = 3;
    }

    public void chooseCancel(){
        currentValue = 4;
    }

    public void doOpti(){


        //start time
        Long startTime = System.currentTimeMillis();

        UtilRandom.reset();

        //Not executed if the map is empty
        if(map == null){
            return;
        }

        //record road and prohibition
        copyMap2();
        copyMap();

        ObservableList items = customBuildingTable.getItems();

        List<Building> buildings = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            Building building = (Building) items.get(i);
            if(isInteger(building.getLength().getText())
                    &&isInteger(building.getWidth().getText())
                    &&isInteger(building.getPrice().getText())
                    &&Integer.parseInt(building.getLength().getText())>0
                    &&Integer.parseInt(building.getWidth().getText())>0
                    &&Integer.parseInt(building.getPrice().getText())>0
                    &&Integer.parseInt(building.getLength().getText()) <= map.length
                    &&Integer.parseInt(building.getWidth().getText()) <= map[0].length){
                if(Integer.parseInt(building.getLength().getText())
                        * Integer.parseInt(building.getWidth().getText())
                        >map[0].length*map.length){
                    showFail();
                }else {
                    customBuilding.put(11 + i,building);
                    building.setArea(Integer.parseInt(building.getLength().getText())
                            *Integer.parseInt(building.getWidth().getText()));
                    building.setUnitValue();
                    building.setCode(11 + i);
                    buildings.add(building);
                }
            }else {
                showBuildingError();
                return;
            }
        }


        buildings.sort((o1,o2) -> o2.getUnitValue().compareTo(o1.getUnitValue()));

        int cap = 0;
        //cal map area
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if(this.map[i][j].getCode() == 0){
                    cap++;
                }
            }
        }

        //Algorithm start
        DP dp = Dynamic.solution(buildings,this.map,cap);
        int maxValue = dp.getValue();
        this.map = dp.getBlock();
        showMax.setText(maxValue+"");
        showMap();
        long endTime = System.currentTimeMillis();

        System.out.println("Spend Time: "+ (endTime - startTime)+"ms");

        }

    public void showMap(){
        mapView.getChildren().clear();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                //Grid style
                Label grid = new Label();
                grid.setLayoutX(40 + row * GRID_WIDTH);
                grid.setLayoutY(40 + col * GRID_WIDTH);
                grid.setPrefWidth(GRID_WIDTH);
                grid.setPrefHeight(GRID_WIDTH);
                grid.setFont(new Font(15));
                grid.setAlignment(Pos.CENTER);
                int mapValue = this.map[row][col].getCode();
                grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,BorderWidths.DEFAULT)));
                switch (mapValue){
                    case 1:
                        grid.setBackground(new Background(new BackgroundFill(Color.GRAY,CornerRadii.EMPTY, Insets.EMPTY)));
                        break;
                    case 3:
                        grid.setText("X");
                        grid.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.DASHED,CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                        grid.setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
                        break;
                    default:
                        if(mapValue > 10){
                            Building building = customBuilding.get(mapValue);
                            Color color = building.getColor().getValue();
                            grid.setBackground(new Background(new BackgroundFill(color,CornerRadii.EMPTY,Insets.EMPTY)));
                        }else {
                            this.map[row][col].setCode(0);
                            if(Controller.this.map2 != null){
                                Controller.this.map2[row][col].setCode(0);
                            }
                            grid.setText("");
                        }
                        break;
                }


                int finalRow = row;
                int finalCol = col;
                //Grid Interaction
                grid.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Grid clickGrid = Controller.this.map[finalRow][finalCol];
                        int oldValue = clickGrid.getCode();
                        long uniqueKey = clickGrid.getUniqueKey();

                        //Cancel the prohibition
                        if(oldValue == 3){
                            if(currentValue == 4){
                                grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                                        CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                                grid.setBackground(new Background(new BackgroundFill(Color.WHITE,CornerRadii.EMPTY, Insets.EMPTY)));
                                grid.setText("");
                                clickGrid.setCode(0);
                                Controller.this.map2[finalRow][finalCol].setCode(0);
                                return;
                            }else {
                                return;
                            }
                        }

                        //Delete road or buildings
                        if(currentValue == 2){
                            //Delete road
                            if(oldValue == 1){
                                Controller.this.map[finalRow][finalCol].setCode(0);
                                grid.setBackground(new Background(new BackgroundFill(Color.WHITE,CornerRadii.EMPTY,Insets.EMPTY)));
                                if(map2 != null){
                                    map2[finalRow][finalCol].setCode(0);
                                }
                                return;
                            }

                            if(Controller.this.map[finalRow][finalCol].getCode() == 0){
                                return;
                            }
                            //Price reduction when deleting buildings
                            if(Integer.parseInt(showMax.getText()) > 0){
                                showMax.setText(Integer.parseInt(showMax.getText())
                                        - Integer.parseInt(customBuilding.get(Controller.this.map[finalRow][finalCol].getCode()).getPrice().getText())+"");
                            }
                            //Deleting buildings based on unique ID
                            for (int i = 0; i < map.length; i++) {
                                for (int j = 0; j < map[0].length; j++) {
                                    Grid curGrid = Controller.this.map[i][j];
                                    if(curGrid.getCode()>10 && curGrid.getUniqueKey() == uniqueKey){
                                        curGrid.setCode(currentValue);
                                        if(Controller.this.map2 != null){
                                            Controller.this.map2[i][j].setCode(currentValue);
                                        }
                                    }
                                }
                            }


                        }

                        if (currentValue == 4){
                            return;
                        }
                        clickGrid.setCode(currentValue);
                        Controller.this.map[finalRow][finalCol].setCode(currentValue);
                        if (Controller.this.map2!=null){
                            Controller.this.map2[finalRow][finalCol].setCode(currentValue);
                        }
                        showMap();
                    }
                });
                mapView.getChildren().add(grid);
            }
        }
    }

    //Create map pop-up window
    public void showCreate(){
        AnchorPane group = new AnchorPane();
        group.setPrefHeight(300);
        group.setPrefWidth(400);
        Stage stage = new Stage();
        stage.setScene(new Scene(group));
        TextField rows = new TextField("");
        Label rowLabel = new Label("Rows: ");
        rowLabel.setLayoutX(50);
        rowLabel.setLayoutY(50);
        rows.setPromptText("input row number");
        rows.setLayoutX(100);
        rows.setLayoutY(50);
        rows.setPrefWidth(200);

        Label colLabel = new Label("Cols:");
        colLabel.setLayoutX(50);
        colLabel.setLayoutY(150);
        TextField cols = new TextField("");
        cols.setPromptText("input col number");
        cols.setLayoutX(100);
        cols.setLayoutY(150);
        cols.setPrefWidth(200);
        group.getChildren().addAll(rowLabel,rows,colLabel,cols);

        Button create = new Button("Create");
        create.setLayoutX(150);
        create.setLayoutY(250);
        create.setPrefWidth(100);
        group.getChildren().add(create);
        create.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showMax.setText("0");

                String rowText = rows.getText();
                String colText = cols.getText();
                if(isInteger(rowText)&&isInteger(colText)){
                mapView.setPrefWidth(GRID_WIDTH * Integer.parseInt(rowText) + 10);
                mapView.setPrefHeight(GRID_WIDTH * Integer.parseInt(colText) + 10);
                map = new Grid[Integer.parseInt(rowText)][Integer.parseInt(colText)];
                for (int i = 0; i < Integer.parseInt(rowText); i++) {
                    for (int j = 0; j < Integer.parseInt(colText); j++) {
                            map[i][j] = new Grid();
                    }
                }
                map2 = null;
                stage.close();
                showMap();
                }else {
                    showMapError();
                }

            }
        });
        stage.show();
    }

    @FXML
    public void showAbout(){
        AnchorPane group = new AnchorPane();
        group.setPrefHeight(200);
        group.setPrefWidth(300);
        Stage stage = new Stage();
        stage.setScene(new Scene(group));
        Text info = new Text("Information");
        info.setFont(new Font(25));
        info.setLayoutX(85);
        info.setLayoutY(20);
        Text sName = new Text("   System for Area Planning" +
                "\n    Name: Qingbiao Song" +
                "\n Email:Q.Song8@newcastle.ac.uk"+
                "\n                 08.2022");
        sName.setFont(new Font(15));
        sName.setLayoutX(40);
        sName.setLayoutY(50);
        Button close = new Button("Close");
        close.setLayoutX(100);
        close.setLayoutY(150);
        close.setPrefWidth(100);
        group.getChildren().addAll(info,sName,close);


        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        stage.show();
    }

    public void showMapError(){
        AnchorPane group = new AnchorPane();
        group.setPrefHeight(200);
        group.setPrefWidth(300);
        Stage stage = new Stage();
        stage.setScene(new Scene(group));
        Text error = new Text("Error");
        error.setFont(new Font(25));
        error.setLayoutX(120);
        error.setLayoutY(30);
        Text errorInfo = new Text("Input map data error!");
        errorInfo.setFont(new Font(20));
        errorInfo.setLayoutX(60);
        errorInfo.setLayoutY(75);
        Button close = new Button("Close");
        close.setLayoutX(100);
        close.setLayoutY(150);
        close.setPrefWidth(100);
        group.getChildren().addAll(error, errorInfo,close);
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        stage.show();
    }

    public void showBuildingError(){
        AnchorPane group = new AnchorPane();
        group.setPrefHeight(200);
        group.setPrefWidth(300);
        Stage stage = new Stage();
        stage.setScene(new Scene(group));
        Text error = new Text("Error");
        error.setFont(new Font(25));
        error.setLayoutX(120);
        error.setLayoutY(30);
        Text errorInfo = new Text("Input buildings data error!");
        errorInfo.setFont(new Font(20));
        errorInfo.setLayoutX(40);
        errorInfo.setLayoutY(75);
        Button close = new Button("Close");
        close.setLayoutX(100);
        close.setLayoutY(150);
        close.setPrefWidth(100);
        group.getChildren().addAll(error, errorInfo,close);
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        stage.show();
    }



    public void showFail(){
        AnchorPane group = new AnchorPane();
        group.setPrefHeight(200);
        group.setPrefWidth(300);
        Stage stage = new Stage();
        stage.setScene(new Scene(group));
        Text error = new Text("Optimisation failure");
        error.setFont(new Font(20));
        error.setLayoutX(60);
        error.setLayoutY(30);
        Text errorInfo = new Text("Optimised buildings are too large\n     please note the map size");
        errorInfo.setFont(new Font(15));
        errorInfo.setLayoutX(40);
        errorInfo.setLayoutY(75);
        Button close = new Button("Close");
        close.setLayoutX(100);
        close.setLayoutY(150);
        close.setPrefWidth(100);
        group.getChildren().addAll(error, errorInfo,close);
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        stage.show();
    }

    @FXML
    public void exit(){
        System.exit(0);
    }

    //Determine if it is an integer
    public static boolean isInteger(String value){
        boolean flag = true;
        try{
            Integer.valueOf(value);
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }


     //record road and prohibition
    public void copyMap() {
        if(this.map2 == null){
            this.map2 = new Grid[map.length][map[0].length];
            for (int i = 0; i < this.map.length; i++) {
                for (int j = 0; j < this.map[0].length; j++) {
                    if(this.map[i][j].getCode() < 10){
                        this.map2[i][j] = this.map[i][j].copy();
                    }
                }
            }
        }

    }

    //record road and prohibition
    public void copyMap2(){

        if(this.map2 == null){
            return;
        }

        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                if(this.map2[i][j].getCode() < 10){
                    this.map[i][j] = this.map2[i][j].copy();
                }
            }
        }
    }
}
