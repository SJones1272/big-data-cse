package test;

import big.data.DataSource;

public class RexWebTest {

	public static void main(String[] args) {
		DataSource ds = DataSource.connectREX("http://www.unitedstateszipcodes.org/ga/");
		ds.setOption("header", "Name|Title");
		//regex is wrong - pulls html code instead of just numbers.
		ds.setOption("regex", "\\d+");
		ds.load();
		ds.printUsageString();
		String[] t = ds.fetchStringArray("row/");
		for(int i = 0; i<t.length; i++){
			System.out.println(t[i]);
		}
		System.out.println("done");
	}

}
