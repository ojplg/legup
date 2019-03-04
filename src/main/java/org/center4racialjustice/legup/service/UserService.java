package org.center4racialjustice.legup.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.hrorm.Dao;
import org.hrorm.Operator;
import org.hrorm.Where;

import java.util.List;

public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);

    private final ConnectionPool connectionPool;

    public UserService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User insertNewUserAndOrganization(String organizationName, String email, String password){
        log.info("Inserting new user + " + email + " with new organization " + organizationName);

        return connectionPool.runAndCommit( connection ->
            {
                Organization organization = new Organization();
                organization.setName(organizationName);

                Dao<Organization> orgDao = DaoBuilders.ORGANIZATIONS.buildDao(connection);
                orgDao.insert(organization);

                User user = User.createNewUser(email, password);
                user.setOrganization(organization);

                Dao<User> userDao = DaoBuilders.USERS.buildDao(connection);
                userDao.insert(user);

                return user;
            });
    }

    public Organization insertNewOrganization(String name){

        log.info("Inserting new organization " + name);

        return connectionPool.runAndCommit( connection ->
            {
                Organization organization = new Organization();
                organization.setName(name);

                Dao<Organization> dao = DaoBuilders.ORGANIZATIONS.buildDao(connection);
                dao.insert(organization);

                return organization;
            });
    }

    public User insertNewUser(String email, String password, Organization organization){
        log.info("Inserting new user " + email);

        return connectionPool.runAndCommit( connection ->
        {
            User user = User.createNewUser(email, password);
            user.setOrganization(organization);

            Dao<User> dao = DaoBuilders.USERS.buildDao(connection);
            dao.insert(user);

            return user;
        });
    }

    public User login(String email, String password){
        log.info("Logging in new user " + email);

        return connectionPool.useConnection( connection ->
                {
                    User template = new User();
                    template.setEmail(email);
                    Dao<User> dao = DaoBuilders.USERS.buildDao(connection);
                    User user = dao.selectByColumns(template, "EMAIL");
                    if ( user == null ){
                        log.info("no such user as " + email);
                        return null;
                    }
                    if ( user.correctPassword(password) ){
                        return user;
                    }
                    log.info("incorrect password for " + email);
                    return null;
                }
        );
    }

    public List<User> findUsersInOrganization(Organization org){
        return connectionPool.useConnection( connection ->
        {
            Dao<User> dao = DaoBuilders.USERS.buildDao(connection);
            return dao.select(new Where("organization_id", Operator.EQUALS, org.getId()));
        });
    }

}
