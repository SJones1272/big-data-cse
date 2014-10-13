package test;
import big.data.*;

public class RexWebDataTest {

 public static void main(String[] args) {
  String path = "http://www.msha.gov/OpenGovernmentData/DataSets/Violations_Definition_File.txt";
  DataSource ds = DataSource.connectREX(path);
  ds.setOption("regex", "\\|"); 
  ds.load();
  ds.printUsageString();
  String[] temp = ds.fetchStringArray("row/FIELD_DESCRIPTION");
  for(int i = 0; i<temp.length; i++){
   System.out.println(temp[i]);
   
  }

 }

}