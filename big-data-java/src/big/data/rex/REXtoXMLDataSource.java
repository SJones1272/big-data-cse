package big.data.rex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import big.data.DataSource;
import big.data.util.XML;
import big.data.xml.XMLDataSource;

public class REXtoXMLDataSource extends XMLDataSource {
	List<String[]> data = new ArrayList<String[]>();
	String[] header;
	String sep = "\n";
	//String rowMatcher = "^([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)$";
	String rowMatcher = null;
	String path;
	private String fileText;
	boolean headset = false;
	
	
	public REXtoXMLDataSource(String name, String path) {
		super(name, path);
		this.header = null;
		this.path = path;
	}
	
	public REXtoXMLDataSource(String name, String path, String sep) {
		super(name, path);
		this.header = null;
		this.path = path;
		this.sep = sep;
	}
	
	
	//loads the data source
	public DataSource load(){	
		String resolvedPath = this.cacher.resolvePath(this.path);
			try{
			this.fileText = new String(Files.readAllBytes(Paths.get(resolvedPath)), StandardCharsets.UTF_8);
			String[] rows = rowSplitter();
			Pattern pat = Pattern.compile(rowMatcher);
			for(String temp : rows){
				Matcher m = pat.matcher(temp);
				if(m.find()){
					int N = m.groupCount();
					
					String[] tempArray = new String[N];
					for(int i = 0; i<N; i++) {
						String t = m.group(i+1);
						tempArray[i] = t;
						System.out.println(t);
					}
					data.add(tempArray);
				}
			}	
		}
		catch(IOException e){
			System.err.println("Bad file: " + resolvedPath);
			e.printStackTrace();
		}
		
		if(data.size() > 0){
			//if the header is not set it pulls off the first row by default
			if(this.header == null){
				this.header = data.get(0);
				data.remove(0);
			}
			
			XML xml = buildXML(this.header, this.data);
			this.setXML(xml);
			return super.load(false);
		}
		else{
			System.out.println("ERROR - DATA SIZE = 0");
			return null;
		}
	}
	
	//splits a given string based on some delimiter 
	private String[] rowSplitter(){
		String[] temp = this.fileText.split(sep);
		return temp;
	}
	
	public DataSource setOption(String op, String value) {
		if("sep".equalsIgnoreCase(op)){
			this.sep = value;
			return this;
		}
		else if ("header".equalsIgnoreCase(op)) {
				this.header = value.split(",");
				return this;
		}
		else if("rowMatcher".equalsIgnoreCase(op)){
			this.rowMatcher = value;
			System.out.println("set row matcher to: " + value);
			return this;
		}
		else {
			return super.setOption(op, value);
		}
	}
	
	protected static XML buildXML(String[] header, List<String[]> rows) {
		XML xml = new XML("rows");
		XML record;
		for (String[] row : rows) {
			if(header.length > 1){
				record = xml.addChild("row");
				//System.err.println("Row: " + IOUtil.join(row, ",") + " (" + row.length + ")");
				if (row.length >= header.length) {
					for (int i = 0; i < header.length; i++) {
						String tag = fixXMLtag(header[i]);   // TODO: make this efficient: calculate header tags once outside loop
						XML child = record.addChild(tag);
						child.setContent(row[i].trim());
						}
					}
				}
			else{
				if (row.length >= header.length) {
					String tag = fixXMLtag(header[0]);
					for (int i = 0; i < header.length; i++) {
						record = xml.addChild(tag);
						record.setContent(row[i].trim());
						}
			}
		} 
	}
		
		//System.out.println(xml);

		return xml;
	}
	

	protected static char[][] replaces = { {' ', '_'}, {'/', '-'} };

	protected static String fixXMLtag(String orig) {
		String tag = orig.trim();
		for (char[] r : replaces) {
			tag = tag.replace(r[0], r[1]);
		}
		return tag;
	}
	
	

}
