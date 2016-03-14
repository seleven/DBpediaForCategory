package neo4jWDSN;

import org.neo4j.graphdb.Label;

public class DbpediaLabel implements Label {
	String myLabelName = null;

	public DbpediaLabel(String myLabelName){
		this.myLabelName = myLabelName;
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return myLabelName;
	}
}
