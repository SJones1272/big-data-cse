package big.data.xml;

import java.util.ArrayList;
import java.util.HashMap;

import big.data.*;
import big.data.field.*;
import big.data.sig.*;
import big.data.util.*;

@SuppressWarnings("unchecked")
public class XMLDataSource extends DataSource {
	protected XML xml;
	protected IPostProcessor proc;
	
	public XMLDataSource(String name, String path) {
		super(name, path);
	}

	public XMLDataSource(String name, String path, IDataField spec) {
		super(name, path, spec);
	}

	public XMLDataSource setXML(XML xml) {
		this.xml = xml;
		doPostProcess();
		return this;
	}
	
	public XMLDataSource setPostProcessor(IPostProcessor proc) {
		this.proc = proc;
		return this;
	}
	
	public XMLDataSource setPostProcessor(String clsName) {
		try {
			setPostProcessor((IPostProcessor) SigBuilder.classFor(clsName).newInstance());
		} catch (InstantiationException e) {
			System.err.println("Could not load post-processor: " + clsName);
		} catch (IllegalAccessException e) {
			System.err.println("Could not load post-processor: " + clsName);
		}
		return this;
	}
	
	protected void doPostProcess() {
		if (proc != null) {
			xml = proc.process(xml);
			if (xml == null) {
				System.err.println(((getName()==null)?"":getName()+": ") + "XML post-process failed");
			}
		}
	}
	
	public DataSource load() {
		return this.load(true);
	}
	
	public DataSource load(boolean forceReload) {
		if (!readyToLoad())
			throw new DataSourceException("Not ready to load; missing parameters: " + IOUtil.join(missingParams().toArray(new String[]{}), ','));

		boolean newlyLoaded = false;
		String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
		if (resolvedPath != null && (xml == null || forceReload)) { 
			xml = IOUtil.loadXML(resolvedPath);
			newlyLoaded = true;
		}

		if (xml == null) {
			System.err.println("Failed to load: " + this.getFullPathURL() + "\nCHECK NETWORK CONNECTION, if applicable");
			return null;
		} else {
			if (newlyLoaded) doPostProcess();
		}
		
		if (spec == null)
			spec = XMLDataFieldInferrer.inferDataField(xml);
		if (spec == null)
			System.err.println("Failed to load: missing data field specification");
		
		if (xml != null && spec != null) {
			this.loaded = true;
		}
		return this;
	}

	public int size() {
		if (!loaded) return 0;

		return
		spec.apply(new IDFVisitor<Integer>() {
			public Integer defaultVisit(IDataField df) {
				XML node = ((ADataField)df).findMyNode(xml);
				return (node == null) ? 0 : 1;
			}

			public Integer visitPrimField(PrimField f, String basePath, String description) {
				return defaultVisit(f);
			}

			public Integer visitCompField(CompField f, String basePath,
					String description, HashMap<String, IDataField> fieldMap) {
				return defaultVisit(f);
			}

			public Integer visitListField(ListField f, String basePath,
					String description, String elemPath, IDataField elemField) {
				XML node = f.findMyNode(xml);
				if (node == null) return 0;
				XML[] children = node.getChildren(elemPath);
				return children.length;
			}
		});
	}
	
	public DataSourceIterator iterator() {
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		return new XMLDataSourceIterator(this, this.spec, this.xml);
	}

	public <T> T fetch(Class<T> cls, String... keys) {
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		ISig sig = SigBuilder.buildCompSig(cls, keys);
		return spec.apply(new XMLInstantiator<T>(xml, sig));
	}

	public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
		if (!this.hasData())
			throw new DataSourceException("No data available: " + this.getName() + " --- make sure you called .load()");
		ISig sig = new ListSig(SigBuilder.buildCompSig(cls, keys));
		return spec.apply(new XMLInstantiator<ArrayList<T>>(xml, sig));
	}

	public DataSource setOption(String op, String value) {
		if ("postprocess".equals(op) && value != null)
			return setPostProcessor(value);
		else
			return super.setOption(op, value);
	}
}
