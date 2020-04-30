package Controllers;

import Entity.User;
import javafx.util.Callback;

public class MyCallback implements Callback<User, User> {
    @Override
    public User call(User user) {
        return user;
    }
}
