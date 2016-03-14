/**
 * 
 */
package neo4jWDSN;

import org.neo4j.graphdb.RelationshipType;

/**
 * @function this enum Relationships include 13  properties that WDSN contains
 * @author Administrator
 * @data 2015年7月17日
 */
public enum Relationships implements RelationshipType {
	
	IS_DBPEDIA_OWL_WIKI_OF,
	DCTERMS_SUBJECT,
	IS_DCTERMS_SUBJECT_OF,
	RDFS_SUBCLASSOF,
	IS_RDFS_SUBCLASSOF_OF,
	SKOS_BROADER,
	IS_SKOS_BROADER_OF;
	
}
