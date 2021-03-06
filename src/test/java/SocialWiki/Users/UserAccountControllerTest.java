package SocialWiki.Users;

import SocialWiki.WikiPages.ConcreteWikiPage;
import SocialWiki.WikiPages.WikiPageRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Chris on 3/30/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserAccountControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WikiPageRepository pageRepo;

    private User user1;
    private User user2;
    private User user3;
    private ConcreteWikiPage page1;
    private ConcreteWikiPage page2;

    @Before
    public void setUp() throws Exception {
        user1 = userRepo.save(new User("testUserName1", "testFirstName1", "testLastName1", "Test1@email.com", "testPassword1"));
        user2 = userRepo.save(new User("testUserName3", "testFirstName3", "testLastName3", "Test3@email.com", "testPassword3"));
        user3 = userRepo.save(new User("testUserName4", "testFirstName4", "testLastName4", "Test4@email.com", "testPassword4"));
        page1 = pageRepo.save(new ConcreteWikiPage("testTitle1", "testContent1", user1));
        page2 = pageRepo.save(new ConcreteWikiPage("testTitle2", "testContent2", user2));
    }

    @After
    public void tearDown() throws Exception {
        pageRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void login() throws Exception {
        // perform successful login with userName
        MvcResult result = mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().value("user", "testUserName1"))
                .andExpect(cookie().maxAge("user", 86400))
                .andExpect(content().string(containsString("\"id\":")))
                .andExpect(content().string(containsString("\"userName\":\"testUserName1\"")))
                .andExpect(content().string(containsString("\"firstName\":\"testFirstName1\"")))
                .andExpect(content().string(containsString("\"lastName\":\"testLastName1\"")))
                .andExpect(content().string(containsString("\"email\":\"Test1@email.com\"")))
                .andExpect(content().string(containsString("\"password\":null")))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotEquals("Failure - successful login did not create a session for user", null, session);

        // perform successful login with email
        result = mockMvc.perform(post("/login")
                .content("login=Test1@email.com&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().value("user", "testUserName1"))
                .andExpect(cookie().maxAge("user", 86400))
                .andExpect(content().string(containsString("\"id\":")))
                .andExpect(content().string(containsString("\"userName\":\"testUserName1\"")))
                .andExpect(content().string(containsString("\"firstName\":\"testFirstName1\"")))
                .andExpect(content().string(containsString("\"lastName\":\"testLastName1\"")))
                .andExpect(content().string(containsString("\"email\":\"Test1@email.com\"")))
                .andExpect(content().string(containsString("\"password\":null")))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotEquals("Failure - successful login did not create a session for user", null, session);

        // perform successful login with extra parameter
        result = mockMvc.perform(post("/login")
                .content("login=Test1@email.com&pass=testPassword1&last=notALastName")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().value("user", "testUserName1"))
                .andExpect(cookie().maxAge("user", 86400))
                .andExpect(content().string(containsString("\"id\":")))
                .andExpect(content().string(containsString("\"userName\":\"testUserName1\"")))
                .andExpect(content().string(containsString("\"firstName\":\"testFirstName1\"")))
                .andExpect(content().string(containsString("\"lastName\":\"testLastName1\"")))
                .andExpect(content().string(containsString("\"email\":\"Test1@email.com\"")))
                .andExpect(content().string(containsString("\"password\":null")))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotEquals("Failure - successful login did not create a session for user", null, session);

        // perform unsuccessful login with an existing session
        mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());

        // perform unsuccessful login with missing "pass" parameter
        mockMvc.perform(post("/login")
                .content("login=testUserName1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        //perform unsuccessful login with missing "login" parameter
        mockMvc.perform(post("/login")
                .content("pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity());

        //perform unsuccessful login with both parameters missing
        mockMvc.perform(post("/login")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful login with empty "login" parameter
        mockMvc.perform(post("/login")
                .content("login=&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful login with empty "pass" parameter
        mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful login with both parameters empty
        mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful login with wrong credentials
        mockMvc.perform(post("/login")
                .content("login=testUserName2&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void create() throws Exception {
        // perform successful creation
        MvcResult result = mockMvc.perform(post("/signup")
                .content("user=testUserName2&first=testFirstName2&last=testLastName2&email=Test2@email.com&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().value("user", "testUserName2"))
                .andExpect(cookie().maxAge("user", 86400))
                .andExpect(content().string(containsString("\"id\":")))
                .andExpect(content().string(containsString("\"userName\":\"testUserName2\"")))
                .andExpect(content().string(containsString("\"firstName\":\"testFirstName2\"")))
                .andExpect(content().string(containsString("\"lastName\":\"testLastName2\"")))
                .andExpect(content().string(containsString("\"email\":\"Test2@email.com\"")))
                .andExpect(content().string(containsString("\"password\":null")))
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotEquals("Failure - successful creation of new user did not create a session for user", null, session);

        User user = userRepo.findByUserName("testUserName2");
        assertNotEquals("Failure - userControllerTest did not successfully create new user", null, user);

        // perform unsuccessful creation with an existing session
        mockMvc.perform(post("/signup")
                .content("user=testUserName2&first=testFirstName2&last=testLastName2&email=Test2@email.com&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());

        // perform unsuccessful creation with same userName
        mockMvc.perform(post("/signup")
                .content("user=testUserName2&first=testFirstName2&last=testLastName2&email=Test3@email.com&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isConflict());

        // perform unsuccessful creation with same email
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName2&last=testLastName2&email=Test2@email.com&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isConflict());

        // perform unsuccessful creation with same userName and email
        mockMvc.perform(post("/signup")
                .content("user=testUserName2&first=testFirstName2&last=testLastName2&email=Test2@email.com&pass=testPassword2")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isConflict());

        // perform unsuccessful creation with missing userName parameter
        mockMvc.perform(post("/signup")
                .content("first=testFirstName3&last=testLastName3&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with empty userName parameter
        mockMvc.perform(post("/signup")
                .content("user=&first=testFirstName3&last=testLastName3&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with missing firstName parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&last=testLastName3&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with empty firstName parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=&last=testLastName3&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with missing lastName parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with empty lastName parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&last=&email=Test3@email.com&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with missing email parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&last=testLastName3&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with empty email parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&last=testLastName3&email=&pass=testPassword3")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with missing password parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&last=testLastName3&email=Test3@email.com")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with empty password parameter
        mockMvc.perform(post("/signup")
                .content("user=testUserName3&first=testFirstName3&last=testLastName3&email=Test3@email.com&pass=")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with no parameters
        mockMvc.perform(post("/signup")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with an improper userName
        mockMvc.perform(post("/signup")
                .content("user=testUserN@me5&first=testFirstName5&last=testLastName5&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with an improper firstName
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstN@me5&last=testLastName5&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with an improper lastName
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName5&last=testLastN@me5&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with an improper email
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName5&last=testLastName5&email=testEmail5&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with an improper password
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName5&last=testLastName5&email=Test5@email.com&pass=testP@ssword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with a userName that is too long
        mockMvc.perform(post("/signup")
                .content("user=testUserName123456789012345678901&first=testFirstName5&last=testLastName5&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with a firstName that is too long
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName12345678901234567890&last=testLastName5&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with a lastName that is too long
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName5&last=testLastName123456789012345678901&email=Test5@email.com&pass=testPassword5")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());

        // perform unsuccessful creation with a password that is too long
        mockMvc.perform(post("/signup")
                .content("user=testUserName5&first=testFirstName5&last=testLastName123456789012345678901&email=Test5@email.com&pass=testPassword123456789012345678901")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void logout() throws Exception {
        // perform login to get session
        MvcResult result = mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        // perform successful logout with existing session
        mockMvc.perform(post("/logout")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().value("user", ""))
                .andExpect(cookie().maxAge("user", 0))
                .andExpect(status().isNoContent());

        // perform login to get session
        result = mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession(false);

        // perform unsuccessful logout with invalidated session
        session.invalidate();
        mockMvc.perform(post("/logout")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());

        // perform unsuccessful logout with no session
        mockMvc.perform(post("/logout")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // perform login to get session
        MvcResult result = mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        // perform successful delete with existing session
        mockMvc.perform(delete("/deleteUser")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().value("user", ""))
                .andExpect(cookie().maxAge("user", 0))
                .andExpect(status().isNoContent());

        // perform unsuccessful delete with invalidated session
        mockMvc.perform(delete("/deleteUser")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());

        // perform unsuccessful delete with no session
        mockMvc.perform(delete("/deleteUser")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isForbidden());

        // perform unsuccessful login with deleted credentials
        mockMvc.perform(post("/login")
                .content("login=testUserName1&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(content().string(""))
                .andExpect(status().isUnauthorized());

        // perform unsuccessful creation with deleted userName
        mockMvc.perform(post("/signup")
                .content("user=testUserName1&first=testFirstName1&last=testLastName1&email=Test1@email.com&pass=testPassword1")
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(cookie().doesNotExist("user"))
                .andExpect(status().isConflict());

        //login with user2
        result = mockMvc.perform(post("/login")
                .content("login=" + user2.getUserName() + "&pass=" + user2.getPassword())
                .contentType("application/x-www-form-urlencoded"))
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession(false);

        //have user 3 follow user 2
        user3.followUser(user2);
        user3 = userRepo.save(user3);

        // perform successful delete with user being followed
        mockMvc.perform(delete("/deleteUser")
                .contentType("application/x-www-form-urlencoded")
                .session(session))
                .andExpect(cookie().value("user", ""))
                .andExpect(cookie().maxAge("user", 0))
                .andExpect(status().isNoContent());

        user3 = userRepo.findOne(user3.getId());

        assertTrue("Failure - Deleting a user should remove it from all followers lists",user3.getFollowedUsers().size() == 0);
    }

}