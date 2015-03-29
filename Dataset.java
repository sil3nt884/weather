
public class Dataset {
	private String id,lait,longi,area;
	
	public Dataset(String id, String laitude, String longitude, String area){
		this.setId(id);
		this.setLait(laitude);
		this.setLongi(longitude);
		this.setArea(area);
	}

	public String getLait() {
		return lait;
	}

	public void setLait(String lait) {
		this.lait = lait;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLongi() {
		return longi;
	}

	public void setLongi(String longi) {
		this.longi = longi;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
