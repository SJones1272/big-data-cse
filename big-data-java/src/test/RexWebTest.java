package test;

import big.data.DataSource;

public class RexWebTest {

	public static void main(String[] args) {
		DataSource ds = DataSource.connectREX("http://www.unitedstateszipcodes.org/ga/");
		ds.setOption("header", "Name|Zip");
		//come up with a regular expression
		ds.setOption("regex", "");
		ds.load();
		ds.printUsageString();
		String[] t = ds.fetchStringArray("row/Zip");
		for(int i = 0; i<t.length; i++){
			System.out.println(t[i]);
		}
		System.out.println("done");
	}

}
