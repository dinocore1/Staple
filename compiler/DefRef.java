// $ANTLR 3.4 DefRef.g 2011-09-18 00:16:30

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class DefRef extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARG", "ARGS", "ARRAY", "ASSIGN", "BLOCK", "CALL", "CLASSDEF", "COMMENT", "ELIST", "EXPR", "EXTERNFUNC", "EXTERNVAR", "FILE", "FUNCDEF", "ID", "INDEX", "INT", "LETTER", "LINE_COMMENT", "STRING", "VARDEF", "WS", "'!='", "'('", "')'", "'*'", "'+'", "','", "'-'", "'/'", "';'", "'<'", "'<='", "'=='", "'>'", "'>='", "'['", "']'", "'class'", "'else'", "'if'", "'int'", "'return'", "'void'", "'while'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
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
    public static final int ARG=4;
    public static final int ARGS=5;
    public static final int ARRAY=6;
    public static final int ASSIGN=7;
    public static final int BLOCK=8;
    public static final int CALL=9;
    public static final int CLASSDEF=10;
    public static final int COMMENT=11;
    public static final int ELIST=12;
    public static final int EXPR=13;
    public static final int EXTERNFUNC=14;
    public static final int EXTERNVAR=15;
    public static final int FILE=16;
    public static final int FUNCDEF=17;
    public static final int ID=18;
    public static final int INDEX=19;
    public static final int INT=20;
    public static final int LETTER=21;
    public static final int LINE_COMMENT=22;
    public static final int STRING=23;
    public static final int VARDEF=24;
    public static final int WS=25;

    // delegates
    public TreeFilter[] getDelegates() {
        return new TreeFilter[] {};
    }

    // delegators


    public DefRef(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public DefRef(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return DefRef.tokenNames; }
    public String getGrammarFileName() { return "DefRef.g"; }


        SymbolTable symtab;
        Scope currentScope;
        public DefRef(TreeNodeStream input, SymbolTable symtab) {
            this(input);
            this.symtab = symtab;
            currentScope = symtab.globals;
        }



    // $ANTLR start "topdown"
    // DefRef.g:17:1: topdown : ( enterClass | enterBlock | enterFunction | varDeclaration );
    public final void topdown() throws RecognitionException {
        try {
            // DefRef.g:18:2: ( enterClass | enterBlock | enterFunction | varDeclaration )
            int alt1=4;
            switch ( input.LA(1) ) {
            case CLASSDEF:
                {
                alt1=1;
                }
                break;
            case BLOCK:
                {
                alt1=2;
                }
                break;
            case EXTERNFUNC:
            case FUNCDEF:
                {
                alt1=3;
                }
                break;
            case ARG:
            case EXTERNVAR:
            case VARDEF:
                {
                alt1=4;
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
                    // DefRef.g:18:6: enterClass
                    {
                    pushFollow(FOLLOW_enterClass_in_topdown51);
                    enterClass();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // DefRef.g:19:9: enterBlock
                    {
                    pushFollow(FOLLOW_enterBlock_in_topdown61);
                    enterBlock();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // DefRef.g:20:9: enterFunction
                    {
                    pushFollow(FOLLOW_enterFunction_in_topdown71);
                    enterFunction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // DefRef.g:21:9: varDeclaration
                    {
                    pushFollow(FOLLOW_varDeclaration_in_topdown81);
                    varDeclaration();

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
    // DefRef.g:24:1: bottomup : ( exitClass | exitBlock | exitFunction | idref | call );
    public final void bottomup() throws RecognitionException {
        try {
            // DefRef.g:25:2: ( exitClass | exitBlock | exitFunction | idref | call )
            int alt2=5;
            switch ( input.LA(1) ) {
            case CLASSDEF:
                {
                alt2=1;
                }
                break;
            case BLOCK:
                {
                alt2=2;
                }
                break;
            case EXTERNFUNC:
            case FUNCDEF:
                {
                alt2=3;
                }
                break;
            case ID:
                {
                alt2=4;
                }
                break;
            case CALL:
                {
                alt2=5;
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
                    // DefRef.g:25:6: exitClass
                    {
                    pushFollow(FOLLOW_exitClass_in_bottomup97);
                    exitClass();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // DefRef.g:26:9: exitBlock
                    {
                    pushFollow(FOLLOW_exitBlock_in_bottomup107);
                    exitBlock();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // DefRef.g:27:9: exitFunction
                    {
                    pushFollow(FOLLOW_exitFunction_in_bottomup117);
                    exitFunction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // DefRef.g:28:9: idref
                    {
                    pushFollow(FOLLOW_idref_in_bottomup127);
                    idref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // DefRef.g:29:7: call
                    {
                    pushFollow(FOLLOW_call_in_bottomup135);
                    call();

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
    // DefRef.g:34:1: enterClass : ^( CLASSDEF ID ( . )* ) ;
    public final void enterClass() throws RecognitionException {
        CTree ID1=null;

        try {
            // DefRef.g:35:2: ( ^( CLASSDEF ID ( . )* ) )
            // DefRef.g:35:6: ^( CLASSDEF ID ( . )* )
            {
            match(input,CLASSDEF,FOLLOW_CLASSDEF_in_enterClass154); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(CTree)match(input,ID,FOLLOW_ID_in_enterClass156); if (state.failed) return ;

            // DefRef.g:35:20: ( . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= ARG && LA3_0 <= 50)) ) {
                    alt3=1;
                }
                else if ( (LA3_0==UP) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // DefRef.g:35:20: .
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
            		ClassSymbol classSymbol = new ClassSymbol((ID1!=null?ID1.getText():null), currentScope);
            		currentScope.define(classSymbol);
            		currentScope = classSymbol;
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
    // DefRef.g:43:1: exitClass : CLASSDEF ;
    public final void exitClass() throws RecognitionException {
        try {
            // DefRef.g:44:2: ( CLASSDEF )
            // DefRef.g:44:6: CLASSDEF
            {
            match(input,CLASSDEF,FOLLOW_CLASSDEF_in_exitClass179); if (state.failed) return ;

            if ( state.backtracking==1 ) {currentScope = currentScope.getEnclosingScope();}

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



    // $ANTLR start "enterBlock"
    // DefRef.g:47:1: enterBlock : BLOCK ;
    public final void enterBlock() throws RecognitionException {
        try {
            // DefRef.g:48:5: ( BLOCK )
            // DefRef.g:48:9: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_enterBlock197); if (state.failed) return ;

            if ( state.backtracking==1 ) {currentScope = new LocalScope(currentScope);}

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



    // $ANTLR start "exitBlock"
    // DefRef.g:50:1: exitBlock : BLOCK ;
    public final void exitBlock() throws RecognitionException {
        try {
            // DefRef.g:51:5: ( BLOCK )
            // DefRef.g:51:9: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_exitBlock217); if (state.failed) return ;

            if ( state.backtracking==1 ) {
                    // System.out.println("locals: "+currentScope);
                    currentScope = currentScope.getEnclosingScope();    // pop scope
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
    // $ANTLR end "exitBlock"



    // $ANTLR start "enterFunction"
    // DefRef.g:58:1: enterFunction : ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* ) ;
    public final void enterFunction() throws RecognitionException {
        CTree ID2=null;
        Type type_tree3 =null;


        try {
            // DefRef.g:59:5: ( ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* ) )
            // DefRef.g:59:9: ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* )
            {
            if ( input.LA(1)==EXTERNFUNC||input.LA(1)==FUNCDEF ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            match(input, Token.DOWN, null); if (state.failed) return ;
            ID2=(CTree)match(input,ID,FOLLOW_ID_in_enterFunction254); if (state.failed) return ;

            pushFollow(FOLLOW_type_tree_in_enterFunction256);
            type_tree3=type_tree();

            state._fsp--;
            if (state.failed) return ;

            // DefRef.g:59:45: ( . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= ARG && LA4_0 <= 50)) ) {
                    alt4=1;
                }
                else if ( (LA4_0==UP) ) {
                    alt4=2;
                }


                switch (alt4) {
            	case 1 :
            	    // DefRef.g:59:45: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
                    // System.out.println("line "+ID2.getLine()+": def method "+(ID2!=null?ID2.getText():null));
                    Type retType = type_tree3; // rule type returns a Type symbol
            		FunctionSymbol fs = new FunctionSymbol((ID2!=null?ID2.getText():null),type_tree3,currentScope);
            		ID2.symbol = fs;
                    currentScope.define(fs); // def method in globals
                    currentScope = fs;       // set current scope to method scope
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
    // $ANTLR end "enterFunction"



    // $ANTLR start "exitFunction"
    // DefRef.g:69:1: exitFunction : ( FUNCDEF | EXTERNFUNC ) ;
    public final void exitFunction() throws RecognitionException {
        try {
            // DefRef.g:70:5: ( ( FUNCDEF | EXTERNFUNC ) )
            // DefRef.g:70:9: ( FUNCDEF | EXTERNFUNC )
            {
            if ( input.LA(1)==EXTERNFUNC||input.LA(1)==FUNCDEF ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            if ( state.backtracking==1 ) {
                    // System.out.println("args: "+currentScope);
                    currentScope = currentScope.getEnclosingScope();// pop arg scope
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
    // $ANTLR end "exitFunction"



    // $ANTLR start "type_tree"
    // DefRef.g:77:1: type_tree returns [Type type] : ( ^( ARRAY t= type_tree e= . ) | type_specifier );
    public final Type type_tree() throws RecognitionException {
        Type type = null;


        CTree e=null;
        Type t =null;

        DefRef.type_specifier_return type_specifier4 =null;


        try {
            // DefRef.g:78:2: ( ^( ARRAY t= type_tree e= . ) | type_specifier )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ARRAY) ) {
                alt5=1;
            }
            else if ( (LA5_0==45||LA5_0==47) ) {
                alt5=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return type;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // DefRef.g:78:4: ^( ARRAY t= type_tree e= . )
                    {
                    match(input,ARRAY,FOLLOW_ARRAY_in_type_tree322); if (state.failed) return type;

                    match(input, Token.DOWN, null); if (state.failed) return type;
                    pushFollow(FOLLOW_type_tree_in_type_tree326);
                    t=type_tree();

                    state._fsp--;
                    if (state.failed) return type;

                    e=(CTree)input.LT(1);

                    matchAny(input); if (state.failed) return type;

                    match(input, Token.UP, null); if (state.failed) return type;


                    if ( state.backtracking==1 ) {type = new ArrayType(t,e);}

                    }
                    break;
                case 2 :
                    // DefRef.g:79:4: type_specifier
                    {
                    pushFollow(FOLLOW_type_specifier_in_type_tree338);
                    type_specifier4=type_specifier();

                    state._fsp--;
                    if (state.failed) return type;

                    if ( state.backtracking==1 ) {type = (type_specifier4!=null?type_specifier4.type:null);}

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
        return type;
    }
    // $ANTLR end "type_tree"


    public static class type_specifier_return extends TreeRuleReturnScope {
        public Type type;
    };


    // $ANTLR start "type_specifier"
    // DefRef.g:82:1: type_specifier returns [Type type] : ( 'void' | 'int' );
    public final DefRef.type_specifier_return type_specifier() throws RecognitionException {
        DefRef.type_specifier_return retval = new DefRef.type_specifier_return();
        retval.start = input.LT(1);


         retval.type = (Type)currentScope.resolve(((CTree)retval.start).getText()); 
        try {
            // DefRef.g:84:2: ( 'void' | 'int' )
            // DefRef.g:
            {
            if ( input.LA(1)==45||input.LA(1)==47 ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
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
    // $ANTLR end "type_specifier"



    // $ANTLR start "varDeclaration"
    // DefRef.g:90:1: varDeclaration : ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree ) ;
    public final void varDeclaration() throws RecognitionException {
        CTree ID5=null;
        Type type_tree6 =null;


        try {
            // DefRef.g:91:5: ( ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree ) )
            // DefRef.g:91:9: ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree )
            {
            if ( input.LA(1)==ARG||input.LA(1)==EXTERNVAR||input.LA(1)==VARDEF ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            match(input, Token.DOWN, null); if (state.failed) return ;
            ID5=(CTree)match(input,ID,FOLLOW_ID_in_varDeclaration398); if (state.failed) return ;

            pushFollow(FOLLOW_type_tree_in_varDeclaration400);
            type_tree6=type_tree();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
                    // System.out.println("line "+ID5.getLine()+": def "+(ID5!=null?ID5.getText():null)+" type "+type_tree6);
                    VariableSymbol vs = new VariableSymbol((ID5!=null?ID5.getText():null),type_tree6);
                    currentScope.define(vs);
                    ID5.symbol = vs;
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
    // $ANTLR end "varDeclaration"



    // $ANTLR start "call"
    // DefRef.g:102:1: call : ^( CALL ID . ) ;
    public final void call() throws RecognitionException {
        CTree ID7=null;

        try {
            // DefRef.g:102:5: ( ^( CALL ID . ) )
            // DefRef.g:102:9: ^( CALL ID . )
            {
            match(input,CALL,FOLLOW_CALL_in_call429); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID7=(CTree)match(input,ID,FOLLOW_ID_in_call431); if (state.failed) return ;

            matchAny(input); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
                    Symbol s = currentScope.resolve((ID7!=null?ID7.getText():null));
                    ID7.symbol = s;
                    // System.out.println("line "+ID7.getLine()+": call "+s);
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
    // $ANTLR end "call"


    public static class idref_return extends TreeRuleReturnScope {
    };


    // $ANTLR start "idref"
    // DefRef.g:110:1: idref :{...}? ID ;
    public final DefRef.idref_return idref() throws RecognitionException {
        DefRef.idref_return retval = new DefRef.idref_return();
        retval.start = input.LT(1);


        CTree ID8=null;

        try {
            // DefRef.g:111:5: ({...}? ID )
            // DefRef.g:111:9: {...}? ID
            {
            if ( !((((CTree)retval.start).hasAncestor(EXPR) || ((CTree)retval.start).hasAncestor(ASSIGN) ||
                	 ((CTree)retval.start).hasAncestor(ELIST))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "idref", "$start.hasAncestor(EXPR) || $start.hasAncestor(ASSIGN) ||\n    \t $start.hasAncestor(ELIST)");
            }

            ID8=(CTree)match(input,ID,FOLLOW_ID_in_idref463); if (state.failed) return retval;

            if ( state.backtracking==1 ) {
                    Symbol s = currentScope.resolve((ID8!=null?ID8.getText():null));
                    ID8.symbol = s;
                    // System.out.println("line "+ID8.getLine()+": ref "+s);
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
    // $ANTLR end "idref"

    // Delegated rules


 

    public static final BitSet FOLLOW_enterClass_in_topdown51 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterBlock_in_topdown61 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterFunction_in_topdown71 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varDeclaration_in_topdown81 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitClass_in_bottomup97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitBlock_in_bottomup107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitFunction_in_bottomup117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_idref_in_bottomup127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_bottomup135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASSDEF_in_enterClass154 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterClass156 = new BitSet(new long[]{0x0007FFFFFFFFFFF8L});
    public static final BitSet FOLLOW_CLASSDEF_in_exitClass179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_enterBlock197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_exitBlock217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_enterFunction248 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterFunction254 = new BitSet(new long[]{0x0000A00000000040L});
    public static final BitSet FOLLOW_type_tree_in_enterFunction256 = new BitSet(new long[]{0x0007FFFFFFFFFFF8L});
    public static final BitSet FOLLOW_set_in_exitFunction289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_in_type_tree322 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_tree_in_type_tree326 = new BitSet(new long[]{0x0007FFFFFFFFFFF0L});
    public static final BitSet FOLLOW_type_specifier_in_type_tree338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_varDeclaration390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_varDeclaration398 = new BitSet(new long[]{0x0000A00000000040L});
    public static final BitSet FOLLOW_type_tree_in_varDeclaration400 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_call429 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_call431 = new BitSet(new long[]{0x0007FFFFFFFFFFF0L});
    public static final BitSet FOLLOW_ID_in_idref463 = new BitSet(new long[]{0x0000000000000002L});

}