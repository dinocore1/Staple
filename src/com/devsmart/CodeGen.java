// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g 2012-09-03 19:05:44

package com.devsmart;

import com.devsmart.symbol.*;
import com.devsmart.type.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
@SuppressWarnings({"all", "warnings", "unchecked"})
public class CodeGen extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARGS", "ASSIGN", "BLOCK", "CALL", "CLASS", "COMMENT", "CREATEOBJ", "EscapeSequence", "FIELDACCESS", "FIELDS", "FORMALARGS", "FUNCTION", "ID", "IMPORT", "INT", "LETTER", "LINE_COMMENT", "METHODS", "NOT", "PACKAGE", "StringLiteral", "THIS", "UNIT", "VARDEF", "WS", "'!'", "'!='", "'('", "')'", "'*'", "'+'", "','", "'-'", "'.'", "'/'", "';'", "'<'", "'<='", "'='", "'=='", "'>'", "'>='", "'bool'", "'class'", "'extends'", "'import'", "'int'", "'new'", "'package'", "'this'", "'void'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int ARGS=4;
    public static final int ASSIGN=5;
    public static final int BLOCK=6;
    public static final int CALL=7;
    public static final int CLASS=8;
    public static final int COMMENT=9;
    public static final int CREATEOBJ=10;
    public static final int EscapeSequence=11;
    public static final int FIELDACCESS=12;
    public static final int FIELDS=13;
    public static final int FORMALARGS=14;
    public static final int FUNCTION=15;
    public static final int ID=16;
    public static final int IMPORT=17;
    public static final int INT=18;
    public static final int LETTER=19;
    public static final int LINE_COMMENT=20;
    public static final int METHODS=21;
    public static final int NOT=22;
    public static final int PACKAGE=23;
    public static final int StringLiteral=24;
    public static final int THIS=25;
    public static final int UNIT=26;
    public static final int VARDEF=27;
    public static final int WS=28;

    // delegates
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators


    public CodeGen(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public CodeGen(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected StringTemplateGroup templateLib =
  new StringTemplateGroup("CodeGenTemplates", AngleBracketTemplateLexer.class);

public void setTemplateLib(StringTemplateGroup templateLib) {
  this.templateLib = templateLib;
}
public StringTemplateGroup getTemplateLib() {
  return templateLib;
}
/** allows convenient multi-value initialization:
 *  "new STAttrMap().put(...).put(...)"
 */
public static class STAttrMap extends HashMap {
  public STAttrMap put(String attrName, Object value) {
    super.put(attrName, value);
    return this;
  }
  public STAttrMap put(String attrName, int value) {
    super.put(attrName, new Integer(value));
    return this;
  }
}
    public String[] getTokenNames() { return CodeGen.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/CodeGen.g"; }



    	List mClassPackageName;
    	String mClassName;


    public static class code_unit_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "code_unit"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:21:1: code_unit : ^( UNIT ^( PACKAGE (packagename+= . )* ) class_def ) -> code_unit(packagename=$packagenamecode=$class_def.st);
    public final CodeGen.code_unit_return code_unit() throws RecognitionException {
        CodeGen.code_unit_return retval = new CodeGen.code_unit_return();
        retval.start = input.LT(1);


        StapleTree packagename=null;
        List list_packagename=null;
        CodeGen.class_def_return class_def1 =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:22:2: ( ^( UNIT ^( PACKAGE (packagename+= . )* ) class_def ) -> code_unit(packagename=$packagenamecode=$class_def.st))
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:22:4: ^( UNIT ^( PACKAGE (packagename+= . )* ) class_def )
            {
            match(input,UNIT,FOLLOW_UNIT_in_code_unit54); 

            match(input, Token.DOWN, null); 
            match(input,PACKAGE,FOLLOW_PACKAGE_in_code_unit62); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:24:22: (packagename+= . )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0 >= ARGS && LA1_0 <= 56)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:24:22: packagename+= .
                	    {
                	    packagename=(StapleTree)input.LT(1);

                	    matchAny(input); 
                	    if (list_packagename==null) list_packagename=new ArrayList();
                	    list_packagename.add(packagename);


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);



                			mClassPackageName = list_packagename;
                		

                match(input, Token.UP, null); 
            }


            pushFollow(FOLLOW_class_def_in_code_unit79);
            class_def1=class_def();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 29:13: -> code_unit(packagename=$packagenamecode=$class_def.st)
            {
                retval.st = templateLib.getInstanceOf("code_unit",new STAttrMap().put("packagename", list_packagename).put("code", (class_def1!=null?class_def1.st:null)));
            }



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "code_unit"


    public static class class_def_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "class_def"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:32:1: class_def : ^( CLASS name= . superclass= . ^( FIELDS (fieldDefs= fieldDefinition )* ) ^( METHODS (methodDefs= methodDefinition )* ) ) -> class_def(name=classNamesuperclass=$superclassfields=$fieldDefs.stcode=$methodDefs.st);
    public final CodeGen.class_def_return class_def() throws RecognitionException {
        CodeGen.class_def_return retval = new CodeGen.class_def_return();
        retval.start = input.LT(1);


        StapleTree name=null;
        StapleTree superclass=null;
        CodeGen.fieldDefinition_return fieldDefs =null;

        CodeGen.methodDefinition_return methodDefs =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:33:2: ( ^( CLASS name= . superclass= . ^( FIELDS (fieldDefs= fieldDefinition )* ) ^( METHODS (methodDefs= methodDefinition )* ) ) -> class_def(name=classNamesuperclass=$superclassfields=$fieldDefs.stcode=$methodDefs.st))
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:33:4: ^( CLASS name= . superclass= . ^( FIELDS (fieldDefs= fieldDefinition )* ) ^( METHODS (methodDefs= methodDefinition )* ) )
            {
            match(input,CLASS,FOLLOW_CLASS_in_class_def107); 

            match(input, Token.DOWN, null); 
            name=(StapleTree)input.LT(1);

            matchAny(input); 


            		mClassName = name.getText();
            		ArrayList className = new ArrayList(mClassPackageName);
            		className.add(name);
            	

            superclass=(StapleTree)input.LT(1);

            matchAny(input); 

            match(input,FIELDS,FOLLOW_FIELDS_in_class_def123); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:39:33: (fieldDefs= fieldDefinition )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==ID||LA2_0==46||LA2_0==50||LA2_0==54) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:39:33: fieldDefs= fieldDefinition
                	    {
                	    pushFollow(FOLLOW_fieldDefinition_in_class_def127);
                	    fieldDefs=fieldDefinition();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }


            match(input,METHODS,FOLLOW_METHODS_in_class_def132); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:39:72: (methodDefs= methodDefinition )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==FUNCTION) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:39:72: methodDefs= methodDefinition
                	    {
                	    pushFollow(FOLLOW_methodDefinition_in_class_def136);
                	    methodDefs=methodDefinition();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop3;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 40:3: -> class_def(name=classNamesuperclass=$superclassfields=$fieldDefs.stcode=$methodDefs.st)
            {
                retval.st = templateLib.getInstanceOf("class_def",new STAttrMap().put("name", className).put("superclass", superclass).put("fields", (fieldDefs!=null?fieldDefs.st:null)).put("code", (methodDefs!=null?methodDefs.st:null)));
            }



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "class_def"


    public static class fieldDefinition_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "fieldDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:43:1: fieldDefinition : ^(t= typeDefinition name= ID ) ;
    public final CodeGen.fieldDefinition_return fieldDefinition() throws RecognitionException {
        CodeGen.fieldDefinition_return retval = new CodeGen.fieldDefinition_return();
        retval.start = input.LT(1);


        StapleTree name=null;
        CodeGen.typeDefinition_return t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:44:2: ( ^(t= typeDefinition name= ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:44:4: ^(t= typeDefinition name= ID )
            {
            pushFollow(FOLLOW_typeDefinition_in_fieldDefinition180);
            t=typeDefinition();

            state._fsp--;


            match(input, Token.DOWN, null); 
            name=(StapleTree)match(input,ID,FOLLOW_ID_in_fieldDefinition184); 

            match(input, Token.UP, null); 



            		VarableSymbol vs = (VarableSymbol)name.symbol;
            		switch(TypeFactory.getType(vs.type)){
            			case TypeFactory.TYPE_BOOL:
            				retval.st = templateLib.getInstanceOf("bool_field_def",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_INT:
            				retval.st = templateLib.getInstanceOf("int_field_def",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_CLASS:
            				retval.st = templateLib.getInstanceOf("obj_field_def",new STAttrMap().put("name", vs.getName()));
            			break;
            		}
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fieldDefinition"


    public static class methodDefinition_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "methodDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:63:1: methodDefinition : ^( FUNCTION name= ID returnType= methodReturnDefinition ^( FORMALARGS (formals= formalArg )* ) code= block ) -> method(name=methodNamereturn=$returnType.stformals=$formals.stcode=$code.st);
    public final CodeGen.methodDefinition_return methodDefinition() throws RecognitionException {
        CodeGen.methodDefinition_return retval = new CodeGen.methodDefinition_return();
        retval.start = input.LT(1);


        StapleTree name=null;
        CodeGen.methodReturnDefinition_return returnType =null;

        CodeGen.formalArg_return formals =null;

        CodeGen.block_return code =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:64:2: ( ^( FUNCTION name= ID returnType= methodReturnDefinition ^( FORMALARGS (formals= formalArg )* ) code= block ) -> method(name=methodNamereturn=$returnType.stformals=$formals.stcode=$code.st))
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:64:4: ^( FUNCTION name= ID returnType= methodReturnDefinition ^( FORMALARGS (formals= formalArg )* ) code= block )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_methodDefinition201); 

            match(input, Token.DOWN, null); 
            name=(StapleTree)match(input,ID,FOLLOW_ID_in_methodDefinition205); 

            pushFollow(FOLLOW_methodReturnDefinition_in_methodDefinition209);
            returnType=methodReturnDefinition();

            state._fsp--;


            match(input,FORMALARGS,FOLLOW_FORMALARGS_in_methodDefinition212); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:64:77: (formals= formalArg )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ID||LA4_0==46||LA4_0==50||LA4_0==54) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:64:77: formals= formalArg
                	    {
                	    pushFollow(FOLLOW_formalArg_in_methodDefinition216);
                	    formals=formalArg();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop4;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }


            pushFollow(FOLLOW_block_in_methodDefinition222);
            code=block();

            state._fsp--;


            match(input, Token.UP, null); 



            			ArrayList methodName = new ArrayList(mClassPackageName);
            			methodName.add(mClassName);
            			methodName.add(name);
            		

            // TEMPLATE REWRITE
            // 70:3: -> method(name=methodNamereturn=$returnType.stformals=$formals.stcode=$code.st)
            {
                retval.st = templateLib.getInstanceOf("method",new STAttrMap().put("name", methodName).put("return", (returnType!=null?returnType.st:null)).put("formals", (formals!=null?formals.st:null)).put("code", (code!=null?code.st:null)));
            }



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "methodDefinition"


    public static class methodReturnDefinition_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "methodReturnDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:73:1: methodReturnDefinition : ( 'void' -> void_return_def(| 'int' -> int_return_def(| 'bool' -> bool_return_def(| ID -> obj_return_def();
    public final CodeGen.methodReturnDefinition_return methodReturnDefinition() throws RecognitionException {
        CodeGen.methodReturnDefinition_return retval = new CodeGen.methodReturnDefinition_return();
        retval.start = input.LT(1);


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:74:2: ( 'void' -> void_return_def(| 'int' -> int_return_def(| 'bool' -> bool_return_def(| ID -> obj_return_def()
            int alt5=4;
            switch ( input.LA(1) ) {
            case 54:
                {
                alt5=1;
                }
                break;
            case 50:
                {
                alt5=2;
                }
                break;
            case 46:
                {
                alt5=3;
                }
                break;
            case ID:
                {
                alt5=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:74:4: 'void'
                    {
                    match(input,54,FOLLOW_54_in_methodReturnDefinition265); 

                    // TEMPLATE REWRITE
                    // 74:11: -> void_return_def(
                    {
                        retval.st = templateLib.getInstanceOf("void_return_def");
                    }



                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:75:4: 'int'
                    {
                    match(input,50,FOLLOW_50_in_methodReturnDefinition276); 

                    // TEMPLATE REWRITE
                    // 75:10: -> int_return_def(
                    {
                        retval.st = templateLib.getInstanceOf("int_return_def");
                    }



                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:76:4: 'bool'
                    {
                    match(input,46,FOLLOW_46_in_methodReturnDefinition287); 

                    // TEMPLATE REWRITE
                    // 76:11: -> bool_return_def(
                    {
                        retval.st = templateLib.getInstanceOf("bool_return_def");
                    }



                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:77:4: ID
                    {
                    match(input,ID,FOLLOW_ID_in_methodReturnDefinition298); 

                    // TEMPLATE REWRITE
                    // 77:7: -> obj_return_def(
                    {
                        retval.st = templateLib.getInstanceOf("obj_return_def");
                    }



                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "methodReturnDefinition"


    public static class formalArg_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "formalArg"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:80:1: formalArg : ^(t= typeDefinition name= ID ) ;
    public final CodeGen.formalArg_return formalArg() throws RecognitionException {
        CodeGen.formalArg_return retval = new CodeGen.formalArg_return();
        retval.start = input.LT(1);


        StapleTree name=null;
        CodeGen.typeDefinition_return t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:81:2: ( ^(t= typeDefinition name= ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:81:4: ^(t= typeDefinition name= ID )
            {
            pushFollow(FOLLOW_typeDefinition_in_formalArg319);
            t=typeDefinition();

            state._fsp--;


            match(input, Token.DOWN, null); 
            name=(StapleTree)match(input,ID,FOLLOW_ID_in_formalArg323); 

            match(input, Token.UP, null); 



            		VarableSymbol vs = (VarableSymbol)name.symbol;
            		switch(TypeFactory.getType(vs.type)){
            			case TypeFactory.TYPE_BOOL:
            				retval.st = templateLib.getInstanceOf("bool_arg",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_INT:
            				retval.st = templateLib.getInstanceOf("int_arg",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_CLASS:
            				retval.st = templateLib.getInstanceOf("obj_arg",new STAttrMap().put("name", vs.getName()));
            			break;
            		}
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "formalArg"


    public static class block_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "block"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:100:1: block : ^( BLOCK (code= statement )* ) -> basic_block(code=$code.st);
    public final CodeGen.block_return block() throws RecognitionException {
        CodeGen.block_return retval = new CodeGen.block_return();
        retval.start = input.LT(1);


        CodeGen.statement_return code =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:101:2: ( ^( BLOCK (code= statement )* ) -> basic_block(code=$code.st))
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:101:4: ^( BLOCK (code= statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block340); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:101:16: (code= statement )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( ((LA6_0 >= ASSIGN && LA6_0 <= BLOCK)||LA6_0==INT||LA6_0==VARDEF||(LA6_0 >= 33 && LA6_0 <= 34)||LA6_0==36||LA6_0==38) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:101:16: code= statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block344);
                	    code=statement();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop6;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }


            // TEMPLATE REWRITE
            // 101:29: -> basic_block(code=$code.st)
            {
                retval.st = templateLib.getInstanceOf("basic_block",new STAttrMap().put("code", (code!=null?code.st:null)));
            }



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class statement_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "statement"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:104:1: statement : ( block | assignment | vardef | integerOp );
    public final CodeGen.statement_return statement() throws RecognitionException {
        CodeGen.statement_return retval = new CodeGen.statement_return();
        retval.start = input.LT(1);


        CodeGen.block_return block2 =null;

        CodeGen.assignment_return assignment3 =null;

        CodeGen.vardef_return vardef4 =null;

        CodeGen.integerOp_return integerOp5 =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:105:2: ( block | assignment | vardef | integerOp )
            int alt7=4;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt7=1;
                }
                break;
            case ASSIGN:
                {
                alt7=2;
                }
                break;
            case VARDEF:
                {
                alt7=3;
                }
                break;
            case INT:
            case 33:
            case 34:
            case 36:
            case 38:
                {
                alt7=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:105:4: block
                    {
                    pushFollow(FOLLOW_block_in_statement367);
                    block2=block();

                    state._fsp--;


                    retval.st = (block2!=null?block2.st:null);

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:106:4: assignment
                    {
                    pushFollow(FOLLOW_assignment_in_statement374);
                    assignment3=assignment();

                    state._fsp--;


                    retval.st = (assignment3!=null?assignment3.st:null);

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:107:4: vardef
                    {
                    pushFollow(FOLLOW_vardef_in_statement381);
                    vardef4=vardef();

                    state._fsp--;


                    retval.st = (vardef4!=null?vardef4.st:null);

                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:108:4: integerOp
                    {
                    pushFollow(FOLLOW_integerOp_in_statement388);
                    integerOp5=integerOp();

                    state._fsp--;


                    retval.st = (integerOp5!=null?integerOp5.st:null);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "statement"


    public static class assignment_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "assignment"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:111:1: assignment : ^( ASSIGN lside= statement rside= statement ) -> assignment(lside=$lside.strside=$rside.st);
    public final CodeGen.assignment_return assignment() throws RecognitionException {
        CodeGen.assignment_return retval = new CodeGen.assignment_return();
        retval.start = input.LT(1);


        CodeGen.statement_return lside =null;

        CodeGen.statement_return rside =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:112:2: ( ^( ASSIGN lside= statement rside= statement ) -> assignment(lside=$lside.strside=$rside.st))
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:112:4: ^( ASSIGN lside= statement rside= statement )
            {
            match(input,ASSIGN,FOLLOW_ASSIGN_in_assignment403); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_statement_in_assignment407);
            lside=statement();

            state._fsp--;


            pushFollow(FOLLOW_statement_in_assignment411);
            rside=statement();

            state._fsp--;


            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 112:46: -> assignment(lside=$lside.strside=$rside.st)
            {
                retval.st = templateLib.getInstanceOf("assignment",new STAttrMap().put("lside", (lside!=null?lside.st:null)).put("rside", (rside!=null?rside.st:null)));
            }



            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "assignment"


    public static class vardef_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "vardef"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:115:1: vardef : ^( VARDEF t= typeDefinition name= ID ) ;
    public final CodeGen.vardef_return vardef() throws RecognitionException {
        CodeGen.vardef_return retval = new CodeGen.vardef_return();
        retval.start = input.LT(1);


        StapleTree name=null;
        CodeGen.typeDefinition_return t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:116:2: ( ^( VARDEF t= typeDefinition name= ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:116:4: ^( VARDEF t= typeDefinition name= ID )
            {
            match(input,VARDEF,FOLLOW_VARDEF_in_vardef439); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_typeDefinition_in_vardef443);
            t=typeDefinition();

            state._fsp--;


            name=(StapleTree)match(input,ID,FOLLOW_ID_in_vardef447); 

            match(input, Token.UP, null); 



            		VarableSymbol vs = (VarableSymbol)name.symbol;
            		switch(TypeFactory.getType(vs.type)){
            			case TypeFactory.TYPE_BOOL:
            				retval.st = templateLib.getInstanceOf("bool_var",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_INT:
            				retval.st = templateLib.getInstanceOf("int_var",new STAttrMap().put("name", vs.getName()));
            			break;
            			
            			case TypeFactory.TYPE_CLASS:
            				retval.st = templateLib.getInstanceOf("obj_var",new STAttrMap().put("name", vs.getName()));
            			break;
            		}
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "vardef"


    public static class integerOp_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "integerOp"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:135:1: integerOp : ( ^( '+' lside= statement rside= statement ) -> add(lside=$lside.strside=$rside.st)| ^( '-' lside= statement rside= statement ) -> subtract(lside=$lside.strside=$rside.st)| ^( '*' lside= statement rside= statement ) -> multiply(lside=$lside.strside=$rside.st)| ^( '/' lside= statement rside= statement ) -> divide(lside=$lside.strside=$rside.st)|value= INT -> int_literal(value=$value.text));
    public final CodeGen.integerOp_return integerOp() throws RecognitionException {
        CodeGen.integerOp_return retval = new CodeGen.integerOp_return();
        retval.start = input.LT(1);


        StapleTree value=null;
        CodeGen.statement_return lside =null;

        CodeGen.statement_return rside =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:136:2: ( ^( '+' lside= statement rside= statement ) -> add(lside=$lside.strside=$rside.st)| ^( '-' lside= statement rside= statement ) -> subtract(lside=$lside.strside=$rside.st)| ^( '*' lside= statement rside= statement ) -> multiply(lside=$lside.strside=$rside.st)| ^( '/' lside= statement rside= statement ) -> divide(lside=$lside.strside=$rside.st)|value= INT -> int_literal(value=$value.text))
            int alt8=5;
            switch ( input.LA(1) ) {
            case 34:
                {
                alt8=1;
                }
                break;
            case 36:
                {
                alt8=2;
                }
                break;
            case 33:
                {
                alt8=3;
                }
                break;
            case 38:
                {
                alt8=4;
                }
                break;
            case INT:
                {
                alt8=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:136:4: ^( '+' lside= statement rside= statement )
                    {
                    match(input,34,FOLLOW_34_in_integerOp464); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_statement_in_integerOp468);
                    lside=statement();

                    state._fsp--;


                    pushFollow(FOLLOW_statement_in_integerOp472);
                    rside=statement();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    // TEMPLATE REWRITE
                    // 136:43: -> add(lside=$lside.strside=$rside.st)
                    {
                        retval.st = templateLib.getInstanceOf("add",new STAttrMap().put("lside", (lside!=null?lside.st:null)).put("rside", (rside!=null?rside.st:null)));
                    }



                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:137:4: ^( '-' lside= statement rside= statement )
                    {
                    match(input,36,FOLLOW_36_in_integerOp493); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_statement_in_integerOp497);
                    lside=statement();

                    state._fsp--;


                    pushFollow(FOLLOW_statement_in_integerOp501);
                    rside=statement();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    // TEMPLATE REWRITE
                    // 137:43: -> subtract(lside=$lside.strside=$rside.st)
                    {
                        retval.st = templateLib.getInstanceOf("subtract",new STAttrMap().put("lside", (lside!=null?lside.st:null)).put("rside", (rside!=null?rside.st:null)));
                    }



                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:138:4: ^( '*' lside= statement rside= statement )
                    {
                    match(input,33,FOLLOW_33_in_integerOp522); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_statement_in_integerOp526);
                    lside=statement();

                    state._fsp--;


                    pushFollow(FOLLOW_statement_in_integerOp530);
                    rside=statement();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    // TEMPLATE REWRITE
                    // 138:43: -> multiply(lside=$lside.strside=$rside.st)
                    {
                        retval.st = templateLib.getInstanceOf("multiply",new STAttrMap().put("lside", (lside!=null?lside.st:null)).put("rside", (rside!=null?rside.st:null)));
                    }



                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:139:4: ^( '/' lside= statement rside= statement )
                    {
                    match(input,38,FOLLOW_38_in_integerOp551); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_statement_in_integerOp555);
                    lside=statement();

                    state._fsp--;


                    pushFollow(FOLLOW_statement_in_integerOp559);
                    rside=statement();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    // TEMPLATE REWRITE
                    // 139:43: -> divide(lside=$lside.strside=$rside.st)
                    {
                        retval.st = templateLib.getInstanceOf("divide",new STAttrMap().put("lside", (lside!=null?lside.st:null)).put("rside", (rside!=null?rside.st:null)));
                    }



                    }
                    break;
                case 5 :
                    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:140:4: value= INT
                    {
                    value=(StapleTree)match(input,INT,FOLLOW_INT_in_integerOp581); 

                    // TEMPLATE REWRITE
                    // 140:14: -> int_literal(value=$value.text)
                    {
                        retval.st = templateLib.getInstanceOf("int_literal",new STAttrMap().put("value", (value!=null?value.getText():null)));
                    }



                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "integerOp"


    public static class typeDefinition_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "typeDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:143:1: typeDefinition : ( 'void' | 'int' | 'bool' | ID );
    public final CodeGen.typeDefinition_return typeDefinition() throws RecognitionException {
        CodeGen.typeDefinition_return retval = new CodeGen.typeDefinition_return();
        retval.start = input.LT(1);


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:144:2: ( 'void' | 'int' | 'bool' | ID )
            // /home/paul/workspace/Staple/src/com/devsmart/CodeGen.g:
            {
            if ( input.LA(1)==ID||input.LA(1)==46||input.LA(1)==50||input.LA(1)==54 ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "typeDefinition"

    // Delegated rules


 

    public static final BitSet FOLLOW_UNIT_in_code_unit54 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PACKAGE_in_code_unit62 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_class_def_in_code_unit79 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLASS_in_class_def107 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FIELDS_in_class_def123 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fieldDefinition_in_class_def127 = new BitSet(new long[]{0x0044400000010008L});
    public static final BitSet FOLLOW_METHODS_in_class_def132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_methodDefinition_in_class_def136 = new BitSet(new long[]{0x0000000000008008L});
    public static final BitSet FOLLOW_typeDefinition_in_fieldDefinition180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_fieldDefinition184 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_methodDefinition201 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_methodDefinition205 = new BitSet(new long[]{0x0044400000010000L});
    public static final BitSet FOLLOW_methodReturnDefinition_in_methodDefinition209 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_FORMALARGS_in_methodDefinition212 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_formalArg_in_methodDefinition216 = new BitSet(new long[]{0x0044400000010008L});
    public static final BitSet FOLLOW_block_in_methodDefinition222 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_54_in_methodReturnDefinition265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_methodReturnDefinition276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_methodReturnDefinition287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_methodReturnDefinition298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinition_in_formalArg319 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_formalArg323 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block344 = new BitSet(new long[]{0x0000005608040068L});
    public static final BitSet FOLLOW_block_in_statement367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_statement374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vardef_in_statement381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerOp_in_statement388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_assignment403 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_assignment407 = new BitSet(new long[]{0x0000005608040060L});
    public static final BitSet FOLLOW_statement_in_assignment411 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARDEF_in_vardef439 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeDefinition_in_vardef443 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_vardef447 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_34_in_integerOp464 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_integerOp468 = new BitSet(new long[]{0x0000005608040060L});
    public static final BitSet FOLLOW_statement_in_integerOp472 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_36_in_integerOp493 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_integerOp497 = new BitSet(new long[]{0x0000005608040060L});
    public static final BitSet FOLLOW_statement_in_integerOp501 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_integerOp522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_integerOp526 = new BitSet(new long[]{0x0000005608040060L});
    public static final BitSet FOLLOW_statement_in_integerOp530 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_integerOp551 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_integerOp555 = new BitSet(new long[]{0x0000005608040060L});
    public static final BitSet FOLLOW_statement_in_integerOp559 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INT_in_integerOp581 = new BitSet(new long[]{0x0000000000000002L});

}