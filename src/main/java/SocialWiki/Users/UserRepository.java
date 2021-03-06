package SocialWiki.Users;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by connor on 2/24/17.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Find the User with the corresponding userName
     * @param userName - the userName for the User that is being queried
     * @return the User that matches the userName
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.userName) = UPPER(:userName)")
    User findByUserName(@Param("userName") String userName);

    /**
     * Find the User with the corresponding userName and is not deleted
     * @param userName - the userName for the User that is being queried
     * @return the User that matches the userName that is not deleted
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.userName) = UPPER(:userName) AND u.isDeleted = false")
    User findByUserNameWithoutDeletions(@Param("userName") String userName);

    /**
     * Find the User with the corresponding email
     * @param email - the email for the User that is being queried
     * @return the User that matches the email
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.email) = UPPER(:email)")
    User findByEmail(@Param("email") String email);

    /**
     * Find the User with the corresponding userName and password
     * @param userName - the userName for the User that is being queried
     * @param password - the password for the User's account
     * @return the User that matches the userName and password
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.userName) = UPPER(:userName) AND u.password = :password AND u.isDeleted = false")
    User findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    /**
     * Find the User with the corresponding email and password
     * @param email - the email for the User that is being queried
     * @param password - the password for the User's account
     * @return the User that matches the email and password
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.email) = UPPER(:email) AND u.password = :password AND u.isDeleted = false")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    /**
     * Find the User with the corresponding userName or email
     * @param userName - the userName for the User that is being queried
     * @param email - the email for the User that is being queried
     * @return a list of Users that match the userName or email (should be a max of two since userName and email is unique)
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.userName) = UPPER(:userName) OR UPPER(u.email) = UPPER(:email)")
    List<User> findByUserNameOrEmail(@Param("userName") String userName, @Param("email") String email);

    /**
     * Find the Users that like a specific page
     * @param pageId - the id of the specific page that is being checked
     * @return a list of Users that like the page specified by pageId
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "INNER JOIN u.likedPages p " +
            "WHERE :pageId = p.id")
    List<User> findUsersByLikedPage(@Param("pageId") Long pageId);

    /**
     * Find the Users that a specific User follows
     * @param userName - the userName of the specific User
     * @return a list of Users that the User follows
     */
    @Query("SELECT u.followedUsers" +
                  " FROM SocialWiki.Users.User u " +
            "WHERE UPPER(u.userName) = UPPER(:userName)")
    List<User> findFollowedUsersByUsername(@Param("userName") String userName);

    /**
     * Find the Users that are following a specific User
     * @param user - the specific User who's followers are being checked
     * @return a list of Users that are following the specific User
     */
    @Query("SELECT u " +
            "FROM SocialWiki.Users.User u " +
            "INNER JOIN u.followedUsers f " +
            "WHERE :user IN (f)")
    List<User> findUsersFollowingUserByUser(@Param("user") User user);
}
