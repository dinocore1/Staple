// $ANTLR 3.4 DefRef.g 2011-09-17 17:20:47

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARG", "ARGS", "ARRAY", "ASSIGN", "BLOCK", "CALL", "COMMENT", "ELIST", "EXPR", "EXTERNFUNC", "EXTERNVAR", "FILE", "FUNCDEF", "ID", "INDEX", "INT", "LETTER", "LINE_COMMENT", "STRING", "VARDEF", "WS", "'!='", "'('", "')'", "'*'", "'+'", "','", "'-'", "'/'", "';'", "'<'", "'<='", "'=='", "'>'", "'>='", "'['", "']'", "'else'", "'if'", "'int'", "'return'", "'void'", "'while'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__25=25;
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
    public static final int ARG=4;
    public static final int ARGS=5;
    public static final int ARRAY=6;
    public static final int ASSIGN=7;
    public static final int BLOCK=8;
    public static final int CALL=9;
    public static final int COMMENT=10;
    public static final int ELIST=11;
    public static final int EXPR=12;
    public static final int EXTERNFUNC=13;
    public static final int EXTERNVAR=14;
    public static final int FILE=15;
    public static final int FUNCDEF=16;
    public static final int ID=17;
    public static final int INDEX=18;
    public static final int INT=19;
    public static final int LETTER=20;
    public static final int LINE_COMMENT=21;
    public static final int STRING=22;
    public static final int VARDEF=23;
    public static final int WS=24;

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
    // DefRef.g:17:1: topdown : ( enterBlock | enterFunction | varDeclaration );
    public final void topdown() throws RecognitionException {
        try {
            // DefRef.g:18:5: ( enterBlock | enterFunction | varDeclaration )
            int alt1=3;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt1=1;
                }
                break;
            case EXTERNFUNC:
            case FUNCDEF:
                {
                alt1=2;
                }
                break;
            case ARG:
            case EXTERNVAR:
            case VARDEF:
                {
                alt1=3;
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
                    // DefRef.g:18:9: enterBlock
                    {
                    pushFollow(FOLLOW_enterBlock_in_topdown54);
                    enterBlock();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // DefRef.g:19:9: enterFunction
                    {
                    pushFollow(FOLLOW_enterFunction_in_topdown64);
                    enterFunction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // DefRef.g:20:9: varDeclaration
                    {
                    pushFollow(FOLLOW_varDeclaration_in_topdown74);
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
    // DefRef.g:23:1: bottomup : ( exitBlock | exitFunction | idref | call );
    public final void bottomup() throws RecognitionException {
        try {
            // DefRef.g:24:5: ( exitBlock | exitFunction | idref | call )
            int alt2=4;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt2=1;
                }
                break;
            case EXTERNFUNC:
            case FUNCDEF:
                {
                alt2=2;
                }
                break;
            case ID:
                {
                alt2=3;
                }
                break;
            case CALL:
                {
                alt2=4;
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
                    // DefRef.g:24:9: exitBlock
                    {
                    pushFollow(FOLLOW_exitBlock_in_bottomup93);
                    exitBlock();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // DefRef.g:25:9: exitFunction
                    {
                    pushFollow(FOLLOW_exitFunction_in_bottomup103);
                    exitFunction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // DefRef.g:26:9: idref
                    {
                    pushFollow(FOLLOW_idref_in_bottomup113);
                    idref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // DefRef.g:27:7: call
                    {
                    pushFollow(FOLLOW_call_in_bottomup121);
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



    // $ANTLR start "enterBlock"
    // DefRef.g:32:1: enterBlock : BLOCK ;
    public final void enterBlock() throws RecognitionException {
        try {
            // DefRef.g:33:5: ( BLOCK )
            // DefRef.g:33:9: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_enterBlock142); if (state.failed) return ;

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
    // DefRef.g:35:1: exitBlock : BLOCK ;
    public final void exitBlock() throws RecognitionException {
        try {
            // DefRef.g:36:5: ( BLOCK )
            // DefRef.g:36:9: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_exitBlock162); if (state.failed) return ;

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
    // DefRef.g:43:1: enterFunction : ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* ) ;
    public final void enterFunction() throws RecognitionException {
        CTree ID1=null;
        Type type_tree2 =null;


        try {
            // DefRef.g:44:5: ( ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* ) )
            // DefRef.g:44:9: ^( ( FUNCDEF | EXTERNFUNC ) ID type_tree ( . )* )
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
            ID1=(CTree)match(input,ID,FOLLOW_ID_in_enterFunction199); if (state.failed) return ;

            pushFollow(FOLLOW_type_tree_in_enterFunction201);
            type_tree2=type_tree();

            state._fsp--;
            if (state.failed) return ;

            // DefRef.g:44:45: ( . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= ARG && LA3_0 <= 48)) ) {
                    alt3=1;
                }
                else if ( (LA3_0==UP) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // DefRef.g:44:45: .
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
                    // System.out.println("line "+ID1.getLine()+": def method "+(ID1!=null?ID1.getText():null));
                    Type retType = type_tree2; // rule type returns a Type symbol
            		FunctionSymbol fs = new FunctionSymbol((ID1!=null?ID1.getText():null),type_tree2,currentScope);
            		ID1.symbol = fs;
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
    // DefRef.g:54:1: exitFunction : ( FUNCDEF | EXTERNFUNC ) ;
    public final void exitFunction() throws RecognitionException {
        try {
            // DefRef.g:55:5: ( ( FUNCDEF | EXTERNFUNC ) )
            // DefRef.g:55:9: ( FUNCDEF | EXTERNFUNC )
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
    // DefRef.g:62:1: type_tree returns [Type type] : ( ^( ARRAY t= type_tree e= . ) | type_specifier );
    public final Type type_tree() throws RecognitionException {
        Type type = null;


        CTree e=null;
        Type t =null;

        DefRef.type_specifier_return type_specifier3 =null;


        try {
            // DefRef.g:63:2: ( ^( ARRAY t= type_tree e= . ) | type_specifier )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ARRAY) ) {
                alt4=1;
            }
            else if ( (LA4_0==43||LA4_0==45) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return type;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // DefRef.g:63:4: ^( ARRAY t= type_tree e= . )
                    {
                    match(input,ARRAY,FOLLOW_ARRAY_in_type_tree267); if (state.failed) return type;

                    match(input, Token.DOWN, null); if (state.failed) return type;
                    pushFollow(FOLLOW_type_tree_in_type_tree271);
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
                    // DefRef.g:64:4: type_specifier
                    {
                    pushFollow(FOLLOW_type_specifier_in_type_tree283);
                    type_specifier3=type_specifier();

                    state._fsp--;
                    if (state.failed) return type;

                    if ( state.backtracking==1 ) {type = (type_specifier3!=null?type_specifier3.type:null);}

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
    // DefRef.g:67:1: type_specifier returns [Type type] : ( 'void' | 'int' );
    public final DefRef.type_specifier_return type_specifier() throws RecognitionException {
        DefRef.type_specifier_return retval = new DefRef.type_specifier_return();
        retval.start = input.LT(1);


         retval.type = (Type)currentScope.resolve(((CTree)retval.start).getText()); 
        try {
            // DefRef.g:69:2: ( 'void' | 'int' )
            // DefRef.g:
            {
            if ( input.LA(1)==43||input.LA(1)==45 ) {
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
    // DefRef.g:75:1: varDeclaration : ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree ) ;
    public final void varDeclaration() throws RecognitionException {
        CTree ID4=null;
        Type type_tree5 =null;


        try {
            // DefRef.g:76:5: ( ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree ) )
            // DefRef.g:76:9: ^( ( VARDEF | EXTERNVAR | ARG ) ID type_tree )
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
            ID4=(CTree)match(input,ID,FOLLOW_ID_in_varDeclaration343); if (state.failed) return ;

            pushFollow(FOLLOW_type_tree_in_varDeclaration345);
            type_tree5=type_tree();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
                    // System.out.println("line "+ID4.getLine()+": def "+(ID4!=null?ID4.getText():null)+" type "+type_tree5);
                    VariableSymbol vs = new VariableSymbol((ID4!=null?ID4.getText():null),type_tree5);
                    currentScope.define(vs);
                    ID4.symbol = vs;
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
    // DefRef.g:87:1: call : ^( CALL ID . ) ;
    public final void call() throws RecognitionException {
        CTree ID6=null;

        try {
            // DefRef.g:87:5: ( ^( CALL ID . ) )
            // DefRef.g:87:9: ^( CALL ID . )
            {
            match(input,CALL,FOLLOW_CALL_in_call374); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID6=(CTree)match(input,ID,FOLLOW_ID_in_call376); if (state.failed) return ;

            matchAny(input); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
                    Symbol s = currentScope.resolve((ID6!=null?ID6.getText():null));
                    ID6.symbol = s;
                    // System.out.println("line "+ID6.getLine()+": call "+s);
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
    // DefRef.g:95:1: idref :{...}? ID ;
    public final DefRef.idref_return idref() throws RecognitionException {
        DefRef.idref_return retval = new DefRef.idref_return();
        retval.start = input.LT(1);


        CTree ID7=null;

        try {
            // DefRef.g:96:5: ({...}? ID )
            // DefRef.g:96:9: {...}? ID
            {
            if ( !((((CTree)retval.start).hasAncestor(EXPR) || ((CTree)retval.start).hasAncestor(ASSIGN) ||
                	 ((CTree)retval.start).hasAncestor(ELIST))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "idref", "$start.hasAncestor(EXPR) || $start.hasAncestor(ASSIGN) ||\n    \t $start.hasAncestor(ELIST)");
            }

            ID7=(CTree)match(input,ID,FOLLOW_ID_in_idref408); if (state.failed) return retval;

            if ( state.backtracking==1 ) {
                    Symbol s = currentScope.resolve((ID7!=null?ID7.getText():null));
                    ID7.symbol = s;
                    // System.out.println("line "+ID7.getLine()+": ref "+s);
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


 

    public static final BitSet FOLLOW_enterBlock_in_topdown54 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enterFunction_in_topdown64 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varDeclaration_in_topdown74 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitBlock_in_bottomup93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitFunction_in_bottomup103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_idref_in_bottomup113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_bottomup121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_enterBlock142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_exitBlock162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_enterFunction193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterFunction199 = new BitSet(new long[]{0x0000280000000040L});
    public static final BitSet FOLLOW_type_tree_in_enterFunction201 = new BitSet(new long[]{0x0001FFFFFFFFFFF8L});
    public static final BitSet FOLLOW_set_in_exitFunction234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_in_type_tree267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_tree_in_type_tree271 = new BitSet(new long[]{0x0001FFFFFFFFFFF0L});
    public static final BitSet FOLLOW_type_specifier_in_type_tree283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_varDeclaration335 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_varDeclaration343 = new BitSet(new long[]{0x0000280000000040L});
    public static final BitSet FOLLOW_type_tree_in_varDeclaration345 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_call374 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_call376 = new BitSet(new long[]{0x0001FFFFFFFFFFF0L});
    public static final BitSet FOLLOW_ID_in_idref408 = new BitSet(new long[]{0x0000000000000002L});

}