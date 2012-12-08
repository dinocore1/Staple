// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/Staple.g 2012-08-29 00:04:51

package com.devsmart;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class StapleLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public StapleLexer() {} 
    public StapleLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public StapleLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/Staple.g"; }

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:6:7: ( '!' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:6:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:7:7: ( '!=' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:7:9: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:8:7: ( '(' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:8:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:9:7: ( ')' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:9:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:10:7: ( '*' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:10:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:11:7: ( '+' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:11:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:12:7: ( ',' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:12:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:13:7: ( '-' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:13:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:14:7: ( '.' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:14:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:15:7: ( '/' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:15:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:16:7: ( ';' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:16:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:17:7: ( '<' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:17:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:18:7: ( '<=' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:18:9: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:19:7: ( '=' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:19:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:7: ( '==' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:9: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:21:7: ( '>' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:21:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:22:7: ( '>=' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:22:9: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:23:7: ( 'bool' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:23:9: 'bool'
            {
            match("bool"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:24:7: ( 'class' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:24:9: 'class'
            {
            match("class"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:25:7: ( 'extends' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:25:9: 'extends'
            {
            match("extends"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:26:7: ( 'import' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:26:9: 'import'
            {
            match("import"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:27:7: ( 'int' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:27:9: 'int'
            {
            match("int"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:7: ( 'new' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:9: 'new'
            {
            match("new"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:29:7: ( 'package' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:29:9: 'package'
            {
            match("package"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:30:7: ( 'this' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:30:9: 'this'
            {
            match("this"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__53"

    // $ANTLR start "T__54"
    public final void mT__54() throws RecognitionException {
        try {
            int _type = T__54;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:31:7: ( 'void' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:31:9: 'void'
            {
            match("void"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__54"

    // $ANTLR start "T__55"
    public final void mT__55() throws RecognitionException {
        try {
            int _type = T__55;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:32:7: ( '{' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:32:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__55"

    // $ANTLR start "T__56"
    public final void mT__56() throws RecognitionException {
        try {
            int _type = T__56;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:33:7: ( '}' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:33:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__56"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:141:5: ( '\"' ( EscapeSequence |~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:141:8: '\"' ( EscapeSequence |~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:141:12: ( EscapeSequence |~ ( '\\\\' | '\"' ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\\') ) {
                    alt1=1;
                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '\uFFFF')) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:141:14: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:141:31: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            int _type = EscapeSequence;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:145:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:145:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
            {
            match('\\'); 

            if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:149:2: ( LETTER ( LETTER | '0' .. '9' )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:149:4: LETTER ( LETTER | '0' .. '9' )*
            {
            mLETTER(); 


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:149:11: ( LETTER | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:155:2: ( 'A' .. 'Z' | 'a' .. 'z' | '_' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:159:5: ( '1' .. '9' ( '0' .. '9' )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:159:7: '1' .. '9' ( '0' .. '9' )*
            {
            matchRange('1','9'); 

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:159:16: ( '0' .. '9' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:162:4: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:162:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:166:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:166:7: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:166:12: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='*') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1=='/') ) {
                        alt4=2;
                    }
                    else if ( ((LA4_1 >= '\u0000' && LA4_1 <= '.')||(LA4_1 >= '0' && LA4_1 <= '\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0 >= '\u0000' && LA4_0 <= ')')||(LA4_0 >= '+' && LA4_0 <= '\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:166:40: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:170:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:170:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 



            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:170:12: (~ ( '\\n' | '\\r' ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= '\u0000' && LA5_0 <= '\t')||(LA5_0 >= '\u000B' && LA5_0 <= '\f')||(LA5_0 >= '\u000E' && LA5_0 <= '\uFFFF')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:170:26: ( '\\r' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\r') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:170:26: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINE_COMMENT"

    public void mTokens() throws RecognitionException {
        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:8: ( T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | StringLiteral | EscapeSequence | ID | INT | WS | COMMENT | LINE_COMMENT )
        int alt7=35;
        alt7 = dfa7.predict(input);
        switch (alt7) {
            case 1 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:10: T__29
                {
                mT__29(); 


                }
                break;
            case 2 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:16: T__30
                {
                mT__30(); 


                }
                break;
            case 3 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:22: T__31
                {
                mT__31(); 


                }
                break;
            case 4 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:28: T__32
                {
                mT__32(); 


                }
                break;
            case 5 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:34: T__33
                {
                mT__33(); 


                }
                break;
            case 6 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:40: T__34
                {
                mT__34(); 


                }
                break;
            case 7 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:46: T__35
                {
                mT__35(); 


                }
                break;
            case 8 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:52: T__36
                {
                mT__36(); 


                }
                break;
            case 9 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:58: T__37
                {
                mT__37(); 


                }
                break;
            case 10 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:64: T__38
                {
                mT__38(); 


                }
                break;
            case 11 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:70: T__39
                {
                mT__39(); 


                }
                break;
            case 12 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:76: T__40
                {
                mT__40(); 


                }
                break;
            case 13 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:82: T__41
                {
                mT__41(); 


                }
                break;
            case 14 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:88: T__42
                {
                mT__42(); 


                }
                break;
            case 15 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:94: T__43
                {
                mT__43(); 


                }
                break;
            case 16 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:100: T__44
                {
                mT__44(); 


                }
                break;
            case 17 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:106: T__45
                {
                mT__45(); 


                }
                break;
            case 18 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:112: T__46
                {
                mT__46(); 


                }
                break;
            case 19 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:118: T__47
                {
                mT__47(); 


                }
                break;
            case 20 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:124: T__48
                {
                mT__48(); 


                }
                break;
            case 21 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:130: T__49
                {
                mT__49(); 


                }
                break;
            case 22 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:136: T__50
                {
                mT__50(); 


                }
                break;
            case 23 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:142: T__51
                {
                mT__51(); 


                }
                break;
            case 24 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:148: T__52
                {
                mT__52(); 


                }
                break;
            case 25 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:154: T__53
                {
                mT__53(); 


                }
                break;
            case 26 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:160: T__54
                {
                mT__54(); 


                }
                break;
            case 27 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:166: T__55
                {
                mT__55(); 


                }
                break;
            case 28 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:172: T__56
                {
                mT__56(); 


                }
                break;
            case 29 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:178: StringLiteral
                {
                mStringLiteral(); 


                }
                break;
            case 30 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:192: EscapeSequence
                {
                mEscapeSequence(); 


                }
                break;
            case 31 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:207: ID
                {
                mID(); 


                }
                break;
            case 32 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:210: INT
                {
                mINT(); 


                }
                break;
            case 33 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:214: WS
                {
                mWS(); 


                }
                break;
            case 34 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:217: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 35 :
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:1:225: LINE_COMMENT
                {
                mLINE_COMMENT(); 


                }
                break;

        }

    }


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\1\uffff\1\36\7\uffff\1\41\1\uffff\1\43\1\45\1\47\10\32\22\uffff"+
        "\15\32\1\76\1\77\3\32\1\103\3\32\2\uffff\1\32\1\110\1\111\1\uffff"+
        "\1\112\3\32\3\uffff\1\32\1\117\1\32\1\121\1\uffff\1\122\2\uffff";
    static final String DFA7_eofS =
        "\123\uffff";
    static final String DFA7_minS =
        "\1\11\1\75\7\uffff\1\52\1\uffff\3\75\1\157\1\154\1\170\1\155\1\145"+
        "\1\141\1\150\1\157\22\uffff\1\157\1\141\1\164\1\160\1\164\1\167"+
        "\1\143\2\151\1\154\1\163\1\145\1\157\2\60\1\153\1\163\1\144\1\60"+
        "\1\163\1\156\1\162\2\uffff\1\141\2\60\1\uffff\1\60\1\144\1\164\1"+
        "\147\3\uffff\1\163\1\60\1\145\1\60\1\uffff\1\60\2\uffff";
    static final String DFA7_maxS =
        "\1\175\1\75\7\uffff\1\57\1\uffff\3\75\1\157\1\154\1\170\1\156\1"+
        "\145\1\141\1\150\1\157\22\uffff\1\157\1\141\1\164\1\160\1\164\1"+
        "\167\1\143\2\151\1\154\1\163\1\145\1\157\2\172\1\153\1\163\1\144"+
        "\1\172\1\163\1\156\1\162\2\uffff\1\141\2\172\1\uffff\1\172\1\144"+
        "\1\164\1\147\3\uffff\1\163\1\172\1\145\1\172\1\uffff\1\172\2\uffff";
    static final String DFA7_acceptS =
        "\2\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\uffff\1\13\13\uffff\1\33"+
        "\1\34\1\35\1\36\1\37\1\40\1\41\1\2\1\1\1\42\1\43\1\12\1\15\1\14"+
        "\1\17\1\16\1\21\1\20\26\uffff\1\26\1\27\3\uffff\1\22\4\uffff\1\31"+
        "\1\32\1\23\4\uffff\1\25\1\uffff\1\24\1\30";
    static final String DFA7_specialS =
        "\123\uffff}>";
    static final String[] DFA7_transitionS = {
            "\2\34\1\uffff\2\34\22\uffff\1\34\1\1\1\30\5\uffff\1\2\1\3\1"+
            "\4\1\5\1\6\1\7\1\10\1\11\1\uffff\11\33\1\uffff\1\12\1\13\1\14"+
            "\1\15\2\uffff\32\32\1\uffff\1\31\2\uffff\1\32\1\uffff\1\32\1"+
            "\16\1\17\1\32\1\20\3\32\1\21\4\32\1\22\1\32\1\23\3\32\1\24\1"+
            "\32\1\25\4\32\1\26\1\uffff\1\27",
            "\1\35",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\37\4\uffff\1\40",
            "",
            "\1\42",
            "\1\44",
            "\1\46",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\1\100",
            "\1\101",
            "\1\102",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\1\104",
            "\1\105",
            "\1\106",
            "",
            "",
            "\1\107",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\1\113",
            "\1\114",
            "\1\115",
            "",
            "",
            "",
            "\1\116",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\1\120",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | StringLiteral | EscapeSequence | ID | INT | WS | COMMENT | LINE_COMMENT );";
        }
    }
 

}