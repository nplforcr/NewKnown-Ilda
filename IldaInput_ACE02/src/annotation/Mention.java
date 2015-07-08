package annotation;

public class Mention {
	private String id;
	private String type;
	private int extentSt;
	private int extentEd;
	private String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getExtentSt() {
		return extentSt;
	}
	public void setExtentSt(int extentSt) {
		this.extentSt = extentSt;
	}
	public int getExtentEd() {
		return extentEd;
	}
	public void setExtentEd(int extentEd) {
		this.extentEd = extentEd;
	}
}
