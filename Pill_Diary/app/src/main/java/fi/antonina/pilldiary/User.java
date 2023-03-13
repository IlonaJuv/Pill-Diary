package fi.antonina.pilldiary;

public class User {
    private String name, email, pass, age;

    public User() {}

    public User(String name, String email, String pass, String age) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}