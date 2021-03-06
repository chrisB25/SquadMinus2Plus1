package SocialWiki.Users;

import SocialWiki.WikiPages.ConcreteWikiPage;
import SocialWiki.WikiPages.WikiPageRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by connor on 2/24/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WikiPageRepository pageRepo;

    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = new User("testUserName1", "testFirstName1", "testLastName1", "Test1@email.com", "testPassword1");
        user2 = new User("testUserName2", "testFirstName2", "testLastName2", "Test2@email.com", "testPassword2");
        user1 = userRepo.save(user1);
        user2 = userRepo.save(user2);
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }

    @Test
    public void findByUserName() throws Exception {
        User user = userRepo.findByUserName("testUserName1");
        assertEquals("Failure - userRepository query by userName does not return user1", user1, user);

        user = userRepo.findByUserName("testUserName2");
        assertEquals("Failure - userRepository query by userName does not return user2", user2, user);

        user = userRepo.findByUserName("testUserName3");
        assertEquals("Failure - userRepository found a user with a userName that does not exist", null, user);

        user = userRepo.findByUserName("");
        assertEquals("Failure - userRepository found a user with a blank userName", null, user);

        user = userRepo.findByUserName("testusername1");
        assertEquals("Failure - userRepository query by LOWER userName does not return user1", user1, user);

        user = userRepo.findByUserName("TESTUSERNAME1");
        assertEquals("Failure - userRepository query by UPPER userName does not return user1", user1, user);

        user = userRepo.findByUserName("TeStUsErNaMe1");
        assertEquals("Failure - userRepository query by MiXeD userName does not return user1", user1, user);
    }

    @Test
    public void findByUserNameWithoutDeletions() throws Exception {
        User user = userRepo.findByUserNameWithoutDeletions("testUserName1");
        assertEquals("Failure - userRepository query by userName that is not deleted does not return user1", user1, user);

        user = userRepo.findByUserNameWithoutDeletions("testUserName2");
        assertEquals("Failure - userRepository query by userName that is not deleted does not return user2", user2, user);

        user = userRepo.findByUserNameWithoutDeletions("testUserName3");
        assertEquals("Failure - userRepository found a user with a userName that does not exist", null, user);

        user = userRepo.findByUserNameWithoutDeletions("");
        assertEquals("Failure - userRepository found a user with a blank userName", null, user);

        user = userRepo.findByUserNameWithoutDeletions("testusername1");
        assertEquals("Failure - userRepository query by LOWER userName that is not deleted does not return user1", user1, user);

        user = userRepo.findByUserNameWithoutDeletions("TESTUSERNAME1");
        assertEquals("Failure - userRepository query by UPPER userName that is not deleted does not return user1", user1, user);

        user = userRepo.findByUserNameWithoutDeletions("TeStUsErNaMe1");
        assertEquals("Failure - userRepository query by MiXeD userName that is not deleted does not return user1", user1, user);

        user1.delete();
        user1 = userRepo.save(user1);

        user = userRepo.findByUserNameWithoutDeletions("testUserName1");
        assertEquals("Failure - userRepository query by userName that is deleted found a user still", null, user);
    }

    @Test
    public void findByEmail() throws Exception {
        User user = userRepo.findByEmail("Test1@email.com");
        assertEquals("Failure - userRepository query by email does not return user1", user1, user);

        user = userRepo.findByEmail("Test2@email.com");
        assertEquals("Failure - userRepository query by email does not return user2", user2, user);

        user = userRepo.findByEmail("Test3@email.com");
        assertEquals("Failure - userRepository found a user with an email that does not exist", null, user);

        user = userRepo.findByEmail("");
        assertEquals("Failure - userRepository found a user with a blank email", null, user);

        user = userRepo.findByEmail("test1@email.com");
        assertEquals("Failure - userRepository query by lower email does not return user1", user1, user);

        user = userRepo.findByEmail("TEST1@EMAIL.COM");
        assertEquals("Failure - userRepository query by UPPER email does not return user1", user1, user);

        user = userRepo.findByEmail("TeSt1@EmAiL.cOm");
        assertEquals("Failure - userRepository query by MiXeD email does not return user1", user1, user);
    }

    @Test
    public void findByUserNameAndPassword() throws Exception {
        User user = userRepo.findByUserNameAndPassword("testUserName1", "testPassword1");
        assertEquals("Failure - userRepository query by userName and password does not return user1", user1, user);

        user = userRepo.findByUserNameAndPassword("testUserName2", "testPassword2");
        assertEquals("Failure - userRepository query by userName and password does not return user2", user2, user);

        user = userRepo.findByUserNameAndPassword("testUserName1", "testPassword2");
        assertEquals("Failure - userRepository found a user with a userName-password pair that does not exist", null, user);

        user = userRepo.findByUserNameAndPassword("", "testPassword2");
        assertEquals("Failure - userRepository found a user with a blank userName", null, user);

        user = userRepo.findByUserNameAndPassword("testUserName2", "");
        assertEquals("Failure - userRepository found a user with a blank password", null, user);

        user = userRepo.findByUserNameAndPassword("", "");
        assertEquals("Failure - userRepository found a user with a blank userName and password", null, user);

        user = userRepo.findByUserNameAndPassword("testusername1", "testPassword1");
        assertEquals("Failure - userRepository query by LOWER userName does not return user1", user1, user);

        user = userRepo.findByUserNameAndPassword("TESTUSERNAME1", "testPassword1");
        assertEquals("Failure - userRepository query by UPPER userName does not return user1", user1, user);

        user = userRepo.findByUserNameAndPassword("TeStUsErNaMe1", "testPassword1");
        assertEquals("Failure - userRepository query by MiXeD userName does not return user1", user1, user);

        user = userRepo.findByUserNameAndPassword("testUserName1", "testPassword1");
        user.delete();
        userRepo.save(user);
        user = userRepo.findByUserNameAndPassword("testUserName1", "testPassword1");
        assertEquals("Failure - userRepository found a user that was deleted", null, user);
    }

    @Test
    public void findByEmailAndPassword() throws Exception {
        User user = userRepo.findByEmailAndPassword("Test1@email.com", "testPassword1");
        assertEquals("Failure - userRepository query by email and password does not return user1", user1, user);

        user = userRepo.findByEmailAndPassword("Test2@email.com", "testPassword2");
        assertEquals("Failure - userRepository query by email and password does not return user2", user2, user);

        user = userRepo.findByEmailAndPassword("Test1@email.com", "testPassword2");
        assertEquals("Failure - userRepository found a user with a email-password pair that does not exist", null, user);

        user = userRepo.findByEmailAndPassword("", "testPassword2");
        assertEquals("Failure - userRepository found a user with a blank email", null, user);

        user = userRepo.findByEmailAndPassword("Test2@email.com", "");
        assertEquals("Failure - userRepository found a user with a blank password", null, user);

        user = userRepo.findByEmailAndPassword("", "");
        assertEquals("Failure - userRepository found a user with a blank email and password", null, user);

        user = userRepo.findByEmailAndPassword("test1@email.com", "testPassword1");
        assertEquals("Failure - userRepository query by lower email does not return user1", user1, user);

        user = userRepo.findByEmailAndPassword("TEST1@EMAIL.COM", "testPassword1");
        assertEquals("Failure - userRepository query by UPPER email does not return user1", user1, user);

        user = userRepo.findByEmailAndPassword("TeSt1@EmAiL.cOm", "testPassword1");
        assertEquals("Failure - userRepository query by MiXeD email does not return user1", user1, user);
    }

    @Test
    public void findByUserNameOrEmail() throws Exception {
        List<User> users = userRepo.findByUserNameOrEmail("testUserName1", "Test1@email.com");
        assertEquals("Failure - userRepository query with existing userName and email for user1 could not be found", user1, users.get(0));

        users = userRepo.findByUserNameOrEmail("testUserName1", "Test3@email.com");
        assertEquals("Failure - userRepository query with existing userName for user1 could not be found", user1, users.get(0));

        users = userRepo.findByUserNameOrEmail("testUserName3", "Test1@email.com");
        assertEquals("Failure - userRepository query with existing email for user1 could not be found", user1, users.get(0));

        users = userRepo.findByUserNameOrEmail("testUserName1", "Test2@email.com");
        assertEquals("Failure - userRepository query with existing userName for user1 and email for user2 could not find both", 2, users.size());
        assertEquals("Failure - userRepository query with existing userName for user1 could not be found", user1, users.get(0));
        assertEquals("Failure - userRepository query with existing email for user2 could not be found", user2, users.get(1));

        users = userRepo.findByUserNameOrEmail("testUserName3", "Test3@email.com");
        assertEquals("Failure - userRepository query by userName or email returned users that do not exist", 0, users.size());

        users = userRepo.findByUserNameOrEmail("", "");
        assertEquals("Failure - userRepository query by userName or email returned users with blank userName or email", 0, users.size());

        users = userRepo.findByUserNameOrEmail("testusername1", "test2@email.com");
        assertEquals("Failure - userRepository query with existing lower userName for user1 and lower email for user2 could not find both", 2, users.size());
        assertEquals("Failure - userRepository query with existing lower userName for user1 could not be found", user1, users.get(0));
        assertEquals("Failure - userRepository query with existing lower email for user2 could not be found", user2, users.get(1));

        users = userRepo.findByUserNameOrEmail("TESTUSERNAME2", "TEST1@EMAIL.COM");
        assertEquals("Failure - userRepository query with existing UPPER userName for user2 and UPPER email for user1 could not find both", 2, users.size());
        assertEquals("Failure - userRepository query with existing UPPER userName for user2 could not be found", user1, users.get(0));
        assertEquals("Failure - userRepository query with existing UPPER email for user1 could not be found", user2, users.get(1));

        users = userRepo.findByUserNameOrEmail("TeStUsErNaMe1", "TeSt2@EmAiL.cOm");
        assertEquals("Failure - userRepository query with existing MiXeD userName for user1 and MiXeD email for user2 could not find both", 2, users.size());
        assertEquals("Failure - userRepository query with existing MiXeD userName for user1 could not be found", user1, users.get(0));
        assertEquals("Failure - userRepository query with existing MiXeD email for user2 could not be found", user2, users.get(1));
    }

    @Test
    public void findFollowedUsersByUsername() throws Exception {
        user1.followUser(user2);
        user1 = userRepo.save(user1);

        List<User> users = userRepo.findFollowedUsersByUsername(user1.getUserName());
        assertEquals("Failure - findFollowedUsersByUsername(user1.getUserName()) did not return correct user", user2, users.get(0));

        user1.unfollowUser(user2);
        user1 = userRepo.save(user1);

        users = userRepo.findFollowedUsersByUsername(user1.getUserName());
        assertEquals("Failure - findFollowedUsersByUsername(user1.getUserName()) did not return empty list when no users followed", 0, users.size());

    }

    @Test
    public void findUsersFollowingUserByUser() throws Exception {
        user1.followUser(user2);
        user1 = userRepo.save(user1);

        List<User> users = userRepo.findUsersFollowingUserByUser(user2);
        assertEquals("Failure - findUsersFollowingUserByUser(user2) did not return correct user", user1, users.get(0));

        user1.unfollowUser(user2);
        user1 = userRepo.save(user1);

        users = userRepo.findUsersFollowingUserByUser(user2);
        assertEquals("Failure - findUsersFollowingUserByUser(user2) did not return empty list when no users following", 0, users.size());

    }

    public void findUsersByLikedPage() throws Exception {
        ConcreteWikiPage page1 = pageRepo.save(new ConcreteWikiPage("testTitle1", "testContent1", user1));

        List<User> users = userRepo.findUsersByLikedPage(page1.getId());
        assertEquals("Failure - userRepository query for user that likes specified pageId should have found no users", 0, users.size());

        user1.likePage(page1);
        user1 = userRepo.save(user1);

        users = userRepo.findUsersByLikedPage(page1.getId());
        assertEquals("Failure - userRepository query for user that likes specified pageId should have found 1 user", 1, users.size());
        assertTrue("Failure - userRepository query for user that likes specified pageId not found", users.contains(user1));

        user2.likePage(page1);
        user2 = userRepo.save(user2);

        users = userRepo.findUsersByLikedPage(page1.getId());
        assertEquals("Failure - userRepository query for user that likes specified pageId should have found 2 users", 2, users.size());
        assertTrue("Failure - userRepository query for user1 that likes specified pageId not found", users.contains(user1));
        assertTrue("Failure - userRepository query for user2 that likes specified pageId not found", users.contains(user2));

        user1.unlikePage(page1);
        user1 = userRepo.save(user1);
        users = userRepo.findUsersByLikedPage(page1.getId());
        assertEquals("Failure - userRepository query for user that likes specified pageId should have found 1 user", 1, users.size());
        assertTrue("Failure - userRepository query for user that likes specified pageId not found", users.contains(user2));
    }
}