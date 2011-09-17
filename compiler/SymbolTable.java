public class SymbolTable {
    GlobalScope globals = new GlobalScope();

    public SymbolTable() { initTypeSystem(); }
    protected void initTypeSystem() {
        BuiltInTypeSymbol tInt = new BuiltInTypeSymbol("int");
        BuiltInTypeSymbol tVoid = new BuiltInTypeSymbol("void");
        globals.define(tInt);
        globals.define(tVoid); // pseudo-type
        globals.define(new FunctionSymbol("printf", tVoid, globals));
    }

    public String toString() { return globals.toString(); }
}
