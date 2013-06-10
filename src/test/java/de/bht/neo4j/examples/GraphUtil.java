/**
 * Project: 	neo4j-samples
 * Package:		de.bht.neo4j
 * Filename:	GraphUtil.java
 * Timestamp:	04.06.2013 | 16:25:22
 */
package de.bht.neo4j.examples;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * @author markus
 * 
 */
public class GraphUtil
{
    @SuppressWarnings("deprecation")
    public static void cleanUp(final GraphDatabaseService graphDb, final Index<Node> nodeIndex) {
        for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
            for (Relationship rel : node.getRelationships()) {
                rel.delete();
            }
            nodeIndex.remove(node);
            node.delete();
        }
    }

    public static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
