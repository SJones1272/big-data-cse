package test;

import java.io.FileNotFoundException;

import big.data.DataSource;


public class RexSpecTest {
	
	public static void main(String[] args) throws FileNotFoundException {
		DataSource ds = DataSource.connectUsing("/Users/Stephen/Desktop/RexSpecFile.XML");
		ds.load();
		ds.printUsageString();
		double[] temp = ds.fetchDoubleArray("row/DATA_LENGTH");
		double sum = 0;
		for(int i = 0; i<temp.length; i++){
		   sum+=temp[i];
		  }
		 System.out.println("Average length of data is: " + sum/2);
	}

}
