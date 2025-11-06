package app.Populator;

import app.config.HibernateConfig;
import app.entities.User;
import app.security.ISecurityDAO;
import app.security.SecurityDAO;

public class UserPopulator {

    public static void populateDefaultUsers() {
        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());

        dao.createRole("User");
        dao.createRole("Admin");

        User user = dao.createUser("Philip", "pass12345");
        System.out.println("Philip bcrypt: " + user.getPassword());

        User admin = dao.createUser("PhilipAdmin", "pass12345");
        System.out.println("PhilipAdmin bcrypt: " + admin.getPassword());

        dao.addUserRole("Philip", "User");
        dao.addUserRole("PhilipAdmin", "Admin");
    }

}
