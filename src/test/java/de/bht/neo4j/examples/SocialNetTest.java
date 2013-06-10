/**
 * Project: 	neo4j-samples
 * Package:		de.bht.neo4j.examples
 * Filename:	IndexSearchTest.java
 * Timestamp:	05.06.2013 | 10:42:21
 */
package de.bht.neo4j.examples;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * @author markus
 * 
 */
public class SocialNetTest
{
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String EMAIL_KEY = "email";
    private static final String URL_KEY = "url";

    private static final String DB_PATH = "/tmp/neo4j";
    private GraphDatabaseService graphDb;
    private Index<Node> userIndex;
    private Index<Node> websiteIndex;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        graphDb = new EmbeddedGraphDatabase(DB_PATH);
        userIndex = graphDb.index().forNodes("Users");
        websiteIndex = graphDb.index().forNodes("Websites");
        GraphUtil.registerShutdownHook(graphDb);

        final Transaction tx = graphDb.beginTx();
        try {
            /* cleanup first */
            GraphUtil.cleanUp(graphDb, userIndex);
            /*
             * create some users and index their properties
             */
            final Node me = createAndIndexUser(1, "me", "me@net.org");
            System.out.println("Created " + getUserToString(me));
            final Node user2 = createAndIndexUser(2, "user2", "user2@net.org");
            System.out.println("Created " + getUserToString(user2));
            final Node user3 = createAndIndexUser(3, "user3", "user3@net.org");
            System.out.println("Created " + getUserToString(user3));
            final Node user4 = createAndIndexUser(4, "user4", "user4@net.org");
            System.out.println("Created " + getUserToString(user4));
            final Node user5 = createAndIndexUser(5, "user5", "user5@net.org");
            System.out.println("Created " + getUserToString(user5));
            final Node user6 = createAndIndexUser(6, "user6", "user6@net.org");
            System.out.println("Created " + getUserToString(user6));
            final Node user7 = createAndIndexUser(7, "user7", "user7@net.org");
            System.out.println("Created " + getUserToString(user7));
            final Node user8 = createAndIndexUser(8, "user8", "user8@net.org");
            System.out.println("Created " + getUserToString(user8));

            /* friendships of user me: user2, user3, user4 */
            final Relationship meFriend1 = me.createRelationshipTo(user2, RelTypes.KNOWS);
            final Relationship meFriend2 = me.createRelationshipTo(user3, RelTypes.KNOWS);
            final Relationship meFriend3 = me.createRelationshipTo(user4, RelTypes.KNOWS);

            /* friendships of user2: user5, user8 */
            final Relationship user2Friend1 = user2.createRelationshipTo(user5, RelTypes.KNOWS);
            final Relationship user2Friend2 = user2.createRelationshipTo(user8, RelTypes.KNOWS);

            /* friendships of user3: user4, user6 */
            final Relationship user3Friend1 = user3.createRelationshipTo(user4, RelTypes.KNOWS);
            final Relationship user3Friend2 = user3.createRelationshipTo(user6, RelTypes.KNOWS);

            /* friendships of user4: user5 */
            final Relationship user4Friend1 = user4.createRelationshipTo(user5, RelTypes.KNOWS);

            /* friendships of user5: user8 */
            final Relationship user5Friend1 = user5.createRelationshipTo(user8, RelTypes.KNOWS);

            /* friendships of user6: user8 */
            final Relationship user6Friend1 = user6.createRelationshipTo(user7, RelTypes.KNOWS);

            /* friendships of user7: user8 */
            final Relationship user7Friend1 = user7.createRelationshipTo(user8, RelTypes.KNOWS);

            /* create website nodes */
            final Node url1 = createAndIndexWebsite(9, "http://www.web.de");
            System.out.println("Created " + getWebsiteToString(url1));
            final Node url2 = createAndIndexWebsite(10, "http://www.facebook.com");
            System.out.println("Created " + getWebsiteToString(url2));
            final Node url3 = createAndIndexWebsite(11, "http://www.twitter.com");
            System.out.println("Created " + getWebsiteToString(url3));
            final Node url4 = createAndIndexWebsite(12, "http://www.spiegel.de");
            System.out.println("Created " + getWebsiteToString(url4));
            final Node url5 = createAndIndexWebsite(13, "http://www.berlin.de");
            System.out.println("Created " + getWebsiteToString(url5));
            final Node url6 = createAndIndexWebsite(14, "http://www.heise.de");
            System.out.println("Created " + getWebsiteToString(url6));
            final Node url7 = createAndIndexWebsite(15, "http://www.nike.com");
            System.out.println("Created " + getWebsiteToString(url7));

            /* create likes relationships */
            /* user2 */
            final Relationship like21 = user2.createRelationshipTo(url1, RelTypes.LIKES);
            final Relationship like22 = user2.createRelationshipTo(url2, RelTypes.LIKES);

            /* user3 */
            final Relationship like31 = user3.createRelationshipTo(url1, RelTypes.LIKES);
            final Relationship like32 = user3.createRelationshipTo(url2, RelTypes.LIKES);
            final Relationship like33 = user3.createRelationshipTo(url3, RelTypes.LIKES);

            /* user4 */
            final Relationship like41 = user4.createRelationshipTo(url3, RelTypes.LIKES);
            final Relationship like42 = user4.createRelationshipTo(url4, RelTypes.LIKES);

            /* user5 */
            final Relationship like51 = user5.createRelationshipTo(url3, RelTypes.LIKES);
            final Relationship like54 = user5.createRelationshipTo(url5, RelTypes.LIKES);
            final Relationship like52 = user5.createRelationshipTo(url6, RelTypes.LIKES);
            final Relationship like53 = user5.createRelationshipTo(url7, RelTypes.LIKES);

            /* user6 */
            final Relationship like61 = user6.createRelationshipTo(url2, RelTypes.LIKES);
            final Relationship like62 = user6.createRelationshipTo(url4, RelTypes.LIKES);

            /* user7 */
            final Relationship like71 = user7.createRelationshipTo(url5, RelTypes.LIKES);
            final Relationship like72 = user7.createRelationshipTo(url6, RelTypes.LIKES);

            /* user8 */
            final Relationship like81 = user8.createRelationshipTo(url7, RelTypes.LIKES);

            tx.success();

        } finally {
            tx.finish();
        }
    }
    
    //@Test
    public void testFindFriendsDepth1WithCypher() {
        // find friends of user name 'me'
        String searchName = "me";
        System.out.println("searching for friends of " + searchName);
        final String query = String.format("START me=node:Users(name = '%s') " + "MATCH me-[:KNOWS]->friends "
                + "RETURN friends", searchName);
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(query);
        System.out.println(result);
    }

    //@Test
    public void testFindFriendsDepth2WithCypher() {
        // find 'friends of friends' of user name 'me'
        String searchName = "me";
        System.out.println("searching for friends of friends of " + searchName);
        final String query = String.format("START me=node:Users(name = '%s') "
                + "MATCH me-[:KNOWS]->friend-[:KNOWS]->friend2 "
                + " WHERE not(me-[:KNOWS]-friend2) RETURN distinct friend2 order by friend2.name ", searchName);
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(query);
        System.out.println(result);
    }

    //@Test
    public void testFindFriendsDepth3WithCypher() {
        // find 'friends of friends of friends' of user name 'me'
        String searchName = "me";
        System.out.println("searching for friends of friends of friends of " + searchName);
        final String query = String.format("START me=node:Users(name = '%s') "
                + "MATCH me-[:KNOWS]->friend-[:KNOWS]->friend2-[:KNOWS]->friend3 "
                + "RETURN distinct friend3 order by friend3.name", searchName);
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(query);
        System.out.println(result);
    }

    @Test
    public void testFindFriendsWithTraversalApi() {
        final String searchName = "me";
        System.out.println("searching for user with name: " + searchName);
        Node user = userIndex.get(NAME_KEY, searchName).getSingle();
        final TraversalDescription traversalDescription = Traversal.description()
                .relationships(RelTypes.KNOWS, Direction.OUTGOING)
                .evaluator(Evaluators.atDepth(1))
                .uniqueness(Uniqueness.NODE_GLOBAL);
        Iterable<Node> nodes = traversalDescription.traverse(user).nodes();
        for (Node friend : nodes) {
            System.out.println(friend.getProperty(NAME_KEY));
        }

    }

    // @Test
    public void testFindLikesOfFriends() {
        String searchName = "me";
        System.out.println("searching for likes of friends for " + searchName);
        final String query = String.format("START me=node:Users(name = '%s') "
                + "MATCH me-[:KNOWS]->friends-[:LIKES]->websites " + "RETURN websites.url, friends", searchName);
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(query);
        System.out.println(result);
    }

    // @Test
    public void testSitesMostLikedOfFriends() {
        String searchName = "me";
        System.out.println("search websites friends of " + searchName + " liked most:");
        final String query = String.format("START me=node:Users(name = '%s') "
                + "MATCH me-[:KNOWS]->friends-[:LIKES]->websites "
                + "RETURN websites.url, count(*) order by count(*) desc", searchName);
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(query);
        System.out.println(result);

    }

   

    // @Test
    public void testIndexSearch() {
        // search for user with id 3
        final int searchId = 2;
        System.out.println("searching for user with id: " + searchId);
        Node user = userIndex.get(ID_KEY, searchId).getSingle();
        System.out.println("found: " + getUserToString(user));
        assertEquals(searchId, user.getProperty(ID_KEY));

        // search for user with name 'user2'
        final String searchName = "user2";
        System.out.println("searching for user with name: " + searchName);
        user = userIndex.get(NAME_KEY, searchName).getSingle();
        System.out.println("found: " + getUserToString(user));
        assertEquals(searchName, user.getProperty(NAME_KEY));

        // search for user with email 'me@net.org'
        final String searchEmail = "me@net.org";
        System.out.println("searching for user with email: " + searchEmail);
        user = userIndex.get(EMAIL_KEY, searchEmail).getSingle();
        System.out.println("found: " + getUserToString(user));
        assertEquals(searchEmail, user.getProperty(EMAIL_KEY));
    }
    
    

    private String getUserToString(final Node user) {
        final StringBuilder sb = new StringBuilder("User [").append(user.getProperty(ID_KEY)).append(", ")
                .append(user.getProperty(NAME_KEY)).append(", ").append(user.getProperty(EMAIL_KEY)).append("]");
        return sb.toString();

    }

    private String getWebsiteToString(final Node website) {
        final StringBuilder sb = new StringBuilder("Website [").append(website.getProperty(ID_KEY)).append(", ")
                .append(website.getProperty(URL_KEY)).append("]");
        return sb.toString();

    }

    private Node createAndIndexUser(final int id, final String name, final String email) {
        final Node user = graphDb.createNode();
        user.setProperty(ID_KEY, id);
        user.setProperty(NAME_KEY, name);
        user.setProperty(EMAIL_KEY, email);
        userIndex.add(user, ID_KEY, id);
        userIndex.add(user, NAME_KEY, name);
        userIndex.add(user, EMAIL_KEY, email);
        return user;
    }

    private Node createAndIndexWebsite(final int id, final String url) {
        final Node website = graphDb.createNode();
        website.setProperty(ID_KEY, id);
        website.setProperty(URL_KEY, url);
        websiteIndex.add(website, ID_KEY, id);
        websiteIndex.add(website, URL_KEY, url);
        return website;
    }

    @After
    public void shutDown() {
        graphDb.shutdown();
    }

}
