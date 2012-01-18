
public class ClassType implements Type {

	private ClassSymbol mSymbol;

	public ClassType(ClassSymbol classSymbol) {
		mSymbol = classSymbol;
	}

	public String getName() {
		return mSymbol.name;
	}

	@Override
	public boolean isPointer() {
		return true;
	}

}
