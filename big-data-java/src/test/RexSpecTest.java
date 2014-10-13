package test;

import big.data.DataSource;

public class RexSpecTest {

	public static void main(String[] args) {
		DataSource ds = DataSource.connectUsing("RexTest.xml");
		ds.load();
		ds.printUsageString();
	}
	
	/*
    IDataField fs = ds.getFieldSpec();
    XML fsxml = fs.apply(new FieldToXMLSpec());
    System.out.println(fsxml.format(2));
    */

}
