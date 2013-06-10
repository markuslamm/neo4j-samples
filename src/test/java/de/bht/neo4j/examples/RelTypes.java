/**
 * Project: 	neo4j-samples
 * Package:		de.bht.neo4j
 * Filename:	RelTypes.java
 * Timestamp:	04.06.2013 | 17:54:56
 */
package de.bht.neo4j.examples;

import org.neo4j.graphdb.RelationshipType;

/**
 * @author markus
 *
 */
public enum RelTypes implements RelationshipType {
    KNOWS, LIKES 
}
