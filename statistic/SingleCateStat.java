package statistic;

public class SingleCateStat {

	String categoryName = "";
	double alpha , beta, gamma , delta, rec , pre , acc ;
	
	public SingleCateStat(String categoryName){
		this.categoryName = categoryName;
		this.alpha = 0;
		this.beta = 0;
		this.gamma = 0;
		this.delta = 0;
		this.rec = 0;
		this.pre = 0;
		this.acc = 0;
	
	}
	
	public String toString(){
		return categoryName +": alpha=" +alpha+"-beta="+beta+"-gamma="+gamma+"-delte="+delta+ "  R:"+rec
			+ " P:"+pre	+ " Acc:"+acc;
	}  
}
