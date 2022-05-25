package plantholder.application;

public class Plants {

    public String id;
    public String species;
    public Integer row;
    public Integer column;
    public String health;

    public Plants(){}

    public Plants (String id,String species,int row, int column, String health ){
        this.id = id;
        this.species = species;
        this.row = row;
        this.column = column;
        this.health = health;
    }

    public String getID() {
        return id;
    }

    public String getSpecies() {
        return species;
    }

    public String getHealth() {
        return health;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getColumn() {
        return column;
    }
}
