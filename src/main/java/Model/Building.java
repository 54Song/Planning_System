package Model;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;


public class Building{

    private TextField name;
    private TextField length;
    private TextField width;
    private TextField price;
    private ColorPicker color;
    private Double unitValue;
    private Integer code;
    private Integer area;

    public Building(String name, Integer length, Integer width, Integer price, ColorPicker color) {
        this.name = new TextField(name);
        this.length = new TextField(length+"");
        this.width = new TextField(width+"");
        this.price = new TextField(price+"");
        this.color = color;
    }

    public void setUnitValue(){
        this.unitValue = Double.parseDouble(price.getText())*1.0
                / (Integer.parseInt(length.getText()) * Integer.parseInt(width.getText()));
    }

    public Integer getArea() {
        return this.area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public TextField getName() {
        return name;
    }
    public void setName(TextField name) {
        this.name = name;
    }
    public TextField getLength() {
        return length;
    }
    public void setLength(TextField length) {
        this.length = length;
    }
    public TextField getWidth() {
        return width;
    }
    public void setWidth(TextField width) {
        this.width = width;
    }
    public TextField getPrice() {
        return price;
    }
    public Integer getComPrice(){
        return Integer.parseInt(this.price.getText());
    }
    public void setPrice(TextField price) {
        this.price = price;
    }
    public ColorPicker getColor() {
        return color;
    }
    public void setColor(ColorPicker color) {
        this.color = color;
    }
    public Double getUnitValue() {
        return unitValue;
    }
    public void setUnitValue(Double unitValue) {
        this.unitValue = unitValue;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
