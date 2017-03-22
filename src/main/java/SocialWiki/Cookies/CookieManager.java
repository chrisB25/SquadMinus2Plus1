package SocialWiki.Cookies;

import SocialWiki.Users.User;
import SocialWiki.WikiPages.ConcreteWikiPage;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Connor on 2017-03-08.
 */
public class CookieManager {

    /**
     * Get a new user cookie that corresponds to provided userName
     * @param userName - the value that will be stored in the user cookie
     * @return the new user cookie
     */
    public static Cookie getUserCookie(String userName) {
        // create the user cookie
        Cookie c = new Cookie("user", userName);
        c.setMaxAge(86400); // expires after a day

        return c;
    }

    /**
     * Get a cookie that effectively clears the client's user cookie
     * @return the clear user cookie
     */
    public static Cookie getClearUserCookie() {
        // create a cookie that will overwrite the user cookie
        Cookie c = new Cookie("user", "");
        c.setMaxAge(0); // expires immediately

        return c;
    }

    /**
     * Checks that the userName attached to the request's session matches the name in the user cookie also attached
     * @param request - an HTTP request that contains user data that needs to be validated
     * @return whether the session and cookie userNames are equal
     */
    public static boolean checkUserCookie(HttpServletRequest request) {
        // check that the session exists, if not, check is false
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        // get the user from the session
        User user = (User) session.getAttribute("user");

        // iterate over the request's cookies and check if the user one exists and matches the session user
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("user")) {
                    if (c.getValue().equals(user.getUserName())) {
                        return true;
                    }
                    return false;
                }
            }
        }

        // the cookie doesn't exist, so it doesn't need to be cleared
        return true;
    }

    /**
     * Get a cookie that signifies if the user likes the current page or not
     * @param request - an HTTP request that contains the session cookie used to identify the current user
     * @param id - the Id of the page that the user does or does not like
     * @return a cookie that signifies if the user likes the page, they do not, or clears the cookie
     */
    public static Cookie getIsLikedCookie(HttpServletRequest request, Long id) {
        Cookie c;

        // check that the session exists, if not, return a clearing cookie
        HttpSession session = request.getSession(false);
        if (session == null) {
            c = new Cookie("isLiked", "");
            c.setMaxAge(0);
            return c;
        }

        // get the user from the session
        User user = (User) session.getAttribute("user");

        // if the page id is within the liked page list, return true cookie
        for (ConcreteWikiPage page : user.getLikedPages()) {
            if (page.getId().equals(id)) {
                c = new Cookie("isLiked", "true");
                c.setMaxAge(86400);
                return c;
            }
        }

        // the page is not liked, so return false cookie
        c = new Cookie("isLiked", "false");
        c.setMaxAge(86400);
        return c;
    }
}
