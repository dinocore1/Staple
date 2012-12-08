// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g 2012-09-03 18:04:10

package com.devsmart;

import com.devsmart.symbol.*;
import com.devsmart.type.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SemPass2 extends TreeFilter {
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
    public TreeFilter[] getDelegates() {
        return new TreeFilter[] {};
    }

    // delegators


    public SemPass2(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public SemPass2(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SemPass2.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/SemPass2.g"; }


       
        ErrorStream errorstream;
        Scope currentScope;
        ClassSymbol currentClass;
        MethodSymbol currentMethod;
        
        public SemPass2(TreeNodeStream input, ErrorStream estream) {
            this(input);
            errorstream = estream;
        }



    // $ANTLR start "topdown"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:28:1: topdown : ( enterClass | enterFieldDefinition | enterMethodDefinition | enterFormalArgs | enterBlock );
    public final void topdown() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:29:2: ( enterClass | enterFieldDefinition | enterMethodDefinition | enterFormalArgs | enterBlock )
            int alt1=5;
            switch ( input.LA(1) ) {
            case CLASS:
                {
                alt1=1;
                }
                break;
            case FIELDS:
                {
                alt1=2;
                }
                break;
            case METHODS:
                {
                alt1=3;
                }
                break;
            case FORMALARGS:
                {
                alt1=4;
                }
                break;
            case BLOCK:
                {
                alt1=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:29:4: enterClass
                    {
                    pushFollow(FOLLOW_enterClass_in_topdown53);
                    enterClass();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:30:4: enterFieldDefinition
                    {
                    pushFollow(FOLLOW_enterFieldDefinition_in_topdown58);
                    enterFieldDefinition();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:31:4: enterMethodDefinition
                    {
                    pushFollow(FOLLOW_enterMethodDefinition_in_topdown63);
                    enterMethodDefinition();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:32:4: enterFormalArgs
                    {
                    pushFollow(FOLLOW_enterFormalArgs_in_topdown68);
                    enterFormalArgs();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:33:4: enterBlock
                    {
                    pushFollow(FOLLOW_enterBlock_in_topdown73);
                    enterBlock();

                    state._fsp--;
                    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "topdown"



    // $ANTLR start "bottomup"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:36:1: bottomup : ( exitClass | exitMethodDefinition | exitBlock );
    public final void bottomup() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:37:2: ( exitClass | exitMethodDefinition | exitBlock )
            int alt2=3;
            switch ( input.LA(1) ) {
            case CLASS:
                {
                alt2=1;
                }
                break;
            case FUNCTION:
                {
                alt2=2;
                }
                break;
            case BLOCK:
                {
                alt2=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }

            switch (alt2) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:37:4: exitClass
                    {
                    pushFollow(FOLLOW_exitClass_in_bottomup87);
                    exitClass();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:38:4: exitMethodDefinition
                    {
                    pushFollow(FOLLOW_exitMethodDefinition_in_bottomup92);
                    exitMethodDefinition();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:39:4: exitBlock
                    {
                    pushFollow(FOLLOW_exitBlock_in_bottomup97);
                    exitBlock();

                    state._fsp--;
                    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "bottomup"



    // $ANTLR start "enterClass"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:42:1: enterClass : ^( CLASS cname= ID subclass= ID ( . )* ) ;
    public final void enterClass() throws RecognitionException {
        StapleTree cname=null;
        StapleTree subclass=null;
        StapleTree CLASS1=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:43:2: ( ^( CLASS cname= ID subclass= ID ( . )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:43:4: ^( CLASS cname= ID subclass= ID ( . )* )
            {
            CLASS1=(StapleTree)match(input,CLASS,FOLLOW_CLASS_in_enterClass116); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            cname=(StapleTree)match(input,ID,FOLLOW_ID_in_enterClass120); if (state.failed) return ;

            subclass=(StapleTree)match(input,ID,FOLLOW_ID_in_enterClass124); if (state.failed) return ;

            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:43:33: ( . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==UP) ) {
                    alt3=2;
                }
                else if ( ((LA3_0 >= ARGS && LA3_0 <= 56)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:43:33: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
            		currentClass = (ClassSymbol)CLASS1.symbol;
            		currentClass.subclass = (ClassSymbol)currentClass.scope.resolve((subclass!=null?subclass.getText():null));
            		if(currentClass.subclass == null){
            			errorstream.addSymanticError(subclass.token, "Undefined class " + (subclass!=null?subclass.getText():null));
            		}
            		currentScope = currentClass.scope;
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
        return ;
    }
    // $ANTLR end "enterClass"



    // $ANTLR start "exitClass"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:54:1: exitClass : CLASS ;
    public final void exitClass() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:55:2: ( CLASS )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:55:4: CLASS
            {
            match(input,CLASS,FOLLOW_CLASS_in_exitClass144); if (state.failed) return ;

            if ( state.backtracking==1 ) {
            		currentClass = null;
            		currentScope = currentScope.pop();
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
        return ;
    }
    // $ANTLR end "exitClass"



    // $ANTLR start "enterFieldDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:62:1: enterFieldDefinition : ^( FIELDS ( ^(t= typeDefinition name= ID ) )* ) ;
    public final void enterFieldDefinition() throws RecognitionException {
        StapleTree name=null;
        AbstractType t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:63:2: ( ^( FIELDS ( ^(t= typeDefinition name= ID ) )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:63:4: ^( FIELDS ( ^(t= typeDefinition name= ID ) )* )
            {
            match(input,FIELDS,FOLLOW_FIELDS_in_enterFieldDefinition160); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:64:3: ( ^(t= typeDefinition name= ID ) )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ID||LA4_0==46||LA4_0==50||LA4_0==54) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:65:3: ^(t= typeDefinition name= ID )
                	    {
                	    pushFollow(FOLLOW_typeDefinition_in_enterFieldDefinition172);
                	    t=typeDefinition();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    match(input, Token.DOWN, null); if (state.failed) return ;
                	    name=(StapleTree)match(input,ID,FOLLOW_ID_in_enterFieldDefinition176); if (state.failed) return ;

                	    match(input, Token.UP, null); if (state.failed) return ;


                	    if ( state.backtracking==1 ) {
                	    				VarableSymbol fs = new VarableSymbol((name!=null?name.getText():null), t);
                	    				name.symbol = fs;
                	    				if(currentScope.resolve((name!=null?name.getText():null)) != null){
                	    					errorstream.addSymanticError(name.token, "Redefinition of symbol " + (name!=null?name.getText():null));
                	    				}
                	    				currentScope.define(fs);
                	    				currentClass.fields.put(fs.getName(), fs);
                	    			}

                	    }
                	    break;

                	default :
                	    break loop4;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
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
        return ;
    }
    // $ANTLR end "enterFieldDefinition"



    // $ANTLR start "enterMethodDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:79:1: enterMethodDefinition : ^( METHODS ( ^( FUNCTION name= ID rtype= typeDefinition ( . )* ) )* ) ;
    public final void enterMethodDefinition() throws RecognitionException {
        StapleTree name=null;
        StapleTree FUNCTION2=null;
        AbstractType rtype =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:80:2: ( ^( METHODS ( ^( FUNCTION name= ID rtype= typeDefinition ( . )* ) )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:80:4: ^( METHODS ( ^( FUNCTION name= ID rtype= typeDefinition ( . )* ) )* )
            {
            match(input,METHODS,FOLLOW_METHODS_in_enterMethodDefinition204); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:81:3: ( ^( FUNCTION name= ID rtype= typeDefinition ( . )* ) )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==FUNCTION) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:82:3: ^( FUNCTION name= ID rtype= typeDefinition ( . )* )
                	    {
                	    FUNCTION2=(StapleTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_enterMethodDefinition213); if (state.failed) return ;

                	    match(input, Token.DOWN, null); if (state.failed) return ;
                	    name=(StapleTree)match(input,ID,FOLLOW_ID_in_enterMethodDefinition217); if (state.failed) return ;

                	    pushFollow(FOLLOW_typeDefinition_in_enterMethodDefinition221);
                	    rtype=typeDefinition();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:82:43: ( . )*
                	    loop5:
                	    do {
                	        int alt5=2;
                	        int LA5_0 = input.LA(1);

                	        if ( (LA5_0==UP) ) {
                	            alt5=2;
                	        }
                	        else if ( ((LA5_0 >= ARGS && LA5_0 <= 56)) ) {
                	            alt5=1;
                	        }


                	        switch (alt5) {
                	    	case 1 :
                	    	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:82:43: .
                	    	    {
                	    	    matchAny(input); if (state.failed) return ;

                	    	    }
                	    	    break;

                	    	default :
                	    	    break loop5;
                	        }
                	    } while (true);


                	    match(input, Token.UP, null); if (state.failed) return ;


                	    if ( state.backtracking==1 ) {
                	    				MethodSymbol ms = new MethodSymbol((name!=null?name.getText():null), rtype, currentClass);
                	    				FUNCTION2.symbol = ms;
                	    				
                	    				if(currentScope.resolve((name!=null?name.getText():null)) != null){
                	    					errorstream.addSymanticError(name.token, "Redefinition of symbol " + (name!=null?name.getText():null));
                	    				}
                	    				currentScope.define(ms);
                	    				currentClass.methods.put((name!=null?name.getText():null) , ms);
                	    				
                	    				currentScope = currentScope.push();
                	    				ms.scope = currentScope;
                	    				
                	    				currentMethod = ms;
                	    				
                	    			}

                	    }
                	    break;

                	default :
                	    break loop6;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
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
        return ;
    }
    // $ANTLR end "enterMethodDefinition"



    // $ANTLR start "exitMethodDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:103:1: exitMethodDefinition : FUNCTION ;
    public final void exitMethodDefinition() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:104:2: ( FUNCTION )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:104:4: FUNCTION
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_exitMethodDefinition251); if (state.failed) return ;

            if ( state.backtracking==1 ) {
            			currentScope = currentScope.pop();
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
        return ;
    }
    // $ANTLR end "exitMethodDefinition"



    // $ANTLR start "enterFormalArgs"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:110:1: enterFormalArgs : ^( FORMALARGS ( ^(t= typeDefinition name= ID ) )* ) ;
    public final void enterFormalArgs() throws RecognitionException {
        StapleTree name=null;
        AbstractType t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:111:2: ( ^( FORMALARGS ( ^(t= typeDefinition name= ID ) )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:111:4: ^( FORMALARGS ( ^(t= typeDefinition name= ID ) )* )
            {
            match(input,FORMALARGS,FOLLOW_FORMALARGS_in_enterFormalArgs268); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:112:3: ( ^(t= typeDefinition name= ID ) )*
                loop7:
                do {
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ID||LA7_0==46||LA7_0==50||LA7_0==54) ) {
                        alt7=1;
                    }


                    switch (alt7) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:113:3: ^(t= typeDefinition name= ID )
                	    {
                	    pushFollow(FOLLOW_typeDefinition_in_enterFormalArgs280);
                	    t=typeDefinition();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    match(input, Token.DOWN, null); if (state.failed) return ;
                	    name=(StapleTree)match(input,ID,FOLLOW_ID_in_enterFormalArgs284); if (state.failed) return ;

                	    match(input, Token.UP, null); if (state.failed) return ;


                	    if ( state.backtracking==1 ) {
                	    				VarableSymbol fs = new VarableSymbol((name!=null?name.getText():null), t);
                	    				name.symbol = fs;
                	    				if(currentScope.resolve((name!=null?name.getText():null)) != null){
                	    					errorstream.addSymanticError(name.token, "Redefinition of symbol " + (name!=null?name.getText():null));
                	    				}
                	    				currentScope.define(fs);
                	    				currentMethod.formalArgs.add(fs);
                	    			}

                	    }
                	    break;

                	default :
                	    break loop7;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
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
        return ;
    }
    // $ANTLR end "enterFormalArgs"



    // $ANTLR start "typeDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:128:1: typeDefinition returns [AbstractType value] : ( 'void' | 'int' | 'bool' |name= ID );
    public final AbstractType typeDefinition() throws RecognitionException {
        AbstractType value = null;


        StapleTree name=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:129:2: ( 'void' | 'int' | 'bool' |name= ID )
            int alt8=4;
            switch ( input.LA(1) ) {
            case 54:
                {
                alt8=1;
                }
                break;
            case 50:
                {
                alt8=2;
                }
                break;
            case 46:
                {
                alt8=3;
                }
                break;
            case ID:
                {
                alt8=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return value;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:129:4: 'void'
                    {
                    match(input,54,FOLLOW_54_in_typeDefinition320); if (state.failed) return value;

                    if ( state.backtracking==1 ) { value = PrimitiveType.VOID; }

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:130:4: 'int'
                    {
                    match(input,50,FOLLOW_50_in_typeDefinition327); if (state.failed) return value;

                    if ( state.backtracking==1 ) { value = PrimitiveType.INT; }

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:131:4: 'bool'
                    {
                    match(input,46,FOLLOW_46_in_typeDefinition335); if (state.failed) return value;

                    if ( state.backtracking==1 ) { value = PrimitiveType.BOOL; }

                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:132:4: name= ID
                    {
                    name=(StapleTree)match(input,ID,FOLLOW_ID_in_typeDefinition344); if (state.failed) return value;

                    if ( state.backtracking==1 ) { 
                    					AbstractSymbol classtype = currentScope.resolve((name!=null?name.getText():null));
                    					if(classtype == null){
                    						errorstream.addSymanticError(name.token, "Undefined class " + (name!=null?name.getText():null));
                    						return null;
                    					}
                    					if(classtype instanceof ClassSymbol){
                    						errorstream.addSymanticError(name.token, (name!=null?name.getText():null) + " is not a class type");
                    						return null;
                    					}
                    					value = new ClassType((ClassSymbol)classtype);
                    					
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
        return value;
    }
    // $ANTLR end "typeDefinition"



    // $ANTLR start "enterBlock"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:147:1: enterBlock : ^( BLOCK ( statement )* ) ;
    public final void enterBlock() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:148:2: ( ^( BLOCK ( statement )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:148:4: ^( BLOCK ( statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_enterBlock359); if (state.failed) return ;

            if ( state.backtracking==1 ) { currentScope = currentScope.push(); }

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:149:3: ( statement )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ASSIGN||LA9_0==CALL||LA9_0==FIELDACCESS||LA9_0==ID||LA9_0==INT||LA9_0==THIS||LA9_0==VARDEF||(LA9_0 >= 33 && LA9_0 <= 34)||LA9_0==36||LA9_0==38) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:149:3: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_enterBlock365);
                	    statement();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop9;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
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
        return ;
    }
    // $ANTLR end "enterBlock"



    // $ANTLR start "statement"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:153:1: statement : ( assignment | typeTree );
    public final void statement() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:154:2: ( assignment | typeTree )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ASSIGN) ) {
                alt10=1;
            }
            else if ( (LA10_0==CALL||LA10_0==FIELDACCESS||LA10_0==ID||LA10_0==INT||LA10_0==THIS||LA10_0==VARDEF||(LA10_0 >= 33 && LA10_0 <= 34)||LA10_0==36||LA10_0==38) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:154:4: assignment
                    {
                    pushFollow(FOLLOW_assignment_in_statement382);
                    assignment();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:155:4: typeTree
                    {
                    pushFollow(FOLLOW_typeTree_in_statement387);
                    typeTree();

                    state._fsp--;
                    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "statement"



    // $ANTLR start "exitBlock"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:158:1: exitBlock : BLOCK ;
    public final void exitBlock() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:159:2: ( BLOCK )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:159:4: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_exitBlock400); if (state.failed) return ;

            if ( state.backtracking==1 ) { currentScope = currentScope.pop(); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "exitBlock"



    // $ANTLR start "assignment"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:162:1: assignment : ^( ASSIGN l= typeTree r= typeTree ) ;
    public final void assignment() throws RecognitionException {
        StapleTree ASSIGN3=null;
        AbstractType l =null;

        AbstractType r =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:163:2: ( ^( ASSIGN l= typeTree r= typeTree ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:163:4: ^( ASSIGN l= typeTree r= typeTree )
            {
            ASSIGN3=(StapleTree)match(input,ASSIGN,FOLLOW_ASSIGN_in_assignment415); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_typeTree_in_assignment419);
            l=typeTree();

            state._fsp--;
            if (state.failed) return ;

            pushFollow(FOLLOW_typeTree_in_assignment423);
            r=typeTree();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
            			if(l != r){
            				errorstream.addSymanticError(ASSIGN3.token, "not matching assignable types");
            			}
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
        return ;
    }
    // $ANTLR end "assignment"



    // $ANTLR start "typeTree"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:171:1: typeTree returns [AbstractType value] : (name= ID | THIS | INT | ^(s= ( '+' | '-' | '*' | '/' ) l= typeTree r= typeTree ) | ^( FIELDACCESS base= typeTree field= ID ) | ^( CALL base= typeTree name= ID ( . )* ) | ^( VARDEF t= typeDefinition name= ID ) );
    public final AbstractType typeTree() throws RecognitionException {
        AbstractType value = null;


        StapleTree name=null;
        StapleTree s=null;
        StapleTree field=null;
        StapleTree FIELDACCESS4=null;
        StapleTree CALL5=null;
        AbstractType l =null;

        AbstractType r =null;

        AbstractType base =null;

        AbstractType t =null;


        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:172:2: (name= ID | THIS | INT | ^(s= ( '+' | '-' | '*' | '/' ) l= typeTree r= typeTree ) | ^( FIELDACCESS base= typeTree field= ID ) | ^( CALL base= typeTree name= ID ( . )* ) | ^( VARDEF t= typeDefinition name= ID ) )
            int alt12=7;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt12=1;
                }
                break;
            case THIS:
                {
                alt12=2;
                }
                break;
            case INT:
                {
                alt12=3;
                }
                break;
            case 33:
            case 34:
            case 36:
            case 38:
                {
                alt12=4;
                }
                break;
            case FIELDACCESS:
                {
                alt12=5;
                }
                break;
            case CALL:
                {
                alt12=6;
                }
                break;
            case VARDEF:
                {
                alt12=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return value;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:172:4: name= ID
                    {
                    name=(StapleTree)match(input,ID,FOLLOW_ID_in_typeTree446); if (state.failed) return value;

                    if ( state.backtracking==1 ) { 
                    			VarableSymbol vs = (VarableSymbol)currentScope.resolve((name!=null?name.getText():null));
                    			if(vs == null){
                    				errorstream.addSymanticError(name.token, "Undefined symbol " + (name!=null?name.getText():null));
                    			}
                    			value = vs.type;
                    		}

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:180:4: THIS
                    {
                    match(input,THIS,FOLLOW_THIS_in_typeTree456); if (state.failed) return value;

                    if ( state.backtracking==1 ) { value = currentClass.type; }

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:181:4: INT
                    {
                    match(input,INT,FOLLOW_INT_in_typeTree463); if (state.failed) return value;

                    if ( state.backtracking==1 ) { value = PrimitiveType.INT; }

                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:182:4: ^(s= ( '+' | '-' | '*' | '/' ) l= typeTree r= typeTree )
                    {
                    s=(StapleTree)input.LT(1);

                    if ( (input.LA(1) >= 33 && input.LA(1) <= 34)||input.LA(1)==36||input.LA(1)==38 ) {
                        input.consume();
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return value;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.DOWN, null); if (state.failed) return value;
                    pushFollow(FOLLOW_typeTree_in_typeTree486);
                    l=typeTree();

                    state._fsp--;
                    if (state.failed) return value;

                    pushFollow(FOLLOW_typeTree_in_typeTree490);
                    r=typeTree();

                    state._fsp--;
                    if (state.failed) return value;

                    match(input, Token.UP, null); if (state.failed) return value;


                    if ( state.backtracking==1 ) { 
                    			if(l != PrimitiveType.INT || r != PrimitiveType.INT){
                    				errorstream.addSymanticError(s.token, "Cannot perform operation on incompatible types");
                    			}
                    			value = PrimitiveType.INT; 
                    		}

                    }
                    break;
                case 5 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:189:4: ^( FIELDACCESS base= typeTree field= ID )
                    {
                    FIELDACCESS4=(StapleTree)match(input,FIELDACCESS,FOLLOW_FIELDACCESS_in_typeTree504); if (state.failed) return value;

                    match(input, Token.DOWN, null); if (state.failed) return value;
                    pushFollow(FOLLOW_typeTree_in_typeTree508);
                    base=typeTree();

                    state._fsp--;
                    if (state.failed) return value;

                    field=(StapleTree)match(input,ID,FOLLOW_ID_in_typeTree512); if (state.failed) return value;

                    match(input, Token.UP, null); if (state.failed) return value;


                    if ( state.backtracking==1 ) { 
                    			if(!(base instanceof ClassType)){
                    				errorstream.addSymanticError(FIELDACCESS4.token, "Cannot access a field of a non-class type");
                    			} else {
                    				ClassSymbol classSymbol = ((ClassType)base).symbol;
                    				VarableSymbol fieldSymbol = classSymbol.fields.get((field!=null?field.getText():null));
                    				if(fieldSymbol == null){
                    					errorstream.addSymanticError(field.token, "Class '" + classSymbol.getName() + "' does not have a field: " + (field!=null?field.getText():null));
                    				} else {
                    					value = fieldSymbol.type;
                    				}
                    			}
                    			 
                    		}

                    }
                    break;
                case 6 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:204:4: ^( CALL base= typeTree name= ID ( . )* )
                    {
                    CALL5=(StapleTree)match(input,CALL,FOLLOW_CALL_in_typeTree525); if (state.failed) return value;

                    match(input, Token.DOWN, null); if (state.failed) return value;
                    pushFollow(FOLLOW_typeTree_in_typeTree529);
                    base=typeTree();

                    state._fsp--;
                    if (state.failed) return value;

                    name=(StapleTree)match(input,ID,FOLLOW_ID_in_typeTree533); if (state.failed) return value;

                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:204:33: ( . )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==UP) ) {
                            alt11=2;
                        }
                        else if ( ((LA11_0 >= ARGS && LA11_0 <= 56)) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:204:33: .
                    	    {
                    	    matchAny(input); if (state.failed) return value;

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return value;


                    if ( state.backtracking==1 ) {
                    			if(!(base instanceof ClassType)){
                    				errorstream.addSymanticError(CALL5.token, "Cannot make a class on a non-class type");
                    			} else {
                    				ClassSymbol classSymbol = ((ClassType)base).symbol;
                    				MethodSymbol methodSymbol = classSymbol.methods.get((name!=null?name.getText():null));
                    				if(methodSymbol == null){
                    					errorstream.addSymanticError(name.token, "Class '" + classSymbol.getName() + "' does not have a method: " + (name!=null?name.getText():null));
                    				} else {
                    					value = methodSymbol.returnType;
                    				}
                    			}
                    		}

                    }
                    break;
                case 7 :
                    // /home/paul/workspace/Staple/src/com/devsmart/SemPass2.g:218:4: ^( VARDEF t= typeDefinition name= ID )
                    {
                    match(input,VARDEF,FOLLOW_VARDEF_in_typeTree547); if (state.failed) return value;

                    match(input, Token.DOWN, null); if (state.failed) return value;
                    pushFollow(FOLLOW_typeDefinition_in_typeTree551);
                    t=typeDefinition();

                    state._fsp--;
                    if (state.failed) return value;

                    name=(StapleTree)match(input,ID,FOLLOW_ID_in_typeTree555); if (state.failed) return value;

                    match(input, Token.UP, null); if (state.failed) return value;


                    if ( state.backtracking==1 ) {
                    			VarableSymbol vs = new VarableSymbol((name!=null?name.getText():null), t);
                    			name.symbol = vs;
                    			if(currentScope.resolve((name!=null?name.getText():null)) != null){
                    				errorstream.addSymanticError(name.token, "Redefinition of symbol " + (name!=null?name.getText():null));
                    			}
                    			currentScope.define(vs);
                    			value = t;
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
        return value;
    }
    // $ANTLR end "typeTree"

    // Delegated rules


 

    public static final BitSet FOLLOW_enterClass_in_topdown53 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterFieldDefinition_in_topdown58 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterMethodDefinition_in_topdown63 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterFormalArgs_in_topdown68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterBlock_in_topdown73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitClass_in_bottomup87 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitMethodDefinition_in_bottomup92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitBlock_in_bottomup97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_enterClass116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterClass120 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_enterClass124 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_CLASS_in_exitClass144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FIELDS_in_enterFieldDefinition160 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeDefinition_in_enterFieldDefinition172 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterFieldDefinition176 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_METHODS_in_enterMethodDefinition204 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FUNCTION_in_enterMethodDefinition213 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterMethodDefinition217 = new BitSet(new long[]{0x0044400000010000L});
    public static final BitSet FOLLOW_typeDefinition_in_enterMethodDefinition221 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_FUNCTION_in_exitMethodDefinition251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORMALARGS_in_enterFormalArgs268 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeDefinition_in_enterFormalArgs280 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterFormalArgs284 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_54_in_typeDefinition320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_typeDefinition327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_typeDefinition335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_typeDefinition344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_enterBlock359 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_enterBlock365 = new BitSet(new long[]{0x000000560A0510A8L});
    public static final BitSet FOLLOW_assignment_in_statement382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeTree_in_statement387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_exitBlock400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_assignment415 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeTree_in_assignment419 = new BitSet(new long[]{0x000000560A051080L});
    public static final BitSet FOLLOW_typeTree_in_assignment423 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_typeTree446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_typeTree456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_typeTree463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeTree474 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeTree_in_typeTree486 = new BitSet(new long[]{0x000000560A051080L});
    public static final BitSet FOLLOW_typeTree_in_typeTree490 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FIELDACCESS_in_typeTree504 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeTree_in_typeTree508 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_typeTree512 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_typeTree525 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeTree_in_typeTree529 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_typeTree533 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_VARDEF_in_typeTree547 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_typeDefinition_in_typeTree551 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_typeTree555 = new BitSet(new long[]{0x0000000000000008L});

}