import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class NeetsCrawler {
	

	StringBuffer score_9_5=null;
	StringBuffer score_9_0=null;
	StringBuffer score_8_8=null;
	
	Map<String,String> _9_5=null;
	Map<String,String> _9_0=null;
	Map<String,String> _8_8=null;
	
	public NeetsCrawler() {
		
		this.score_9_0=new StringBuffer();
		this.score_9_5=new StringBuffer();
		this.score_8_8=new StringBuffer();
		
		this._9_0=new TreeMap<String,String>();
		this._9_5=new TreeMap<String,String>();
		this._8_8=new TreeMap<String,String>();
		
	}
	
	public  String httpRequest(String requestUrl) {
		
		StringBuffer buffer=null;
		BufferedReader reader=null;
		InputStream in=null;
		InputStreamReader read=null;
		HttpURLConnection conection=null;
		URL url=null;
		
		try {
			url=new URL(requestUrl);
			conection=(HttpURLConnection)url.openConnection();
			conection.setDoInput(true);
			conection.setRequestMethod("GET");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		try {
			
			//建立流
			if(conection.getResponseCode()==504) {
				conection=(HttpURLConnection)url.openConnection();
				conection.setDoInput(true);
				conection.setRequestMethod("GET");
			}
			in=conection.getInputStream();
			read=new InputStreamReader(in, "UTF-8");
			reader=new BufferedReader(read);
			
			//读取流
			buffer=new StringBuffer();
			String str=null;
			
			while((str=reader.readLine())!=null) {
				
				buffer.append(str);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 int code;
				try {
					code = conection.getResponseCode();
					System.out.println(code);
					System.out.println("连接超时");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  
				
				
			//conection=(HttpURLConnection)url.openConnection();
			
		}finally {
			
			
			    
			if(reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(read!=null) {
					try {
						read.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(in!=null) {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			}
			
		}
		
		return buffer.toString();
		
	}
	
	public  void  htmlFiter(String html) {
		
		
		String str1="";
		String str2="";
		
		
		
		Document d=Jsoup.parse(html);
		Elements e=d.select(".v_box1");
		
		for(int i=0;i<e.size();i++) {
			
			Document dd=Jsoup.parse(e.get(i).toString());
			String  aa=dd.select("a").attr("href");
			String res="http://neets.cc"+aa;
			String page2=httpRequest(res);
			
			this.htmlFiter2(page2);
			
			
		}
		
		
		
		
	}
	
	
	
	
	public  void  htmlFiter2(String html) {
		
		
		Document d=Jsoup.parse(html);
		Elements a=d.select(".txt_box");
		String str="";
		String str2="";
		float score;
		
		String aa=a.toString();
		d=Jsoup.parse(aa);
		aa=d.select(".title").text();
	
		
		Pattern p=Pattern.compile("(\\d\\.\\d)|(\\d)$");
		Matcher m=p.matcher(aa);
		if(m.find()) {
			str=m.group();
			score=Float.parseFloat(str);
			
				if(score>=8.8) {
					
					if(score>9.5) {
						_9_5.put(aa,str);
						
						
					}else if(score>=9.0) {
						_9_0.put(aa,str);
						
						
					}else  {
						
						_8_8.put(aa,str);
						
					}
					
					System.out.println(aa);
					
				
			}
				
			
			
			
		}
		
		
		
		
	}
	
	public  Map<String, String> sortMapByValue(Map<String, String> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(
				oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator());

		Iterator<Map.Entry<String, String>> iter = entryList.iterator();
		Map.Entry<String, String> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}



class MapValueComparator implements Comparator<Map.Entry<String, String>> {

	
	public int compare(Entry<String, String> me1, Entry<String, String> me2) {

		return me2.getValue().compareTo(me1.getValue());
	}
}
	public  void writeResult() {
		
		Map<String,String>  a=sortMapByValue(_9_5);
		Map<String,String> b =sortMapByValue(_9_0);
		Map<String,String> c =sortMapByValue(_8_8);
		String tmp="";
		for(String key:a.keySet()) {
			tmp=key+"\r\n";
			score_9_5.append(tmp);
			
		}
		
		for(String key:b.keySet()) {
			tmp=key+"\r\n";
			score_9_0.append(tmp);
			
		}
		
		for(String key:c.keySet()) {
			tmp=key+"\r\n";
			score_8_8.append(tmp);
			
		}
		
		
		try {
			
			FileWriter writer=new FileWriter("result.txt",true);
			
			writer.write("-------------评分9.5-10的作品--------------\n\n");
			writer.write(this.score_9_5.toString());
			writer.write("\n\n");

			writer.write("-------------评分9.0-9.5的作品-------------\n\n");
			writer.write(this.score_9_0.toString());
			writer.write("\n\n");

			writer.write("-------------评分8.8-9.0的作品-------------\n\n");
			writer.write(this.score_8_8.toString());
			writer.write("\n\n");
			
			writer.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static void main(String[] args	) {
		
		
		NeetsCrawler test = new NeetsCrawler();

		
		
		for(int i=1;i<=146;i++) {
			String url="http://neets.cc/category?state=1&page="+i+"&type=animation&country=&endYear=&startYear=&week=&order=";
			String html=test.httpRequest(url);
			test.htmlFiter(html);
			
		}
		
		test.writeResult();
		

	}

}






















