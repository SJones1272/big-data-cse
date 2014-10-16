package big.data.rex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
	//by default it separates file into rows based on
	//new line character
	String sep = "\n";
	//By default it seperates on \w
	String rowMatcher = "(.*\\w)";
	String path;
	Boolean scrape = false;
	ByteArrayOutputStream baos;
	Byte[] temp;
	
	public REXtoXMLDataSource(String name, String path) {
		super(name, path);
		this.header = null;
		this.path = path;
	}
	
	public REXtoXMLDataSource(String name, String path, String rowMatcher){
		super(name, path);
		this.rowMatcher = rowMatcher;
		this.header = null;
	}
	
	
	//load the data source
	public DataSource load(){	
		//BufferedReader reader = null;
		String resolvedPath = this.cacher.resolvePath(this.path);
			try{
			//reader = new BufferedReader(new FileReader(new File(resolvedPath)));
			
			//reads in all lines in a file
			String lines = new String(Files.readAllBytes(Paths.get(this.path)));
			System.out.println(lines);
			//splits the file into rows
			String[] rows = rowSplitter(lines);
			for(int i = 0; i<rows.length; i++){
				Matcher m = Pattern.compile(this.rowMatcher).matcher(rows[i]);
				String[] temp = new String[m.groupCount()];
				System.out.println(m.groupCount());
				for(int r = 0; r<temp.length; r++){
					temp[r] = m.group(r);
					System.out.println(m.group(r));
				}
				data.add(temp);
			}
			}
			/*
			String curline;
				while((curline = reader.readLine()) != null){
					String[] temp = curline.split(this.sep);
					rows.add(temp);
					System.out.println(curline);
				}
			}
			*/
		catch(IOException e){
			System.err.println("Bad file: " + resolvedPath);
			e.printStackTrace();
		}
		
		
		if(data.size() != 0){
			//if the header is not set it pulls off the first row by default
			if(this.header == null){
				this.header = data.get(0);
				data.remove(0);
			}
			XML xml = buildXML(this.header, this.data);
			this.setXML(xml);
			return super.load(false);
		}
		else
			return null;
	
	}
	
	//splits a given string based on some delimiter.
	private String[] rowSplitter(String t){
		String[] temp = t.split(this.sep);
		return temp;
	}
	
	public DataSource setOption(String op, String value) {
		if("regex".equalsIgnoreCase(op)){
			this.sep = value;
			return this;
			
		}
		if ("header".equalsIgnoreCase(op)) {
			//Splits the header -- assumes that each part contains letters/digits/underscores 
			//and seperated by non word characters
			this.header = value.split("\\W");
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
