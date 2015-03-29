import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class Program extends JFrame implements ActionListener {
	
	private JLabel day1  = new JLabel("day1");
	private JLabel day2  = new JLabel("day2");
	private JLabel day3  = new JLabel("day3");
	private JLabel day4  = new JLabel("day4");
	private JLabel day5  = new JLabel("day5");
	private JLabel ip = new JLabel();
	private JLabel [] Max = new JLabel[5];
	private JLabel[] Min = new JLabel[5];
	private JLabel[] Wind = new JLabel[5];
	private JButton get = new JButton("Get weather");
	private JPanel panel = new JPanel();
	private JSeparator separator = new JSeparator();
	private JSeparator separator2 = new JSeparator();
	private URL locations;
	private DataInputStream read;
	private BufferedReader reader;
	File tempXML;
	ArrayList<Dataset> datalist = new ArrayList<Dataset>();
	
	
	
	
	
	public void createTable(){
		for(int i=0; i<5; i++){
			Max[i]= new JLabel("Max  0");
			Min[i]= new JLabel("Min  0");
			Wind[i]= new JLabel("Wind 0");
		}
	}
	
	
	
	public Program(){
		createTable();
		panel.add(day1);
		panel.add(Max[0]);
		panel.add(Min[0]);
		panel.add(Wind[0]);
		panel.add(day2);
		panel.add(Max[1]);
		panel.add(Min[1]);
		panel.add(Wind[1]);
		panel.add(day3);
		panel.add(Max[2]);
		panel.add(Min[2]);
		panel.add(Wind[2]);
		panel.add(day4);
		panel.add(Max[3]);
		panel.add(Min[3]);
		panel.add(Wind[3]);
		panel.add(day5);
		panel.add(Max[4]);
		panel.add(Min[4]);
		panel.add(Wind[4]);
		ip.setText("Current IP: "+getPublicIpAddress());
		panel.add(ip);
		get.addActionListener(this);
		panel.add(get);
		panel.setLayout(new GridLayout(6,3));
		add(panel);
		pack();
		setResizable(false);
		setTitle("Weather station");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			locations= new URL("http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/xml/sitelist?key=2684dad5-4bab-42b4-8bde-712e0696f498");
			read = new DataInputStream(locations.openStream());
			reader = new BufferedReader(new InputStreamReader(read));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	public static void main(String[] args) {
		 new Program();
	}
	
	
	public void createTempXML(String line) throws IOException, ParserConfigurationException, SAXException{
		tempXML = new File("tempXML.xml");
		if(tempXML.exists()==false){
		String xml=	line.trim().replaceFirst("^([\\W]+)<","<");
		String xmls="<"+xml;
		byte[] bytes = xmls.getBytes();
		BufferedWriter temp = new BufferedWriter(new FileWriter(tempXML));
		for(int i=0; i<bytes.length; i++){
			temp.write(bytes[i]);
			temp.flush();
		}
		temp.close();
		}
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(new File("tempXML.xml"));
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("Location");
		for(int i=0; i<nList.getLength(); i++){
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE){
				Element element = (Element) nNode;
				String id=element.getAttribute("id");
				String laitude=element.getAttribute("latitude");
				String longitude=element.getAttribute("longitude");
				String area=element.getAttribute("unitaryAuthArea");
				datalist.add(new Dataset(id,laitude,longitude,area));
				
			}
		}
		System.out.println("list added");
		
		GeoIp location =new GeoIp(this.getPublicIpAddress());
		WeatherInputStream control= new WeatherInputStream(""+getWeatherStationID(location.getLatitude(),location.getLogitude()));
		control.setWeather(day1, day2, day3, day4, day5,Max,Min,Wind);
		
		
	}
	
	
	
	public int getWeatherStationID(float laitude,float longitude){
		Float d = Float.MAX_VALUE;	
		Float d2 = Float.MAX_VALUE;
		int id = 0;
		ArrayList<Float> dis = new ArrayList<Float>();
		ArrayList<Float> dis2 = new ArrayList<Float>();
		
		for(int i=0; i<datalist.size(); i++){
			float lait=Float.parseFloat(datalist.get(i).getLait());
			float logi=Float.parseFloat(datalist.get(i).getLongi());
			float distace1 = Math.abs(lait-laitude);
			dis.add(distace1);
			float distance2 = Math.abs(logi-longitude);
			dis2.add(distance2);
		}
		for(int i=0; i<dis.size(); i++){
			if(d > (float)dis.get(i) &&  d2 > (float)dis2.get(i)){
				d=(float)dis.get(i);
				d2=(float)dis.get(i);
				id=i;
			}
		}
		return Integer.parseInt(datalist.get(id).getId());
		
	}


	
	public String getPublicIpAddress() {
		try {
			URL ip = new URL("http://checkip.amazonaws.com/");
			BufferedReader reader = new BufferedReader(new InputStreamReader(ip.openStream()));
			return reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	}



	/*
	public void listProccess(String text){
	 Pattern pattern =Pattern.compile("\\b"+text);
	 Matcher [] matcher  = new Matcher [datalist.size()];
		for(int i=0; i<datalist.size(); i++){
			System.out.println("scanning...");
			matcher[i]= pattern.matcher(datalist.get(i).getArea());
			
		}
		System.out.println("scanning...complete");
		for(int j =0; j<matcher.length; j++){
			System.out.println("creatig");
			new Thread(new Stringer(matcher[j])).start();
		}
		
		
	}*/


	@Override
	public void actionPerformed(ActionEvent e) {
		String line=null;
		if(e.getSource()==get){
			try {
				while(reader.read()!=-1){
					line = reader.readLine();
					
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			createTempXML(line);
		} catch (IOException | ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
