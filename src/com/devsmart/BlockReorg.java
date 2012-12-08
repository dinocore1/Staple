// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g 2012-08-04 15:36:50

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
public class BlockReorg extends TreeFilter {
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


    public BlockReorg(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public BlockReorg(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return BlockReorg.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g"; }


       
        ErrorStream errorstream;
        Scope currentScope;
        
        public BlockReorg(TreeNodeStream input, ErrorStream estream) {
            this(input);
            errorstream = estream;
        }



    // $ANTLR start "topdown"
    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:26:1: topdown : enterBlock ;
    public final void topdown() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:27:2: ( enterBlock )
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:27:4: enterBlock
            {
            pushFollow(FOLLOW_enterBlock_in_topdown53);
            enterBlock();

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
    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:30:1: bottomup : exitBlock ;
    public final void bottomup() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:31:2: ( exitBlock )
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:31:4: exitBlock
            {
            pushFollow(FOLLOW_exitBlock_in_bottomup65);
            exitBlock();

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



    // $ANTLR start "enterBlock"
    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:34:1: enterBlock : ^( BLOCK ( variableDefinition |o= . )* ) ;
    public final void enterBlock() throws RecognitionException {
        StapleTree o=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:35:2: ( ^( BLOCK ( variableDefinition |o= . )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:35:4: ^( BLOCK ( variableDefinition |o= . )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_enterBlock78); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:35:12: ( variableDefinition |o= . )*
                loop1:
                do {
                    int alt1=3;
                    alt1 = dfa1.predict(input);
                    switch (alt1) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:35:13: variableDefinition
                	    {
                	    pushFollow(FOLLOW_variableDefinition_in_enterBlock81);
                	    variableDefinition();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;
                	case 2 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:35:34: o= .
                	    {
                	    o=(StapleTree)input.LT(1);

                	    matchAny(input); if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop1;
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



    // $ANTLR start "exitBlock"
    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:38:1: exitBlock : BLOCK ;
    public final void exitBlock() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:39:2: ( BLOCK )
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:39:4: BLOCK
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_exitBlock105); if (state.failed) return ;

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



    // $ANTLR start "variableDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:42:1: variableDefinition : ^( VARDEF ( . )* ) ;
    public final void variableDefinition() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:43:2: ( ^( VARDEF ( . )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:43:4: ^( VARDEF ( . )* )
            {
            match(input,VARDEF,FOLLOW_VARDEF_in_variableDefinition118); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:43:13: ( . )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==UP) ) {
                        alt2=2;
                    }
                    else if ( ((LA2_0 >= ARGS && LA2_0 <= 56)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // /home/paul/workspace/Staple/src/com/devsmart/BlockReorg.g:43:13: .
                	    {
                	    matchAny(input); if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop2;
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
    // $ANTLR end "variableDefinition"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\10\uffff";
    static final String DFA1_eofS =
        "\10\uffff";
    static final String DFA1_minS =
        "\1\3\1\uffff\1\2\1\uffff\1\3\1\2\2\uffff";
    static final String DFA1_maxS =
        "\1\70\1\uffff\1\70\1\uffff\2\70\2\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\uffff\1\2\2\uffff\2\1";
    static final String DFA1_specialS =
        "\10\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1\27\3\1\2\35\3",
            "",
            "\1\4\66\3",
            "",
            "\1\6\65\5",
            "\1\6\1\7\65\5",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()* loopback of 35:12: ( variableDefinition |o= . )*";
        }
    }
 

    public static final BitSet FOLLOW_enterBlock_in_topdown53 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exitBlock_in_bottomup65 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_enterBlock78 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_variableDefinition_in_enterBlock81 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_BLOCK_in_exitBlock105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARDEF_in_variableDefinition118 = new BitSet(new long[]{0x0000000000000004L});

}