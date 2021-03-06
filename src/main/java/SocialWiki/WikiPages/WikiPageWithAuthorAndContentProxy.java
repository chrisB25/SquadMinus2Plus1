package SocialWiki.WikiPages;

/**
 * Created by Chris on 3/4/2017.
 * Proxy for ConcreteWikiPage that exposes the username of the authoring user and the WikiPages contents
 */
public class WikiPageWithAuthorAndContentProxy extends WikiPageWithAuthorProxy {

    /**
     * Constructs WikiPageWithAuthorAndContentProxy for the provided WikiPage
     * @param realWikiPage - WikiPage that requires proxy
     */
    public WikiPageWithAuthorAndContentProxy(ConcreteWikiPage realWikiPage) {
        super(realWikiPage);
    }

    /**
     * Get the contents of the WikiPage
     * @return the contents of the WikiPage
     */
    public String getContent() {
        return realWikiPage.getContent();
    }

    /**
     * Get the flag that signifies if the author account is deleted or not
     * @return boolean that signifies if the account is deleted or not
     */
    public Boolean isAuthorDeleted() {
        return realWikiPage.getAuthor().isDeleted();
    }

    /**
     * Creates a WikiPageWithAuthorAndContentProxy object from te given WikiPage
     * @param page - The WikiPage to create a WikiPageWithAuthorAndContentProxy from
     * @return The associated WikiPageWithAuthorAndContentProxy from the WikiPage
     */
     public static WikiPageWithAuthorAndContentProxy getFullResult(ConcreteWikiPage page) {
         return new WikiPageWithAuthorAndContentProxy(page);
     }

}
