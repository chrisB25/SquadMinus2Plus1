package SocialWiki.WikiPages;

import SocialWiki.Users.User;
import SocialWiki.Users.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Chris on 2/25/2017.
 * Unit tests for WikiPageRepository class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WikiPageRepositoryTest {

    @Autowired
    private WikiPageRepository wikiPageRepository;

    @Autowired
    private UserRepository userRepository;

    private ConcreteWikiPage testConcreteWikiPage1;
    private ConcreteWikiPage testConcreteWikiPage2;
    private ConcreteWikiPage testConcreteWikiPage3;
    private ConcreteWikiPage testConcreteWikiPage4;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @Test
    public void findByTitle() throws Exception {

        List<WikiPageWithAuthorProxy> testList = new ArrayList<>();
        testList.add(WikiPageWithAuthorProxy.getResult(testConcreteWikiPage3));
        testList.add(WikiPageWithAuthorProxy.getResult(testConcreteWikiPage4));
        WikiPageWithAuthorProxy testResult1 = WikiPageWithAuthorProxy.getResult(testConcreteWikiPage1);

        List<WikiPageWithAuthorProxy> pages = wikiPageRepository.findByTitleIgnoreCase("testTitle1");
        assertEquals("Failure - Number of pages found by findByTitleIgnoreCase('testTitle1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleIgnoreCase('testTitle1') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleIgnoreCase("tEStTiTLe1");
        assertEquals("Failure - Number of pages found by findByTitleIgnoreCase('tEStTiTLe1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleIgnoreCase('tEStTiTLe1') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleIgnoreCase("");
        assertEquals("Failure - Number of pages found by findByTitleIgnoreCase('') should be 0", 0, pages.size());
        pages.clear();

        pages = wikiPageRepository.findByTitleIgnoreCase("testTitlePair");
        assertEquals("Failure - Number of pages found by findByTitleIgnoreCase('testTitlePair') should be 2", 2, pages.size());
        assertTrue("Failure - First page found by findByTitleIgnoreCase('testTitlePair') is not correct", testList.contains(pages.get(0)));
        assertTrue("Failure - Second page found by findByTitleIgnoreCase('testTitlePair') is not correct", testList.contains(pages.get(1)));
        pages.clear();

    }

    @Test
    public void findByTitleAndAuthorAndContent() throws Exception {

        List<WikiPageWithAuthorProxy> testList = new ArrayList<>();
        testList.add(WikiPageWithAuthorProxy.getResult(testConcreteWikiPage3));
        testList.add(WikiPageWithAuthorProxy.getResult(testConcreteWikiPage4));

        WikiPageWithAuthorProxy testResult1 = WikiPageWithAuthorProxy.getResult(testConcreteWikiPage1);

        //Test when parameters are null or blank

        List<WikiPageWithAuthorProxy> pages = wikiPageRepository.findByTitleAndAuthorAndContent("","","");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('','','') should be 4", 4, pages.size());
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent(null,"","");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent(null,'','') should be 4", 4, pages.size());
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("",null,"");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('',null,'') should be 4", 4, pages.size());
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("","",null);
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('','',null) should be 4", 4, pages.size());
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent(null,null,null);
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent(null,null,null) should be 4", 4, pages.size());
        pages.clear();

        //Test finding single page by 1 parameter

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("testTitle1","","");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('testTitle1','','') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('testTitle1','','') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("", testUser1.getUserName(), "");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('',testUser1.getUserName(),'') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('',testUser1.getUserName(),'') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("","","testContent1");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('','','testContent1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('','','testContent1') is not correct", testResult1, pages.get(0));
        pages.clear();

        //Test finding multiple page by 1 parameter that is a substring and mixed casing

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("TitLEpair","","");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('TitLEpair','','') should be 2", 2, pages.size());
        assertTrue("Failure - Page 1 found by findByTitleAndAuthorAndContent('TitLEpair','','') is not correct", testList.contains(pages.get(0)));
        assertTrue("Failure - Page 2 found by findByTitleAndAuthorAndContent('TitLEpair','','') is not correct", testList.contains(pages.get(0)));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("",testUser3.getUserName().substring(4,testUser3.getUserName().length()).toUpperCase(),"");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('',testUser3.getUserName().substring(4,testUser3.getUserName().length()).toUpperCase(),'') should be 2", 2, pages.size());
        assertTrue("Failure - Page 1 found by findByTitleAndAuthorAndContent('',testUser3.getUserName().substring(4,testUser3.getUserName().length()).toUpperCase(),'') is not correct", testList.contains(pages.get(0)));
        assertTrue("Failure - Page 2 found by findByTitleAndAuthorAndContent('',testUser3.getUserName().substring(4,testUser3.getUserName().length()).toUpperCase(),'') is not correct", testList.contains(pages.get(0)));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("","","testConTENT");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('','','testConTENT') should be 4", 4, pages.size());
        pages.clear();

        //Test finding single page by multiple parameters

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("testTitle1", testUser1.getUserName(),"");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('testTitle1','testUser1.getUserName()','') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('testTitle1','testUser1.getUserName()','') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("testTitle1","","testContent1");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('testTitle1','','testContent1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('testTitle1','','testContent1') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("", testUser1.getUserName(), "testContent1");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('',testUser1.getUserName(),'testContent1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('',testUser1.getUserName(),'testContent1') is not correct", testResult1, pages.get(0));
        pages.clear();

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("testTitle1", testUser1.getUserName(),"testContent1");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('testTitle1','testUser1.getUserName()','testContent1') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('testTitle1','testUser1.getUserName()','testContent1') is not correct", testResult1, pages.get(0));
        pages.clear();

        //Test finding single page using title value that is within content

        pages = wikiPageRepository.findByTitleAndAuthorAndContent("testContent1","","");
        assertEquals("Failure - Number of pages found by findByTitleAndAuthorAndContent('testContent1','','') should be 1", 1, pages.size());
        assertEquals("Failure - Page found by findByTitleAndAuthorAndContent('testContent1','','') is not correct", testResult1, pages.get(0));
        pages.clear();

    }

    @Test
    public void findById() throws Exception {

        WikiPageWithAuthorAndContentProxy testResult1 = WikiPageWithAuthorAndContentProxy.getFullResult(testConcreteWikiPage1);

        WikiPageWithAuthorAndContentProxy page = wikiPageRepository.findById(testConcreteWikiPage1.getId());
        assertEquals("Failure - Page found by findById(testConcreteWikiPage1.getId()) is not correct", testResult1, page);

        page = wikiPageRepository.findById(-1L);
        assertEquals("Failure - Page found by findById(-1L) is not correct", null, page);

    }

    @Before
    public void setUp() throws Exception {
        testUser1 = new User("testUserName1", "testFirstName1", "testLastName1", "testEmail1", "testPassword1");
        testUser2 = new User("testUserName2", "testFirstName2", "testLastName2", "testEmail2", "testPassword2");
        testUser3 = new User("testUserName3", "testFirstName3", "testLastName3", "testEmail3", "testPassword3");
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);

        testConcreteWikiPage1 = new ConcreteWikiPage("testTitle1", "testContent1",testUser1);
        testConcreteWikiPage3 = new ConcreteWikiPage("testTitlePair", "testContent2",testUser3);

        testConcreteWikiPage1 = wikiPageRepository.save(testConcreteWikiPage1);
        testConcreteWikiPage3 = wikiPageRepository.save(testConcreteWikiPage3);

        testConcreteWikiPage2 = new ConcreteWikiPage("testTitle2", "testContent3", testConcreteWikiPage1.getId(),testUser2);
        testConcreteWikiPage4 = new ConcreteWikiPage("testTitlePair", "testContent4", testConcreteWikiPage3.getId(),testUser3);

        testConcreteWikiPage2 = wikiPageRepository.save(testConcreteWikiPage2);
        testConcreteWikiPage4 = wikiPageRepository.save(testConcreteWikiPage4);

    }

    @After
    public void tearDown() throws Exception {
        wikiPageRepository.deleteAll();
        userRepository.deleteAll();
    }
}