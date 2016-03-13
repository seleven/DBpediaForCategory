package classification;

public class DocVector {
	public String	categroyName;
	public String	docName;
	public double[]	vector;

	public DocVector(String categoryName,String docName,double[] vector){
		this.categroyName = categoryName;
		this.docName = docName;
		this.vector = vector;
	}
}