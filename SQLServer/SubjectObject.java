package SQLServer;
/**
 * @funcation store the statement of properties data 
 * @author Seleven
 * @date 2015年5月28日
 */
public class SubjectObject {
	private String subject;
	private String object;
	public SubjectObject(String subject,String object){
		this.subject = subject;
		this.object = object;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}

	
}
