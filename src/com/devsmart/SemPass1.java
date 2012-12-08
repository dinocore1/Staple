// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g 2012-08-29 00:04:53

package com.devsmart;

import com.devsmart.symbol.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SemPass1 extends TreeFilter {
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


    public SemPass1(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public SemPass1(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SemPass1.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/SemPass1.g"; }


        Scope currentScope;
        ErrorStream errorstream;
        
        public SemPass1(TreeNodeStream input, Scope globalScope, ErrorStream estream) {
            this(input);
            currentScope = globalScope;
            errorstream = estream;
        }



    // $ANTLR start "topdown"
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:25:1: topdown : enterClass ;
    public final void topdown() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:26:2: ( enterClass )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:26:4: enterClass
            {
            pushFollow(FOLLOW_enterClass_in_topdown53);
            enterClass();

            state._fsp--;
            if (state.failed) return ;

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
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:29:1: bottomup : exitClass ;
    public final void bottomup() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:30:2: ( exitClass )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:30:4: exitClass
            {
            pushFollow(FOLLOW_exitClass_in_bottomup67);
            exitClass();

            state._fsp--;
            if (state.failed) return ;

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
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:33:1: enterClass : ^( CLASS cname= ID subclass= ID ( . )* ) ;
    public final void enterClass() throws RecognitionException {
        StapleTree cname=null;
        StapleTree subclass=null;
        StapleTree CLASS1=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:34:2: ( ^( CLASS cname= ID subclass= ID ( . )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:34:4: ^( CLASS cname= ID subclass= ID ( . )* )
            {
            CLASS1=(StapleTree)match(input,CLASS,FOLLOW_CLASS_in_enterClass86); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            cname=(StapleTree)match(input,ID,FOLLOW_ID_in_enterClass90); if (state.failed) return ;

            subclass=(StapleTree)match(input,ID,FOLLOW_ID_in_enterClass94); if (state.failed) return ;

            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:34:33: ( . )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==UP) ) {
                    alt1=2;
                }
                else if ( ((LA1_0 >= ARGS && LA1_0 <= 56)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:34:33: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;


            if ( state.backtracking==1 ) {
            		ClassSymbol newclass = new ClassSymbol((cname!=null?cname.getText():null));
            		CLASS1.symbol = newclass;
            		
            		currentScope.define(newclass);
            		currentScope = currentScope.push();
            		
            		newclass.scope = currentScope;
            		
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
    // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:47:1: exitClass : CLASS ;
    public final void exitClass() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:48:2: ( CLASS )
            // /home/paul/workspace/Staple/src/com/devsmart/SemPass1.g:48:4: CLASS
            {
            match(input,CLASS,FOLLOW_CLASS_in_exitClass114); if (state.failed) return ;

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
    // $ANTLR end "exitClass"

    // Delegated rules


 

    public static final BitSet FOLLOW_enterClass_in_topdown53 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitClass_in_bottomup67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_enterClass86 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_enterClass90 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_enterClass94 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_CLASS_in_exitClass114 = new BitSet(new long[]{0x0000000000000002L});

}