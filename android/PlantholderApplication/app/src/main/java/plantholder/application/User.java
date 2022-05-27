package plantholder.application;

public class User {

    private String username;
    private String email;
    private String password;

    public User(String userName, String email, String password){
        this.username = userName;
        this.email = email;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}