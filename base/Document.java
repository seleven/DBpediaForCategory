package base;

public class Document{
	String categoryName = null; 
	int documentID = 0; //Unique
	String documentName = null;
	String vectorPath = null;
	
	public Document(String categoryName, int documentID, String documentName,String vectorPath) {
		this.categoryName = categoryName;
		this.documentID = documentID;
		this.documentName = documentName;
		this.vectorPath = vectorPath;
	}
	public String getCategoryName() {
		return categoryName;
	}

	public int getDocumentID() {
		return documentID;
	}

	public String getDocumentName() {
		return documentName;
	}

	public String getVectorPath() {
		return vectorPath;
	}
}