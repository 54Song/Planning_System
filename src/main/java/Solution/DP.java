package Solution;

import Model.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DP {

    private int value;//curr map value

    private Grid[][] grid;//curr map with building

    private List<Integer> codeList = new ArrayList<>();

    public void setCodeList(List<Integer> codeList){
        this.codeList = codeList;
    }

    public List<Integer> getCodeList(){
        return codeList;
    }

    public void addCode(int code){
        if(this.codeList == null){
            this.codeList = new ArrayList<>();
        }
        this.codeList.add(code);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Grid[][] getBlock() {
        return grid;
    }

    public void setBlock(Grid[][] grid) {
        this.grid = grid;
    }

    @Override
    public boolean equals(Object o) {
       if (this == o)return true;
       if (!(o instanceof DP)) return false;
       DP dp = (DP) o;
       return getValue().equals(dp.getValue())&& Objects.equals(getCodeList(),dp.getCodeList());

    }

    @Override
    public int hashCode() {
        return getValue();
    }
}
