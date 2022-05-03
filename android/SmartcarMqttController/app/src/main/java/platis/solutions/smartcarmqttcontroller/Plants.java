package platis.solutions.smartcarmqttcontroller;

public class Plants {

    private String ID;
    private String species;
    private Integer row;
    private Integer column;
    private String health;

    public Plants(){} // needed for later

    public Plants (String ID,String species,int row, int column, String health ){
        this.ID = ID;
        this.species = species;
        this.row = row;
        this.column = column;
        this.health = health;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }
}
