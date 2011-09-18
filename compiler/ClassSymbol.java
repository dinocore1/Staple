import java.util.LinkedHashMap;
import java.util.Map;


public class ClassSymbol extends Symbol implements Scope {

	Map<String, Symbol> symbolTable = new LinkedHashMap<String, Symbol>();
	private Scope enclosingScope;

	public ClassSymbol(String name, Scope enclosingScope) {
		super(name);
		type = new ClassType(this);
		this.enclosingScope = enclosingScope;
	}

	

	public String getScopeName() {
		return name;
	}


	public Scope getEnclosingScope() {
		return enclosingScope;
	}


	public void define(Symbol sym) {
		symbolTable.put(sym.name, sym);
		sym.scope = this;
		
	}

	public Symbol resolve(String name) {
		if ( getEnclosingScope() != null ) {
			return getEnclosingScope().resolve(name);
		}
		return null; // not found
	}




}
