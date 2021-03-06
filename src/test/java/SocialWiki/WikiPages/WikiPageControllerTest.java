package SocialWiki.WikiPages;

import SocialWiki.Users.User;
import SocialWiki.Users.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Chris on 2/24/2017.
 * Unit tests for WikiPageController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@Transactional
public class WikiPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WikiPageRepository wikiPageRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @After
    public void tearDown() throws Exception {
        wikiPageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Before
    public void setUp() throws Exception {
        testUser1 = new User("testUserName1", "testFirstName1", "testLastName1", "Test1@email.com", "testPassword1");
        testUser2 = new User("testUserName2", "testFirstName2", "testLastName2", "Test2@email.com", "testPassword2");
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);

    }

    @Test
    public void createWikiPage() throws Exception {

        // perform login to get session
        MvcResult result = this.mockMvc.perform(post("/login")
                .content("login="+ testUser1.getUserName() + "&pass=" + testUser1.getPassword())
                .contentType("application/x-www-form-urlencoded"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        //Cannot use Id when checking return string as Id is randomly generated value. Cannot creation date as there is no way to know exact time of creation

        MultiValueMap<String, String> params = new HttpHeaders();

        //Check for successful creation
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", "-1");
        result = this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("testTitle")))
                .andExpect(jsonPath("$.content", is("testContent")))
                .andExpect(jsonPath("$.parentID", is(-1)))
                .andExpect(jsonPath("$.views", is(0)))
                .andReturn();
        params.clear();

        Optional<String> idSubstring = Arrays.stream( result.getResponse().getContentAsString().split("[\\{\\,\\}]")).filter(s -> s.contains("id\":")).findFirst();
        String parentid = idSubstring.get().substring(5, idSubstring.get().length());

        //Check for successful edit with altered content
        params.add("title", "testTitle");
        params.add("content", "testContent2");
        params.add("parentID", parentid);
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("testTitle")))
                .andExpect(jsonPath("$.content", is("testContent2")))
                .andExpect(jsonPath("$.parentID", is(Integer.parseInt(parentid))))
                .andExpect(jsonPath("$.views", is(0)));
        params.clear();

        //Check for successful edit with altered title
        params.add("title", "testTitle2");
        params.add("content", "testContent");
        params.add("parentID", parentid);
        this.mockMvc.perform(post("/createWikiPage")
                .params(params)
                .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("testTitle2")))
                .andExpect(jsonPath("$.content", is("testContent")))
                .andExpect(jsonPath("$.parentID", is(Integer.parseInt(parentid))))
                .andExpect(jsonPath("$.views", is(0)));;
        params.clear();

        //Check for successful creation with long content string > 255 length
        params.add("title", "long content");
        params.add("content", "EzNFYkoP4Pk3vNkNENzWgAWANXVPSAkUORyz3ygTjADIXCMmdZDA7IGASMSfa24agfi9kj4Nw6sPCDUV3P9LKiZ6oFLo5Fj2FJ5EXbYmfYUdVKakZIts0R1kiEc799e0IEiIQZmlERkox6KLNRpOfEY8nTnrq9xjg2eJf0CojoGlKxgIp1stQZzikybuv6ng5OnnMDhFS5JLjiKM0P08gxDu4htYeUzSk90WxVEVDlAzfaeKKhfXjphctopZukFY");
        params.add("parentID", "-1");
        result = this.mockMvc.perform(post("/createWikiPage")
                .params(params)
                .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("long content")))
                .andExpect(jsonPath("$.content", is("EzNFYkoP4Pk3vNkNENzWgAWANXVPSAkUORyz3ygTjADIXCMmdZDA7IGASMSfa24agfi9kj4Nw6sPCDUV3P9LKiZ6oFLo5Fj2FJ5EXbYmfYUdVKakZIts0R1kiEc799e0IEiIQZmlERkox6KLNRpOfEY8nTnrq9xjg2eJf0CojoGlKxgIp1stQZzikybuv6ng5OnnMDhFS5JLjiKM0P08gxDu4htYeUzSk90WxVEVDlAzfaeKKhfXjphctopZukFY")))
                .andExpect(jsonPath("$.parentID", is(-1)))
                .andExpect(jsonPath("$.views", is(0)))
                .andReturn();
        params.clear();

        //Check for unsuccessful edit due to same title and content
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", parentid);
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to empty title
        params.add("title", "");
        params.add("content", "testContent");
        params.add("parentID", "-1");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to parent ID = 0
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", "0");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to parent ID = -2
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", "-2");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to no user session
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", "-2");
        this.mockMvc.perform(post("/createWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
        params.clear();

        //Check for successful creation with empty content
        params.add("title", "testTitle");
        params.add("content", "");
        params.add("parentID", "-1");
        this.mockMvc.perform(post("/createWikiPage")
                        .params(params)
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("testTitle")))
                .andExpect(jsonPath("$.content", is("")))
                .andExpect(jsonPath("$.parentID", is(-1)))
                .andExpect(jsonPath("$.views", is(0)));
        params.clear();

        //Check for unsuccessful creation due to missing title parameter
        params.add("content", "testContent");
        params.add("parentID", "-1");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to missing content parameter
        params.add("title", "testTitle");
        params.add("parentID", "-1");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to missing parentID parameter
        params.add("title", "testTitle");
        params.add("content", "testContent");
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for unsuccessful creation due to invalid user session
        params.add("title", "testTitle");
        params.add("content", "testContent");
        params.add("parentID", "-1");
        session.invalidate();
        this.mockMvc.perform(post("/createWikiPage")
                            .params(params)
                            .session(session))
                .andDo(print())
                .andExpect(status().isForbidden());
        params.clear();

    }

    @Test
    public void advancedSearchWikiPage() throws Exception {

        ConcreteWikiPage testConcreteWikiPage1 = new ConcreteWikiPage("testTitle1", "testContent1",testUser1);
        ConcreteWikiPage testConcreteWikiPage2 = new ConcreteWikiPage("testTitlePair", "testContent2",testUser2);

        testConcreteWikiPage1 = wikiPageRepository.save(testConcreteWikiPage1);
        testConcreteWikiPage2 = wikiPageRepository.save(testConcreteWikiPage2);

        ConcreteWikiPage testConcreteWikiPage3 = new ConcreteWikiPage("testTitlePair", "testContent3", testConcreteWikiPage2.getId(),testUser2);
        testConcreteWikiPage3 = wikiPageRepository.save(testConcreteWikiPage3);

        MultiValueMap<String, String> params = new HttpHeaders();

        //Check for unsuccessful search due to no parameters
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        //Check for successful search using title parameter
        params.add("title", "testTitle1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using user parameter
        params.add("user", testUser1.getUserName());
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using content parameter
        params.add("content", "testContent1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using title and user parameters
        params.add("title", "testTitle1");
        params.add("user", testUser1.getUserName());
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using title and content parameters
        params.add("title", "testTitle1");
        params.add("content", "testContent1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using user and content parameters
        params.add("user", testUser1.getUserName());
        params.add("content", "testContent1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search using all parameters
        params.add("title", "testTitle1");
        params.add("user", testUser1.getUserName());
        params.add("content", "testContent1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search resulting in multiple WikiPages found
        params.add("title", "testTitlePair");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage3.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(testConcreteWikiPage2.getId().intValue())));
        params.clear();

        //Check for successful search using all parameters with surrounding whitespace
        params.add("title", " testTitle1 \t");
        params.add("user", " " + testUser1.getUserName() + " \n");
        params.add("content", " testContent1 \r");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        testUser1.likePage(testConcreteWikiPage1);
        testUser1 = userRepository.save(testUser1);
        //Check for successful search that also returns amount of likes page has
        params.add("title", "testTitle1");
        params.add("user", testUser1.getUserName());
        params.add("content", "testContent1");
        this.mockMvc.perform(get("/advancedSearchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.[0].likes", is(1)));
        params.clear();

    }

    @Test
    public void retrieveWikiPage() throws Exception {

        ConcreteWikiPage testConcreteWikiPage1 = new ConcreteWikiPage("testTitle1", "testContent1",testUser1);

        testConcreteWikiPage1 = wikiPageRepository.save(testConcreteWikiPage1);

        MultiValueMap<String, String> params = new HttpHeaders();

        //Check for unsuccessful search due to no parameters
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        //Check for unsuccessful search using bad id
        params.add("id", "-1");
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for successful search using proper id
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.views", is(1)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.authorDeleted", is(false)));
        params.clear();

        // Check for successful search again, incrementing view counter
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.views", is(2)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.authorDeleted", is(false)));
        params.clear();

        // Check for successful search after user likes page and increments like counter
        testUser1.likePage(testConcreteWikiPage1);
        testUser1 = userRepository.save(testUser1);
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.views", is(3)))
                .andExpect(jsonPath("$.likes", is(1)))
                .andExpect(jsonPath("$.authorDeleted", is(false)));
        params.clear();

        // Check for successful search after user unlikes page
        testUser1.unlikePage(testConcreteWikiPage1);
        testUser1 = userRepository.save(testUser1);
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.views", is(4)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.authorDeleted", is(false)));
        params.clear();

        // Check for successful search after the author account has been deleted
        testUser1.delete();
        testUser1 = userRepository.save(testUser1);
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.views", is(5)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.authorDeleted", is(true)));
        params.clear();
    }

    @Test
    public void retrieveWikiPageHistory() throws Exception {
        ConcreteWikiPage testConcreteWikiPage1 = new ConcreteWikiPage("testTitle1", "testContent1",testUser1);
        ConcreteWikiPage testConcreteWikiPage2 = new ConcreteWikiPage("testTitle2", "testContent2",testUser2);

        testConcreteWikiPage1 = wikiPageRepository.save(testConcreteWikiPage1);
        testConcreteWikiPage2 = wikiPageRepository.save(testConcreteWikiPage2);

        ConcreteWikiPage testConcreteWikiPage3 = new ConcreteWikiPage("testTitle1", "testContent3", testConcreteWikiPage1.getId(),testUser1);
        ConcreteWikiPage testConcreteWikiPage4 = new ConcreteWikiPage("testTitle1", "testContent4", testConcreteWikiPage1.getId(),testUser1);

        testConcreteWikiPage3 = wikiPageRepository.save(testConcreteWikiPage3);
        testConcreteWikiPage4 = wikiPageRepository.save(testConcreteWikiPage4);

        ConcreteWikiPage testConcreteWikiPage5 = new ConcreteWikiPage("testTitle1", "testContent5", testConcreteWikiPage3.getId(),testUser2);
        ConcreteWikiPage testConcreteWikiPage6 = new ConcreteWikiPage("testTitle1", "testContent6", testConcreteWikiPage3.getId(),testUser2);

        testConcreteWikiPage5 = wikiPageRepository.save(testConcreteWikiPage5);
        testConcreteWikiPage6 = wikiPageRepository.save(testConcreteWikiPage6);

        MultiValueMap<String, String> params = new HttpHeaders();

        //Check for unsuccessful search due to no parameters
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        //Check for unsuccessful search due to invalid parameters
        params.add("id", "-1");
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        params.clear();

        //Check for successful search of hierarchy of single page
        params.add("id", testConcreteWikiPage2.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage2.getId().intValue())));
        params.clear();

        //Check for successful search of complex hierarchy using id of root. Also check for correct ascending order by Id
        params.add("id", testConcreteWikiPage1.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(testConcreteWikiPage3.getId().intValue())))
                .andExpect(jsonPath("$.[2].id", is(testConcreteWikiPage4.getId().intValue())))
                .andExpect(jsonPath("$.[3].id", is(testConcreteWikiPage5.getId().intValue())))
                .andExpect(jsonPath("$.[4].id", is(testConcreteWikiPage6.getId().intValue())));
        params.clear();

        //Check for successful search of complex hierarchy using id of node in middle. Also check for correct ascending order by Id
        params.add("id", testConcreteWikiPage4.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(testConcreteWikiPage3.getId().intValue())))
                .andExpect(jsonPath("$.[2].id", is(testConcreteWikiPage4.getId().intValue())))
                .andExpect(jsonPath("$.[3].id", is(testConcreteWikiPage5.getId().intValue())))
                .andExpect(jsonPath("$.[4].id", is(testConcreteWikiPage6.getId().intValue())));
        params.clear();

        //Check for successful search of complex hierarchy using id leaf node. Also check for correct ascending order by Id
        params.add("id", testConcreteWikiPage6.getId().toString());
        this.mockMvc.perform(get("/retrieveWikiPageHistory").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(testConcreteWikiPage3.getId().intValue())))
                .andExpect(jsonPath("$.[2].id", is(testConcreteWikiPage4.getId().intValue())))
                .andExpect(jsonPath("$.[3].id", is(testConcreteWikiPage5.getId().intValue())))
                .andExpect(jsonPath("$.[4].id", is(testConcreteWikiPage6.getId().intValue())));
        params.clear();
    }

    @Test
    public void searchWikiPage() throws Exception {

        ConcreteWikiPage testConcreteWikiPage1 = new ConcreteWikiPage("testTitle1", "testContent1",testUser1);
        ConcreteWikiPage testConcreteWikiPage2 = new ConcreteWikiPage("testTitlePair", "testContent2",testUser2);

        testConcreteWikiPage1 = wikiPageRepository.save(testConcreteWikiPage1);
        testConcreteWikiPage2 = wikiPageRepository.save(testConcreteWikiPage2);

        ConcreteWikiPage testConcreteWikiPage3 = new ConcreteWikiPage("testTitlePair", "testContent3", testConcreteWikiPage2.getId(),testUser2);
        testConcreteWikiPage3 = wikiPageRepository.save(testConcreteWikiPage3);

        MultiValueMap<String, String> params = new HttpHeaders();

        //Check for unsuccessful search due to no parameter
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        //Check for successful search
        params.add("title", "testTitle1");
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search with content substring parameter
        params.add("title", "content1");
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        //Check for successful search resulting in multiple WikiPages found
        params.add("title", "testTitlePair");
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage3.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(testConcreteWikiPage2.getId().intValue())));
        params.clear();

        //Check for successful search when parameter has surrounding whitespace
        params.add("title", "\n \r testTitle1 \t");
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())));
        params.clear();

        testUser1.likePage(testConcreteWikiPage1);
        testUser1 = userRepository.save(testUser1);
        //Check for successful search that also returns amount of likes page has
        params.add("title", "\n \r testTitle1 \t");
        this.mockMvc.perform(get("/searchWikiPage").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(testConcreteWikiPage1.getId().intValue())))
                .andExpect(jsonPath("$.[0].likes", is(1)));
        params.clear();

    }


}