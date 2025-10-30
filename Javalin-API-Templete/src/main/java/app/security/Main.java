package app.security;

import app.config.HibernateConfig;
import app.entities.Role;
import app.exceptions.ValidationException;
import app.entities.User;
import jakarta.persistence.*;

public class Main {

    // TODO: Maybe an loader for the user?
    public static void main(String[] args) {
        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());

        User user = dao.createUser("user1", "pass12345");
        System.out.println(user.getUserName() + ": " + user.getPassword());
        Role role = dao.createRole("User");

        try {
            User updatedUser = dao.addUserRole("user1", "User");
            System.out.println(updatedUser);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        try {
            User validatedUser = dao.getVerifiedUser("user1", "pass12345");
            System.out.println("User was validated: " + validatedUser.getUserName());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}


