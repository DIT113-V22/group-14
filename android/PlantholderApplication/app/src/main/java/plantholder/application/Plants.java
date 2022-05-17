package plantholder.application;

public class Plants {

    public String id;
    public String species;
    public Integer row;
    public Integer column;
    public String health;

    public Plants(){} // needed for later

    public Plants (String ID,String species,int row, int column, String health ){
        this.id = ID;
        this.species = species;
        this.row = row;
        this.column = column;
        this.health = health;

    }

}
