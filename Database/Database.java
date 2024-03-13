package Database;

public class Database {
    private Integer key = null;
    private Integer value = null;

    public Database(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }
    
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Boolean isKeyInDatabase(Integer key) {
        return this.key.equals(key);
    }
}
