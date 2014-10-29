package test;
import big.data.*;
import big.data.field.FieldToXMLSpec;
import big.data.field.IDataField;
import big.data.util.XML;

public class RexWebDataTest {

 public static void main(String[] args) {
  String path = "http://www.msha.gov/OpenGovernmentData/DataSets/Violations_Definition_File.txt";
  DataSource ds = DataSource.connectREX(path);
  ds.setOption("rowMatcher", "^([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)$");
  ds.load();
  ds.printUsageString();
  IDataField fs = ds.getFieldSpec();
  XML fsxml = fs.apply(new FieldToXMLSpec());
  System.out.println(fsxml.format(2));
  double[] temp = ds.fetchDoubleArray("row/DATA_LENGTH");
  double sum = 0;
  for(int i = 0; i<temp.length; i++){
   sum+=temp[i];
  }
  System.out.println("Average length of data is: " + sum/2);
 
 }


}