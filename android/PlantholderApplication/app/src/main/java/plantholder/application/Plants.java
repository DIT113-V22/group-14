package plantholder.application;

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
