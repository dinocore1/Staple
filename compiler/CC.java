import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CC {
    /** An adaptor that tells ANTLR to build CTree nodes */
    public static TreeAdaptor cTreeAdaptor = new CommonTreeAdaptor() {
        public Object create(Token token) {
            return new CTree(token);
        }
        public Object dupNode(Object t) {
            if ( t==null ) {
                return null;
            }
            return create(((CTree)t).token);
        }
        public Object errorNode(TokenStream input, Token start, Token stop,
                                RecognitionException e)
        {
            CErrorNode t = new CErrorNode(input, start, stop, e);
            //System.out.println("returning error node '"+t+"' @index="+input.index());
            return t;
        }
    };

    public static void main(String[] args) throws Exception {
        String templatesFilename = "llvm.stg";
        String filename=null;
        int i = 0;
        while ( i<args.length ) {
            if ( args[i].equals("-templates") ) {
                templatesFilename = args[i+1]; i+=2;
            }
            else { filename = args[i]; i++; }
        }

        InputStream input = null;
        if ( filename!=null ) input = new FileInputStream(filename);
        else input = System.in;

        // CREATE LEXER/PARSER THAT CREATES AST FROM INPUT
        CLexer lexer = new CLexer(new ANTLRInputStream(input));
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        CParser parser = new CParser(tokens);
        parser.setTreeAdaptor(cTreeAdaptor);
        CParser.translation_unit_return ret = parser.translation_unit();
        CommonTree t = (CommonTree)ret.getTree();
        System.out.println("; "+t.toStringTree());
        DOTTreeGenerator dot = new DOTTreeGenerator();
        System.out.println(dot.toDOT(t));

        // MAKE SYM TAB
        SymbolTable symtab = new SymbolTable();

        // LOAD TEMPLATES (via classpath)
        ClassLoader cl = CC.class.getClassLoader();
        InputStream in = cl.getResourceAsStream(templatesFilename);
        Reader rd = new InputStreamReader(in);
        StringTemplateGroup templates = new StringTemplateGroup(rd);
        rd.close();

        CommonTreeNodeStream nodes = new CommonTreeNodeStream(cTreeAdaptor, t);
        nodes.setTokenStream(tokens);

        // DEFINE/RESOLVE SYMBOLS
        DefRef def = new DefRef(nodes, symtab); // use custom constructor
        def.downup(t); // trigger symtab actions upon certain subtrees
        //System.out.println("globals: "+symtab.globals);

        // GENERATE CODE
        nodes.reset();
        Gen walker = new Gen(nodes, symtab);
        walker.setTemplateLib(templates);
        Gen.translation_unit_return ret2 = walker.translation_unit();

        // EMIT IR
        // uncomment next line to learn which template emits what output
        //templates.emitDebugStartStopStrings(true);
        String output = ret2.getTemplate().toString();
        System.out.println(output);
    }
}
