package test;
import big.data.*;

public class RexFileTest {

	
	public static void main(String[] args){
		DataSource ds = DataSource.connectREX("/Users/Stephen/Desktop/test.txt");
		ds.setOption("regex", "\\|");
		ds.load();
		ds.printUsageString();
		Person[] t = ds.fetchArray("test.Person", "row/Name", "row/Last");
		for(int i = 0; i<t.length; i++){
			System.out.println(t[i].toString());
		}
	}
	
}

class Person{
	String first, last;
	public Person(String first, String last){
		this.first = first;
		this.last = last;
	}
	
	public String toString(){
		return "First name: " + this.first + "\t Last name: " + this.last;
	}
}
