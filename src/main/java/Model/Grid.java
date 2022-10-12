package Model;

public class Grid {

    private int code;
    private long uniqueKey;

    public Grid(){
        this.code = 0;
    }

    public Grid copy(){
        Grid newMap = new Grid();
        newMap.code = code;
        newMap.uniqueKey = uniqueKey;
        return newMap;
    }

    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public long getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(long uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
