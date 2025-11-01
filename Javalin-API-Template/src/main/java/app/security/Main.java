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

        User user = dao.createUser("Philip", "pass12345");
        System.out.println(user.getUserName() + ": " + user.getPassword());
        Role role = dao.createRole("User");

        User admin = dao.createUser("PhilipAdmin","pass12345");
        System.out.println(admin.getUserName()+": "+admin.getPassword());
        Role roleAdmin = dao.createRole("Admin");

        try {
            User updatedUser = dao.addUserRole("Philip", "User");
            System.out.println(updatedUser);
            User updatedAdmin = dao.addUserRole("PhilipAdmin", "Admin");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        try {
            User validatedUser = dao.getVerifiedUser("Philip", "pass12345");
            System.out.println("User was validated: " + validatedUser.getUserName());
            User  validatedAdmin = dao.getVerifiedUser("PhilipAdmin", "pass12345");
            System.out.println("Admin was validated: " + validatedAdmin.getUserName());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}


