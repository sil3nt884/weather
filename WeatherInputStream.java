import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WeatherInputStream {

	private String postalc;
	private URL connect;
	private DataInputStream read;
	private BufferedReader reader;
	private String key = "2684dad5-4bab-42b4-8bde-712e0696f498";
	private File tempXML;
	private String[] day = new String[5];
	private String[] Max = new String[5];
	private String[] Min = new String[5];
	private String[] wind = new String[5];
	ArrayList<String> maxList =  new ArrayList<String>();
	ArrayList<String> minList = new ArrayList<String>();
	ArrayList<String> windList = new ArrayList<String>();
	ArrayList <WeatherCollection> wList = new ArrayList<WeatherCollection>();
	String date[] = new String[5];


	public WeatherInputStream(String postc) {
		postalc = postc;
		try {
			connect = new URL(
					"http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/"
							+ postalc + "?res=daily&key=" + key);
			read = new DataInputStream(connect.openStream());
			reader = new BufferedReader(new InputStreamReader(read));
			run();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setWeather(JLabel day1, JLabel day2, JLabel day3, JLabel day4, JLabel day5 ,JLabel [] max ,JLabel [] Min, JLabel[] win){
		day1.setText(day[0]);
		day2.setText(day[1]);
		day3.setText(day[2]);
		day4.setText(day[3]);
		day5.setText(day[4]);
		for(int i=0; i<max.length; i++){
			max[i].setText("Max: "+Max[i]+"c");
		}
		for(int i=0; i<Min.length; i++){
			Min[i].setText("Min: "+this.Min[i]+"c");
		}
		for(int i=0; i<wind.length; i++){
			win[i].setText("Wind: "+wind[i]+"mph");
		}
	}

	public String getPostalc() {
		return postalc;
	}

	public void createWeatherXML(String line) throws IOException,
			ParserConfigurationException, SAXException {
		tempXML = new File("weather.xml");
			String xml = line.trim().replaceFirst("^([\\W]+)<", "<");
			String xmls = "<" + xml;
			byte[] bytes = xmls.getBytes();
			BufferedWriter temp = new BufferedWriter(new FileWriter(tempXML));
			for (int i = 0; i < bytes.length; i++) {
				temp.write(bytes[i]);
				temp.flush();
			}
			
			
			
		


		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(new File("weather.xml"));
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("Period");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) nNode;
				String dates = element.getAttribute("value").replace("z", "").replace("-", "");
				System.out.println(dates);
				date[i] = dates;

			}
		}
		NodeList day = doc.getElementsByTagName("Rep");
		for (int i = 0; i < day.getLength(); i++) {
			Node nNode = day.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) nNode;
			String max=element.getAttribute("Dm");
			maxList.add(max);
			String min = element.getAttribute("FDm");
			minList.add(min);
			String wind=element.getAttribute("S");
			windList.add(wind);
			
			
			
			}
		}

		try {
			collectInfo();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	public void collectInfo() throws ParseException {

		for (int i = 0; i <date.length; i++) {
			String d=date[i].replaceAll("Z", "").replaceFirst("-", "").trim();
			DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat targetFormat = new SimpleDateFormat("EEEE");
			Date date = originalFormat.parse(d);
			String formattedDate = targetFormat.format(date); 
			//System.out.println(formattedDate);
			day[i]=formattedDate;
		}
		for(int i=0; i<maxList.size(); i++){
			if(maxList.get(i)==""){
				maxList.remove(i);
			}	
		}
		for(int i=0; i<maxList.size(); i++){
			Max[i]=maxList.get(i);
		}
		
		
		for(int i=0; i<minList.size(); i++){
			if(minList.get(i)==""){
				minList.remove(i);
			}	
		}
		
		for(int i=0; i<minList.size(); i++){
			Min[i]=minList.get(i);
		}
		
		for(int i=0; i<windList.size(); i++){
			if((i & 1)==0){
				windList.remove(i);
			}
		}
		windList.remove(windList.size()-1);
		for(int i=0; i<windList.size(); i++){
			wind[i]=windList.get(i);
		}
		
	
		
	}

	public void setPostalc(String postalc) {
		this.postalc = postalc;
	}

	public void run() {

		try {
			while (reader.read() != -1) {
				String line = reader.readLine();
				createWeatherXML(line);
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
