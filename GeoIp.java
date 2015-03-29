import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;




public class GeoIp {
	
	private File  data;
	private String ip;
	private String region;
	private float logitude;
	private float latitude;
	
	public  GeoIp(String ip){
		this.ip=ip;
		readData();
	}
	
	
	public void readData(){
		try {
			data = new File("GeoLiteCity.dat");
			LookupService lookup = new LookupService(data,LookupService.GEOIP_MEMORY_CACHE);
			Location location = lookup.getLocation(ip);
			setLogitude(location.longitude);
			setLatitude(location.latitude);
			setRegion(location.region);
			System.out.println(region);
			System.out.println(logitude);
			System.out.println(latitude);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	


	public float getLogitude() {
		return logitude;
	}


	public void setLogitude(float logitude) {
		this.logitude = logitude;
	}


	public float getLatitude() {
		return latitude;
	}


	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}


	public String getRegion() {
		return region;
	}


	public void setRegion(String region) {
		this.region = region;
	}
}
