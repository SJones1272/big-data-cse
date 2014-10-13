package big.data.rex;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import big.data.DataSource;
import big.data.util.XML;
import big.data.xml.XMLDataSource;

public class REXtoXMLDataSource extends XMLDataSource {
	List<String[]> rows = new ArrayList<String[]>();
	String[] header;
	//default it is just set to all whitespace
	String sep = "\\W+";
	String path;
	
	public REXtoXMLDataSource(String name, String path) {
		super(name, path);
		this.header = null;
		this.path = path;
		this.sep = "\\W+";
		
	}
	
	public REXtoXMLDataSource(String name, String path, String sep){
		super(name, path);
		this.sep = sep;
		this.header = null;
	}
	
	//load the data source
	@SuppressWarnings("resource")
	public DataSource load(){	
		BufferedReader reader = null;
		String resolvedPath = this.cacher.resolvePath(this.path);
		try{
			reader = new BufferedReader(new FileReader(new File(resolvedPath)));
			String curline;
				while((curline = reader.readLine()) != null){
					String[] temp = curline.split(this.sep);
					rows.add(temp);
				}
		}
		catch(IOException e){
			System.err.println("Bad file: " + resolvedPath);
			e.printStackTrace();
		}
		
		
		if(rows.size() != 0){
			//if the header is not set it pulls off the first row by default
			if(this.header == null){
				this.header = rows.get(0);
				rows.remove(0);
			}
			XML xml = buildXML(this.header, rows);
			this.setXML(xml);
			return super.load(false);
		}
		else
			return null;
	
	}
	
	
	public DataSource setOption(String op, String value) {
		if("regex".equalsIgnoreCase(op)){
			this.sep = value;
			return this;
			
		}
		if ("header".equalsIgnoreCase(op)) {
			//what would be the best thing to split header on?
			Matcher m = Pattern.compile("([a-zA-z]+(\\_)?)").matcher(value);
			header = new String[m.groupCount()];
			for(int i = 0; i<m.groupCount(); i++){
				this.header[i] = m.group();
			}
			return this;
		}
		else {
			return super.setOption(op, value);
		}
	}
	
	
	
	protected static XML buildXML(String[] header, List<String[]> rows) {
		XML xml = new XML("rows");

		for (String[] row : rows) {
			XML record = xml.addChild("row");
			//System.err.println("Row: " + IOUtil.join(row, ",") + " (" + row.length + ")");

			if (row.length >= header.length) {
				for (int i = 0; i < header.length; i++) {
					String tag = fixXMLtag(header[i]);   // TODO: make this efficient: calculate header tags once outside loop
					XML child = record.addChild(tag);
					child.setContent(row[i].trim());
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
