package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import big.data.DataSource;
import big.data.field.FieldToXMLSpec;
import big.data.field.IDataField;
import big.data.util.XML;

public class RexWebTest {

	public static void main(String[] args) throws FileNotFoundException {
		DataSource ds = DataSource.connectREX("http://alerts.weather.gov/cap/ga.php?x=3");
		ds.setOption("header", "City");
		ds.setOption("rowMatcher", "(?!<td width='70%' valign='top'>)([\\w]+)(?=</td>)");
		ds.load();
		ds.printUsageString();
		PrintWriter writer = new PrintWriter(new File("/Users/Stephen/Desktop/Cities.txt"));
		
		String[] temp = ds.fetchStringArray("City");
		for(String c : temp){
			System.out.println(c);
			writer.write(c + "\n");
		}
		writer.close();
	     IDataField fs = ds.getFieldSpec();
	     XML fsxml = fs.apply(new FieldToXMLSpec());
	     System.out.println(fsxml.format(2));
	     
	     //fsxml.write(writer);
	}

} 
