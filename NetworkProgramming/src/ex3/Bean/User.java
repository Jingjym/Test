package ex3.Bean;

import java.io.Serializable;

/**
 * @author Jing Yumeng
 * @version 1.0
 * @see java.io.Serializable
 */
public class User implements Serializable {
    private String name;
    private String password;

    public User(String name, String password) {
        super();
        this.name = name;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!name.equals(user.name)) return false;
        return password != null ? password.equals(user.password) : user.password == null;
    }

    @Override
    public String toString() {
        return "[name: " + name + ",password:" + password + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


