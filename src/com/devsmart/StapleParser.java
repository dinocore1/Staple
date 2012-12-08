// $ANTLR 3.4 /home/paul/workspace/Staple/src/com/devsmart/Staple.g 2012-08-29 00:04:51

package com.devsmart;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class StapleParser extends Parser {
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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public StapleParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public StapleParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return StapleParser.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/workspace/Staple/src/com/devsmart/Staple.g"; }


    public static class compilationUnit_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "compilationUnit"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:19:1: compilationUnit : ( packageDefinition )? ( importDefinition )* classDefinition -> ^( UNIT ( packageDefinition )? ( importDefinition )* classDefinition ) ;
    public final StapleParser.compilationUnit_return compilationUnit() throws RecognitionException {
        StapleParser.compilationUnit_return retval = new StapleParser.compilationUnit_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleParser.packageDefinition_return packageDefinition1 =null;

        StapleParser.importDefinition_return importDefinition2 =null;

        StapleParser.classDefinition_return classDefinition3 =null;


        RewriteRuleSubtreeStream stream_importDefinition=new RewriteRuleSubtreeStream(adaptor,"rule importDefinition");
        RewriteRuleSubtreeStream stream_classDefinition=new RewriteRuleSubtreeStream(adaptor,"rule classDefinition");
        RewriteRuleSubtreeStream stream_packageDefinition=new RewriteRuleSubtreeStream(adaptor,"rule packageDefinition");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:2: ( ( packageDefinition )? ( importDefinition )* classDefinition -> ^( UNIT ( packageDefinition )? ( importDefinition )* classDefinition ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:4: ( packageDefinition )? ( importDefinition )* classDefinition
            {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:4: ( packageDefinition )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==52) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:4: packageDefinition
                    {
                    pushFollow(FOLLOW_packageDefinition_in_compilationUnit106);
                    packageDefinition1=packageDefinition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_packageDefinition.add(packageDefinition1.getTree());

                    }
                    break;

            }


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:23: ( importDefinition )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==49) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:23: importDefinition
            	    {
            	    pushFollow(FOLLOW_importDefinition_in_compilationUnit109);
            	    importDefinition2=importDefinition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_importDefinition.add(importDefinition2.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            pushFollow(FOLLOW_classDefinition_in_compilationUnit112);
            classDefinition3=classDefinition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_classDefinition.add(classDefinition3.getTree());

            // AST REWRITE
            // elements: classDefinition, packageDefinition, importDefinition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 20:57: -> ^( UNIT ( packageDefinition )? ( importDefinition )* classDefinition )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:60: ^( UNIT ( packageDefinition )? ( importDefinition )* classDefinition )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(UNIT, "UNIT")
                , root_1);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:67: ( packageDefinition )?
                if ( stream_packageDefinition.hasNext() ) {
                    adaptor.addChild(root_1, stream_packageDefinition.nextTree());

                }
                stream_packageDefinition.reset();

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:20:86: ( importDefinition )*
                while ( stream_importDefinition.hasNext() ) {
                    adaptor.addChild(root_1, stream_importDefinition.nextTree());

                }
                stream_importDefinition.reset();

                adaptor.addChild(root_1, stream_classDefinition.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "compilationUnit"


    public static class packageDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "packageDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:23:1: packageDefinition : 'package' classname ';' -> ^( PACKAGE classname ) ;
    public final StapleParser.packageDefinition_return packageDefinition() throws RecognitionException {
        StapleParser.packageDefinition_return retval = new StapleParser.packageDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token string_literal4=null;
        Token char_literal6=null;
        StapleParser.classname_return classname5 =null;


        StapleTree string_literal4_tree=null;
        StapleTree char_literal6_tree=null;
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleSubtreeStream stream_classname=new RewriteRuleSubtreeStream(adaptor,"rule classname");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:24:2: ( 'package' classname ';' -> ^( PACKAGE classname ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:24:4: 'package' classname ';'
            {
            string_literal4=(Token)match(input,52,FOLLOW_52_in_packageDefinition138); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_52.add(string_literal4);


            pushFollow(FOLLOW_classname_in_packageDefinition140);
            classname5=classname();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_classname.add(classname5.getTree());

            char_literal6=(Token)match(input,39,FOLLOW_39_in_packageDefinition142); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_39.add(char_literal6);


            // AST REWRITE
            // elements: classname
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 24:28: -> ^( PACKAGE classname )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:24:31: ^( PACKAGE classname )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(PACKAGE, "PACKAGE")
                , root_1);

                adaptor.addChild(root_1, stream_classname.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "packageDefinition"


    public static class classname_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classname"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:27:1: classname : ID ( '.' ID )* -> ( ID )+ ;
    public final StapleParser.classname_return classname() throws RecognitionException {
        StapleParser.classname_return retval = new StapleParser.classname_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token ID7=null;
        Token char_literal8=null;
        Token ID9=null;

        StapleTree ID7_tree=null;
        StapleTree char_literal8_tree=null;
        StapleTree ID9_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:2: ( ID ( '.' ID )* -> ( ID )+ )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:4: ID ( '.' ID )*
            {
            ID7=(Token)match(input,ID,FOLLOW_ID_in_classname162); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID7);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:7: ( '.' ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==37) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:28:8: '.' ID
            	    {
            	    char_literal8=(Token)match(input,37,FOLLOW_37_in_classname165); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_37.add(char_literal8);


            	    ID9=(Token)match(input,ID,FOLLOW_ID_in_classname167); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID9);


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            // AST REWRITE
            // elements: ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 28:17: -> ( ID )+
            {
                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_0, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "classname"


    public static class importDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "importDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:31:1: importDefinition : 'import' classname ';' -> ^( IMPORT classname ) ;
    public final StapleParser.importDefinition_return importDefinition() throws RecognitionException {
        StapleParser.importDefinition_return retval = new StapleParser.importDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token string_literal10=null;
        Token char_literal12=null;
        StapleParser.classname_return classname11 =null;


        StapleTree string_literal10_tree=null;
        StapleTree char_literal12_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleSubtreeStream stream_classname=new RewriteRuleSubtreeStream(adaptor,"rule classname");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:32:2: ( 'import' classname ';' -> ^( IMPORT classname ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:32:4: 'import' classname ';'
            {
            string_literal10=(Token)match(input,49,FOLLOW_49_in_importDefinition185); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_49.add(string_literal10);


            pushFollow(FOLLOW_classname_in_importDefinition187);
            classname11=classname();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_classname.add(classname11.getTree());

            char_literal12=(Token)match(input,39,FOLLOW_39_in_importDefinition189); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_39.add(char_literal12);


            // AST REWRITE
            // elements: classname
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 32:27: -> ^( IMPORT classname )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:32:30: ^( IMPORT classname )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(IMPORT, "IMPORT")
                , root_1);

                adaptor.addChild(root_1, stream_classname.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "importDefinition"


    public static class classDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:35:1: classDefinition : 'class' cname= ID ( 'extends' sup= ID )? '{' ( fieldDefinition | methodDefinition )* '}' -> {$sup!=null}? ^( CLASS $cname $sup ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) ) -> ^( CLASS $cname ID[\"staple.runtime.object\"] ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) ) ;
    public final StapleParser.classDefinition_return classDefinition() throws RecognitionException {
        StapleParser.classDefinition_return retval = new StapleParser.classDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token cname=null;
        Token sup=null;
        Token string_literal13=null;
        Token string_literal14=null;
        Token char_literal15=null;
        Token char_literal18=null;
        StapleParser.fieldDefinition_return fieldDefinition16 =null;

        StapleParser.methodDefinition_return methodDefinition17 =null;


        StapleTree cname_tree=null;
        StapleTree sup_tree=null;
        StapleTree string_literal13_tree=null;
        StapleTree string_literal14_tree=null;
        StapleTree char_literal15_tree=null;
        StapleTree char_literal18_tree=null;
        RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
        RewriteRuleTokenStream stream_47=new RewriteRuleTokenStream(adaptor,"token 47");
        RewriteRuleTokenStream stream_56=new RewriteRuleTokenStream(adaptor,"token 56");
        RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_methodDefinition=new RewriteRuleSubtreeStream(adaptor,"rule methodDefinition");
        RewriteRuleSubtreeStream stream_fieldDefinition=new RewriteRuleSubtreeStream(adaptor,"rule fieldDefinition");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:36:2: ( 'class' cname= ID ( 'extends' sup= ID )? '{' ( fieldDefinition | methodDefinition )* '}' -> {$sup!=null}? ^( CLASS $cname $sup ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) ) -> ^( CLASS $cname ID[\"staple.runtime.object\"] ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:36:4: 'class' cname= ID ( 'extends' sup= ID )? '{' ( fieldDefinition | methodDefinition )* '}'
            {
            string_literal13=(Token)match(input,47,FOLLOW_47_in_classDefinition209); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_47.add(string_literal13);


            cname=(Token)match(input,ID,FOLLOW_ID_in_classDefinition213); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(cname);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:37:3: ( 'extends' sup= ID )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==48) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:37:4: 'extends' sup= ID
                    {
                    string_literal14=(Token)match(input,48,FOLLOW_48_in_classDefinition218); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_48.add(string_literal14);


                    sup=(Token)match(input,ID,FOLLOW_ID_in_classDefinition222); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(sup);


                    }
                    break;

            }


            char_literal15=(Token)match(input,55,FOLLOW_55_in_classDefinition228); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_55.add(char_literal15);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:39:3: ( fieldDefinition | methodDefinition )*
            loop5:
            do {
                int alt5=3;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ID||LA5_0==46||LA5_0==50||LA5_0==54) ) {
                    int LA5_2 = input.LA(2);

                    if ( (LA5_2==ID) ) {
                        int LA5_3 = input.LA(3);

                        if ( (LA5_3==39) ) {
                            alt5=1;
                        }
                        else if ( (LA5_3==31) ) {
                            alt5=2;
                        }


                    }


                }


                switch (alt5) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:39:5: fieldDefinition
            	    {
            	    pushFollow(FOLLOW_fieldDefinition_in_classDefinition235);
            	    fieldDefinition16=fieldDefinition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_fieldDefinition.add(fieldDefinition16.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:40:5: methodDefinition
            	    {
            	    pushFollow(FOLLOW_methodDefinition_in_classDefinition241);
            	    methodDefinition17=methodDefinition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_methodDefinition.add(methodDefinition17.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            char_literal18=(Token)match(input,56,FOLLOW_56_in_classDefinition250); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_56.add(char_literal18);


            // AST REWRITE
            // elements: ID, methodDefinition, fieldDefinition, cname, cname, methodDefinition, fieldDefinition, sup
            // token labels: sup, cname
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleTokenStream stream_sup=new RewriteRuleTokenStream(adaptor,"token sup",sup);
            RewriteRuleTokenStream stream_cname=new RewriteRuleTokenStream(adaptor,"token cname",cname);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 43:3: -> {$sup!=null}? ^( CLASS $cname $sup ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) )
            if (sup!=null) {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:43:20: ^( CLASS $cname $sup ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(CLASS, "CLASS")
                , root_1);

                adaptor.addChild(root_1, stream_cname.nextNode());

                adaptor.addChild(root_1, stream_sup.nextNode());

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:43:40: ^( FIELDS ( fieldDefinition )* )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(FIELDS, "FIELDS")
                , root_2);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:43:49: ( fieldDefinition )*
                while ( stream_fieldDefinition.hasNext() ) {
                    adaptor.addChild(root_2, stream_fieldDefinition.nextTree());

                }
                stream_fieldDefinition.reset();

                adaptor.addChild(root_1, root_2);
                }

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:43:67: ^( METHODS ( methodDefinition )* )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(METHODS, "METHODS")
                , root_2);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:43:77: ( methodDefinition )*
                while ( stream_methodDefinition.hasNext() ) {
                    adaptor.addChild(root_2, stream_methodDefinition.nextTree());

                }
                stream_methodDefinition.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 44:3: -> ^( CLASS $cname ID[\"staple.runtime.object\"] ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:44:6: ^( CLASS $cname ID[\"staple.runtime.object\"] ^( FIELDS ( fieldDefinition )* ) ^( METHODS ( methodDefinition )* ) )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(CLASS, "CLASS")
                , root_1);

                adaptor.addChild(root_1, stream_cname.nextNode());

                adaptor.addChild(root_1, 
                (StapleTree)adaptor.create(ID, "staple.runtime.object")
                );

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:44:49: ^( FIELDS ( fieldDefinition )* )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(FIELDS, "FIELDS")
                , root_2);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:44:58: ( fieldDefinition )*
                while ( stream_fieldDefinition.hasNext() ) {
                    adaptor.addChild(root_2, stream_fieldDefinition.nextTree());

                }
                stream_fieldDefinition.reset();

                adaptor.addChild(root_1, root_2);
                }

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:44:76: ^( METHODS ( methodDefinition )* )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(METHODS, "METHODS")
                , root_2);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:44:86: ( methodDefinition )*
                while ( stream_methodDefinition.hasNext() ) {
                    adaptor.addChild(root_2, stream_methodDefinition.nextTree());

                }
                stream_methodDefinition.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "classDefinition"


    public static class fieldDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:48:1: fieldDefinition : typeDefinition ID ';' -> ^( typeDefinition ID ) ;
    public final StapleParser.fieldDefinition_return fieldDefinition() throws RecognitionException {
        StapleParser.fieldDefinition_return retval = new StapleParser.fieldDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token ID20=null;
        Token char_literal21=null;
        StapleParser.typeDefinition_return typeDefinition19 =null;


        StapleTree ID20_tree=null;
        StapleTree char_literal21_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleSubtreeStream stream_typeDefinition=new RewriteRuleSubtreeStream(adaptor,"rule typeDefinition");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:49:2: ( typeDefinition ID ';' -> ^( typeDefinition ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:49:4: typeDefinition ID ';'
            {
            pushFollow(FOLLOW_typeDefinition_in_fieldDefinition324);
            typeDefinition19=typeDefinition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeDefinition.add(typeDefinition19.getTree());

            ID20=(Token)match(input,ID,FOLLOW_ID_in_fieldDefinition326); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID20);


            char_literal21=(Token)match(input,39,FOLLOW_39_in_fieldDefinition328); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_39.add(char_literal21);


            // AST REWRITE
            // elements: ID, typeDefinition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 49:26: -> ^( typeDefinition ID )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:49:29: ^( typeDefinition ID )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(stream_typeDefinition.nextNode(), root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fieldDefinition"


    public static class methodDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:52:1: methodDefinition : typeDefinition ID '(' ( formalArgs )? ')' block -> ^( FUNCTION ID typeDefinition ^( FORMALARGS ( formalArgs )? ) block ) ;
    public final StapleParser.methodDefinition_return methodDefinition() throws RecognitionException {
        StapleParser.methodDefinition_return retval = new StapleParser.methodDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token ID23=null;
        Token char_literal24=null;
        Token char_literal26=null;
        StapleParser.typeDefinition_return typeDefinition22 =null;

        StapleParser.formalArgs_return formalArgs25 =null;

        StapleParser.block_return block27 =null;


        StapleTree ID23_tree=null;
        StapleTree char_literal24_tree=null;
        StapleTree char_literal26_tree=null;
        RewriteRuleTokenStream stream_32=new RewriteRuleTokenStream(adaptor,"token 32");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_typeDefinition=new RewriteRuleSubtreeStream(adaptor,"rule typeDefinition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_formalArgs=new RewriteRuleSubtreeStream(adaptor,"rule formalArgs");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:53:2: ( typeDefinition ID '(' ( formalArgs )? ')' block -> ^( FUNCTION ID typeDefinition ^( FORMALARGS ( formalArgs )? ) block ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:53:4: typeDefinition ID '(' ( formalArgs )? ')' block
            {
            pushFollow(FOLLOW_typeDefinition_in_methodDefinition348);
            typeDefinition22=typeDefinition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeDefinition.add(typeDefinition22.getTree());

            ID23=(Token)match(input,ID,FOLLOW_ID_in_methodDefinition350); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID23);


            char_literal24=(Token)match(input,31,FOLLOW_31_in_methodDefinition352); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_31.add(char_literal24);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:53:26: ( formalArgs )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ID||LA6_0==46||LA6_0==50||LA6_0==54) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:53:26: formalArgs
                    {
                    pushFollow(FOLLOW_formalArgs_in_methodDefinition354);
                    formalArgs25=formalArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_formalArgs.add(formalArgs25.getTree());

                    }
                    break;

            }


            char_literal26=(Token)match(input,32,FOLLOW_32_in_methodDefinition357); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_32.add(char_literal26);


            pushFollow(FOLLOW_block_in_methodDefinition359);
            block27=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_block.add(block27.getTree());

            // AST REWRITE
            // elements: typeDefinition, formalArgs, block, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 54:4: -> ^( FUNCTION ID typeDefinition ^( FORMALARGS ( formalArgs )? ) block )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:54:7: ^( FUNCTION ID typeDefinition ^( FORMALARGS ( formalArgs )? ) block )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(FUNCTION, "FUNCTION")
                , root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_1, stream_typeDefinition.nextTree());

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:54:36: ^( FORMALARGS ( formalArgs )? )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(FORMALARGS, "FORMALARGS")
                , root_2);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:54:49: ( formalArgs )?
                if ( stream_formalArgs.hasNext() ) {
                    adaptor.addChild(root_2, stream_formalArgs.nextTree());

                }
                stream_formalArgs.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_1, stream_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "methodDefinition"


    public static class formalArgs_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalArgs"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:57:1: formalArgs : formalArg ( ',' formalArg )* -> ( formalArg )+ ;
    public final StapleParser.formalArgs_return formalArgs() throws RecognitionException {
        StapleParser.formalArgs_return retval = new StapleParser.formalArgs_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal29=null;
        StapleParser.formalArg_return formalArg28 =null;

        StapleParser.formalArg_return formalArg30 =null;


        StapleTree char_literal29_tree=null;
        RewriteRuleTokenStream stream_35=new RewriteRuleTokenStream(adaptor,"token 35");
        RewriteRuleSubtreeStream stream_formalArg=new RewriteRuleSubtreeStream(adaptor,"rule formalArg");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:58:2: ( formalArg ( ',' formalArg )* -> ( formalArg )+ )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:58:4: formalArg ( ',' formalArg )*
            {
            pushFollow(FOLLOW_formalArg_in_formalArgs393);
            formalArg28=formalArg();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_formalArg.add(formalArg28.getTree());

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:58:14: ( ',' formalArg )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==35) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:58:15: ',' formalArg
            	    {
            	    char_literal29=(Token)match(input,35,FOLLOW_35_in_formalArgs396); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_35.add(char_literal29);


            	    pushFollow(FOLLOW_formalArg_in_formalArgs398);
            	    formalArg30=formalArg();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_formalArg.add(formalArg30.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            // AST REWRITE
            // elements: formalArg
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 58:32: -> ( formalArg )+
            {
                if ( !(stream_formalArg.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_formalArg.hasNext() ) {
                    adaptor.addChild(root_0, stream_formalArg.nextTree());

                }
                stream_formalArg.reset();

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "formalArgs"


    public static class formalArg_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalArg"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:61:1: formalArg : typeDefinition ID -> ^( typeDefinition ID ) ;
    public final StapleParser.formalArg_return formalArg() throws RecognitionException {
        StapleParser.formalArg_return retval = new StapleParser.formalArg_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token ID32=null;
        StapleParser.typeDefinition_return typeDefinition31 =null;


        StapleTree ID32_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_typeDefinition=new RewriteRuleSubtreeStream(adaptor,"rule typeDefinition");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:62:2: ( typeDefinition ID -> ^( typeDefinition ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:62:4: typeDefinition ID
            {
            pushFollow(FOLLOW_typeDefinition_in_formalArg418);
            typeDefinition31=typeDefinition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeDefinition.add(typeDefinition31.getTree());

            ID32=(Token)match(input,ID,FOLLOW_ID_in_formalArg420); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID32);


            // AST REWRITE
            // elements: typeDefinition, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 62:22: -> ^( typeDefinition ID )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:62:25: ^( typeDefinition ID )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(stream_typeDefinition.nextNode(), root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "formalArg"


    public static class typeDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:65:1: typeDefinition : ( 'void' | 'int' | 'bool' | ID );
    public final StapleParser.typeDefinition_return typeDefinition() throws RecognitionException {
        StapleParser.typeDefinition_return retval = new StapleParser.typeDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token set33=null;

        StapleTree set33_tree=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:66:2: ( 'void' | 'int' | 'bool' | ID )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:
            {
            root_0 = (StapleTree)adaptor.nil();


            set33=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==46||input.LA(1)==50||input.LA(1)==54 ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (StapleTree)adaptor.create(set33)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "typeDefinition"


    public static class variableDefinition_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableDefinition"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:72:1: variableDefinition : typeDefinition ID -> ^( VARDEF typeDefinition ID ) ;
    public final StapleParser.variableDefinition_return variableDefinition() throws RecognitionException {
        StapleParser.variableDefinition_return retval = new StapleParser.variableDefinition_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token ID35=null;
        StapleParser.typeDefinition_return typeDefinition34 =null;


        StapleTree ID35_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_typeDefinition=new RewriteRuleSubtreeStream(adaptor,"rule typeDefinition");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:73:2: ( typeDefinition ID -> ^( VARDEF typeDefinition ID ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:73:4: typeDefinition ID
            {
            pushFollow(FOLLOW_typeDefinition_in_variableDefinition468);
            typeDefinition34=typeDefinition();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeDefinition.add(typeDefinition34.getTree());

            ID35=(Token)match(input,ID,FOLLOW_ID_in_variableDefinition470); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID35);


            // AST REWRITE
            // elements: ID, typeDefinition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 73:22: -> ^( VARDEF typeDefinition ID )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:73:25: ^( VARDEF typeDefinition ID )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(VARDEF, "VARDEF")
                , root_1);

                adaptor.addChild(root_1, stream_typeDefinition.nextTree());

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "variableDefinition"


    public static class block_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:76:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final StapleParser.block_return block() throws RecognitionException {
        StapleParser.block_return retval = new StapleParser.block_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal36=null;
        Token char_literal38=null;
        StapleParser.statement_return statement37 =null;


        StapleTree char_literal36_tree=null;
        StapleTree char_literal38_tree=null;
        RewriteRuleTokenStream stream_56=new RewriteRuleTokenStream(adaptor,"token 56");
        RewriteRuleTokenStream stream_55=new RewriteRuleTokenStream(adaptor,"token 55");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:2: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:4: '{' ( statement )* '}'
            {
            char_literal36=(Token)match(input,55,FOLLOW_55_in_block492); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_55.add(char_literal36);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:8: ( statement )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ID||LA8_0==INT||LA8_0==StringLiteral||LA8_0==46||(LA8_0 >= 50 && LA8_0 <= 51)||(LA8_0 >= 53 && LA8_0 <= 55)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:8: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block494);
            	    statement37=statement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_statement.add(statement37.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            char_literal38=(Token)match(input,56,FOLLOW_56_in_block497); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_56.add(char_literal38);


            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 77:23: -> ^( BLOCK ( statement )* )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:26: ^( BLOCK ( statement )* )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:77:34: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class statement_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "statement"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:80:1: statement : ( block | statementExpression ';' -> statementExpression );
    public final StapleParser.statement_return statement() throws RecognitionException {
        StapleParser.statement_return retval = new StapleParser.statement_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal41=null;
        StapleParser.block_return block39 =null;

        StapleParser.statementExpression_return statementExpression40 =null;


        StapleTree char_literal41_tree=null;
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleSubtreeStream stream_statementExpression=new RewriteRuleSubtreeStream(adaptor,"rule statementExpression");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:81:2: ( block | statementExpression ';' -> statementExpression )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==55) ) {
                alt9=1;
            }
            else if ( (LA9_0==ID||LA9_0==INT||LA9_0==StringLiteral||LA9_0==46||(LA9_0 >= 50 && LA9_0 <= 51)||(LA9_0 >= 53 && LA9_0 <= 54)) ) {
                alt9=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:81:4: block
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_statement518);
                    block39=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block39.getTree());

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:82:4: statementExpression ';'
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement523);
                    statementExpression40=statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_statementExpression.add(statementExpression40.getTree());

                    char_literal41=(Token)match(input,39,FOLLOW_39_in_statement525); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_39.add(char_literal41);


                    // AST REWRITE
                    // elements: statementExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (StapleTree)adaptor.nil();
                    // 82:28: -> statementExpression
                    {
                        adaptor.addChild(root_0, stream_statementExpression.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "statement"


    public static class statementExpression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "statementExpression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:85:1: statementExpression : ( ( leftside '=' )=> assignment | leftside -> leftside );
    public final StapleParser.statementExpression_return statementExpression() throws RecognitionException {
        StapleParser.statementExpression_return retval = new StapleParser.statementExpression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleParser.assignment_return assignment42 =null;

        StapleParser.leftside_return leftside43 =null;


        RewriteRuleSubtreeStream stream_leftside=new RewriteRuleSubtreeStream(adaptor,"rule leftside");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:86:2: ( ( leftside '=' )=> assignment | leftside -> leftside )
            int alt10=2;
            switch ( input.LA(1) ) {
            case 53:
                {
                int LA10_1 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA10_2 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;

                }
                }
                break;
            case INT:
                {
                int LA10_3 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 3, input);

                    throw nvae;

                }
                }
                break;
            case StringLiteral:
                {
                int LA10_4 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 4, input);

                    throw nvae;

                }
                }
                break;
            case 51:
                {
                int LA10_5 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 5, input);

                    throw nvae;

                }
                }
                break;
            case 46:
            case 50:
            case 54:
                {
                int LA10_6 = input.LA(2);

                if ( (synpred1_Staple()) ) {
                    alt10=1;
                }
                else if ( (true) ) {
                    alt10=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 6, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:86:4: ( leftside '=' )=> assignment
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    pushFollow(FOLLOW_assignment_in_statementExpression551);
                    assignment42=assignment();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignment42.getTree());

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:87:4: leftside
                    {
                    pushFollow(FOLLOW_leftside_in_statementExpression556);
                    leftside43=leftside();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_leftside.add(leftside43.getTree());

                    // AST REWRITE
                    // elements: leftside
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (StapleTree)adaptor.nil();
                    // 87:13: -> leftside
                    {
                        adaptor.addChild(root_0, stream_leftside.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "statementExpression"


    public static class leftside_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "leftside"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:90:1: leftside : ( postfixExpression | variableDefinition );
    public final StapleParser.leftside_return leftside() throws RecognitionException {
        StapleParser.leftside_return retval = new StapleParser.leftside_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleParser.postfixExpression_return postfixExpression44 =null;

        StapleParser.variableDefinition_return variableDefinition45 =null;



        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:91:2: ( postfixExpression | variableDefinition )
            int alt11=2;
            switch ( input.LA(1) ) {
            case INT:
            case StringLiteral:
            case 51:
            case 53:
                {
                alt11=1;
                }
                break;
            case ID:
                {
                int LA11_2 = input.LA(2);

                if ( (LA11_2==37||LA11_2==39||LA11_2==42) ) {
                    alt11=1;
                }
                else if ( (LA11_2==ID) ) {
                    alt11=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 2, input);

                    throw nvae;

                }
                }
                break;
            case 46:
            case 50:
            case 54:
                {
                alt11=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:91:4: postfixExpression
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    pushFollow(FOLLOW_postfixExpression_in_leftside572);
                    postfixExpression44=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression44.getTree());

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:92:4: variableDefinition
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    pushFollow(FOLLOW_variableDefinition_in_leftside577);
                    variableDefinition45=variableDefinition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDefinition45.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "leftside"


    public static class assignment_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assignment"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:95:1: assignment : lvalue= leftside '=' rvalue= expression -> ^( ASSIGN $lvalue $rvalue) ;
    public final StapleParser.assignment_return assignment() throws RecognitionException {
        StapleParser.assignment_return retval = new StapleParser.assignment_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal46=null;
        StapleParser.leftside_return lvalue =null;

        StapleParser.expression_return rvalue =null;


        StapleTree char_literal46_tree=null;
        RewriteRuleTokenStream stream_42=new RewriteRuleTokenStream(adaptor,"token 42");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_leftside=new RewriteRuleSubtreeStream(adaptor,"rule leftside");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:96:2: (lvalue= leftside '=' rvalue= expression -> ^( ASSIGN $lvalue $rvalue) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:96:4: lvalue= leftside '=' rvalue= expression
            {
            pushFollow(FOLLOW_leftside_in_assignment590);
            lvalue=leftside();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_leftside.add(lvalue.getTree());

            char_literal46=(Token)match(input,42,FOLLOW_42_in_assignment592); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_42.add(char_literal46);


            pushFollow(FOLLOW_expression_in_assignment596);
            rvalue=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(rvalue.getTree());

            // AST REWRITE
            // elements: lvalue, rvalue
            // token labels: 
            // rule labels: retval, lvalue, rvalue
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_lvalue=new RewriteRuleSubtreeStream(adaptor,"rule lvalue",lvalue!=null?lvalue.tree:null);
            RewriteRuleSubtreeStream stream_rvalue=new RewriteRuleSubtreeStream(adaptor,"rule rvalue",rvalue!=null?rvalue.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 96:42: -> ^( ASSIGN $lvalue $rvalue)
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:96:45: ^( ASSIGN $lvalue $rvalue)
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(ASSIGN, "ASSIGN")
                , root_1);

                adaptor.addChild(root_1, stream_lvalue.nextTree());

                adaptor.addChild(root_1, stream_rvalue.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "assignment"


    public static class expression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:99:1: expression : additive_expression ( ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) ^ additive_expression )* ;
    public final StapleParser.expression_return expression() throws RecognitionException {
        StapleParser.expression_return retval = new StapleParser.expression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token set48=null;
        StapleParser.additive_expression_return additive_expression47 =null;

        StapleParser.additive_expression_return additive_expression49 =null;


        StapleTree set48_tree=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:100:2: ( additive_expression ( ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) ^ additive_expression )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:100:4: additive_expression ( ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) ^ additive_expression )*
            {
            root_0 = (StapleTree)adaptor.nil();


            pushFollow(FOLLOW_additive_expression_in_expression619);
            additive_expression47=additive_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additive_expression47.getTree());

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:100:24: ( ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) ^ additive_expression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==30||(LA12_0 >= 40 && LA12_0 <= 41)||(LA12_0 >= 43 && LA12_0 <= 45)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:100:25: ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) ^ additive_expression
            	    {
            	    set48=(Token)input.LT(1);

            	    set48=(Token)input.LT(1);

            	    if ( input.LA(1)==30||(input.LA(1) >= 40 && input.LA(1) <= 41)||(input.LA(1) >= 43 && input.LA(1) <= 45) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (StapleTree)adaptor.becomeRoot(
            	        (StapleTree)adaptor.create(set48)
            	        , root_0);
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_additive_expression_in_expression637);
            	    additive_expression49=additive_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additive_expression49.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expression"


    public static class additive_expression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "additive_expression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:103:1: additive_expression : multiplicative_expression ( ( '+' | '-' ) ^ multiplicative_expression )* ;
    public final StapleParser.additive_expression_return additive_expression() throws RecognitionException {
        StapleParser.additive_expression_return retval = new StapleParser.additive_expression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token set51=null;
        StapleParser.multiplicative_expression_return multiplicative_expression50 =null;

        StapleParser.multiplicative_expression_return multiplicative_expression52 =null;


        StapleTree set51_tree=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:104:2: ( multiplicative_expression ( ( '+' | '-' ) ^ multiplicative_expression )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:104:4: multiplicative_expression ( ( '+' | '-' ) ^ multiplicative_expression )*
            {
            root_0 = (StapleTree)adaptor.nil();


            pushFollow(FOLLOW_multiplicative_expression_in_additive_expression650);
            multiplicative_expression50=multiplicative_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicative_expression50.getTree());

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:104:30: ( ( '+' | '-' ) ^ multiplicative_expression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==34||LA13_0==36) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:104:31: ( '+' | '-' ) ^ multiplicative_expression
            	    {
            	    set51=(Token)input.LT(1);

            	    set51=(Token)input.LT(1);

            	    if ( input.LA(1)==34||input.LA(1)==36 ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (StapleTree)adaptor.becomeRoot(
            	        (StapleTree)adaptor.create(set51)
            	        , root_0);
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_multiplicative_expression_in_additive_expression660);
            	    multiplicative_expression52=multiplicative_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicative_expression52.getTree());

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "additive_expression"


    public static class multiplicative_expression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "multiplicative_expression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:107:1: multiplicative_expression : unaryExpression ( ( '*' | '/' ) ^ unaryExpression )* ;
    public final StapleParser.multiplicative_expression_return multiplicative_expression() throws RecognitionException {
        StapleParser.multiplicative_expression_return retval = new StapleParser.multiplicative_expression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token set54=null;
        StapleParser.unaryExpression_return unaryExpression53 =null;

        StapleParser.unaryExpression_return unaryExpression55 =null;


        StapleTree set54_tree=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:108:2: ( unaryExpression ( ( '*' | '/' ) ^ unaryExpression )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:108:4: unaryExpression ( ( '*' | '/' ) ^ unaryExpression )*
            {
            root_0 = (StapleTree)adaptor.nil();


            pushFollow(FOLLOW_unaryExpression_in_multiplicative_expression673);
            unaryExpression53=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression53.getTree());

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:108:20: ( ( '*' | '/' ) ^ unaryExpression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==33||LA14_0==38) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:108:21: ( '*' | '/' ) ^ unaryExpression
            	    {
            	    set54=(Token)input.LT(1);

            	    set54=(Token)input.LT(1);

            	    if ( input.LA(1)==33||input.LA(1)==38 ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (StapleTree)adaptor.becomeRoot(
            	        (StapleTree)adaptor.create(set54)
            	        , root_0);
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_unaryExpression_in_multiplicative_expression683);
            	    unaryExpression55=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression55.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "multiplicative_expression"


    public static class unaryExpression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExpression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:111:1: unaryExpression : ( '!' unaryExpression -> ^( NOT unaryExpression ) | postfixExpression );
    public final StapleParser.unaryExpression_return unaryExpression() throws RecognitionException {
        StapleParser.unaryExpression_return retval = new StapleParser.unaryExpression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal56=null;
        StapleParser.unaryExpression_return unaryExpression57 =null;

        StapleParser.postfixExpression_return postfixExpression58 =null;


        StapleTree char_literal56_tree=null;
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:112:2: ( '!' unaryExpression -> ^( NOT unaryExpression ) | postfixExpression )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==29) ) {
                alt15=1;
            }
            else if ( (LA15_0==ID||LA15_0==INT||LA15_0==StringLiteral||LA15_0==51||LA15_0==53) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:112:4: '!' unaryExpression
                    {
                    char_literal56=(Token)match(input,29,FOLLOW_29_in_unaryExpression697); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal56);


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression699);
                    unaryExpression57=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression57.getTree());

                    // AST REWRITE
                    // elements: unaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (StapleTree)adaptor.nil();
                    // 112:24: -> ^( NOT unaryExpression )
                    {
                        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:112:27: ^( NOT unaryExpression )
                        {
                        StapleTree root_1 = (StapleTree)adaptor.nil();
                        root_1 = (StapleTree)adaptor.becomeRoot(
                        (StapleTree)adaptor.create(NOT, "NOT")
                        , root_1);

                        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:113:4: postfixExpression
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    pushFollow(FOLLOW_postfixExpression_in_unaryExpression712);
                    postfixExpression58=postfixExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, postfixExpression58.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"


    public static class primary_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primary"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:116:1: primary : ( 'this' -> THIS | ID | INT | StringLiteral | 'new' classname arguments -> ^( CREATEOBJ classname arguments ) );
    public final StapleParser.primary_return primary() throws RecognitionException {
        StapleParser.primary_return retval = new StapleParser.primary_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token string_literal59=null;
        Token ID60=null;
        Token INT61=null;
        Token StringLiteral62=null;
        Token string_literal63=null;
        StapleParser.classname_return classname64 =null;

        StapleParser.arguments_return arguments65 =null;


        StapleTree string_literal59_tree=null;
        StapleTree ID60_tree=null;
        StapleTree INT61_tree=null;
        StapleTree StringLiteral62_tree=null;
        StapleTree string_literal63_tree=null;
        RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_classname=new RewriteRuleSubtreeStream(adaptor,"rule classname");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:117:2: ( 'this' -> THIS | ID | INT | StringLiteral | 'new' classname arguments -> ^( CREATEOBJ classname arguments ) )
            int alt16=5;
            switch ( input.LA(1) ) {
            case 53:
                {
                alt16=1;
                }
                break;
            case ID:
                {
                alt16=2;
                }
                break;
            case INT:
                {
                alt16=3;
                }
                break;
            case StringLiteral:
                {
                alt16=4;
                }
                break;
            case 51:
                {
                alt16=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }

            switch (alt16) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:117:4: 'this'
                    {
                    string_literal59=(Token)match(input,53,FOLLOW_53_in_primary724); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_53.add(string_literal59);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (StapleTree)adaptor.nil();
                    // 117:11: -> THIS
                    {
                        adaptor.addChild(root_0, 
                        (StapleTree)adaptor.create(THIS, "THIS")
                        );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:118:4: ID
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    ID60=(Token)match(input,ID,FOLLOW_ID_in_primary733); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID60_tree = 
                    (StapleTree)adaptor.create(ID60)
                    ;
                    adaptor.addChild(root_0, ID60_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:119:4: INT
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    INT61=(Token)match(input,INT,FOLLOW_INT_in_primary738); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT61_tree = 
                    (StapleTree)adaptor.create(INT61)
                    ;
                    adaptor.addChild(root_0, INT61_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:120:4: StringLiteral
                    {
                    root_0 = (StapleTree)adaptor.nil();


                    StringLiteral62=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_primary743); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral62_tree = 
                    (StapleTree)adaptor.create(StringLiteral62)
                    ;
                    adaptor.addChild(root_0, StringLiteral62_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:121:4: 'new' classname arguments
                    {
                    string_literal63=(Token)match(input,51,FOLLOW_51_in_primary748); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_51.add(string_literal63);


                    pushFollow(FOLLOW_classname_in_primary750);
                    classname64=classname();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_classname.add(classname64.getTree());

                    pushFollow(FOLLOW_arguments_in_primary752);
                    arguments65=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments65.getTree());

                    // AST REWRITE
                    // elements: classname, arguments
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (StapleTree)adaptor.nil();
                    // 121:30: -> ^( CREATEOBJ classname arguments )
                    {
                        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:121:33: ^( CREATEOBJ classname arguments )
                        {
                        StapleTree root_1 = (StapleTree)adaptor.nil();
                        root_1 = (StapleTree)adaptor.becomeRoot(
                        (StapleTree)adaptor.create(CREATEOBJ, "CREATEOBJ")
                        , root_1);

                        adaptor.addChild(root_1, stream_classname.nextTree());

                        adaptor.addChild(root_1, stream_arguments.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "primary"


    public static class postfixExpression_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "postfixExpression"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:124:1: postfixExpression : ( primary -> primary ) ( '.' ID args= arguments -> ^( CALL $postfixExpression ID $args) | '.' p= primary -> ^( FIELDACCESS $postfixExpression $p) )* ;
    public final StapleParser.postfixExpression_return postfixExpression() throws RecognitionException {
        StapleParser.postfixExpression_return retval = new StapleParser.postfixExpression_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal67=null;
        Token ID68=null;
        Token char_literal69=null;
        StapleParser.arguments_return args =null;

        StapleParser.primary_return p =null;

        StapleParser.primary_return primary66 =null;


        StapleTree char_literal67_tree=null;
        StapleTree ID68_tree=null;
        StapleTree char_literal69_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:125:2: ( ( primary -> primary ) ( '.' ID args= arguments -> ^( CALL $postfixExpression ID $args) | '.' p= primary -> ^( FIELDACCESS $postfixExpression $p) )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:125:4: ( primary -> primary ) ( '.' ID args= arguments -> ^( CALL $postfixExpression ID $args) | '.' p= primary -> ^( FIELDACCESS $postfixExpression $p) )*
            {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:125:4: ( primary -> primary )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:125:5: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression775);
            primary66=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary66.getTree());

            // AST REWRITE
            // elements: primary
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 125:13: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }


            retval.tree = root_0;
            }

            }


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:126:3: ( '.' ID args= arguments -> ^( CALL $postfixExpression ID $args) | '.' p= primary -> ^( FIELDACCESS $postfixExpression $p) )*
            loop17:
            do {
                int alt17=3;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==37) ) {
                    int LA17_2 = input.LA(2);

                    if ( (LA17_2==ID) ) {
                        int LA17_3 = input.LA(3);

                        if ( (LA17_3==31) ) {
                            alt17=1;
                        }
                        else if ( (LA17_3==30||(LA17_3 >= 32 && LA17_3 <= 45)) ) {
                            alt17=2;
                        }


                    }
                    else if ( (LA17_2==INT||LA17_2==StringLiteral||LA17_2==51||LA17_2==53) ) {
                        alt17=2;
                    }


                }


                switch (alt17) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:126:5: '.' ID args= arguments
            	    {
            	    char_literal67=(Token)match(input,37,FOLLOW_37_in_postfixExpression786); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_37.add(char_literal67);


            	    ID68=(Token)match(input,ID,FOLLOW_ID_in_postfixExpression788); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID68);


            	    pushFollow(FOLLOW_arguments_in_postfixExpression792);
            	    args=arguments();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_arguments.add(args.getTree());

            	    // AST REWRITE
            	    // elements: ID, args, postfixExpression
            	    // token labels: 
            	    // rule labels: retval, args
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {

            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"rule args",args!=null?args.tree:null);

            	    root_0 = (StapleTree)adaptor.nil();
            	    // 126:27: -> ^( CALL $postfixExpression ID $args)
            	    {
            	        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:126:30: ^( CALL $postfixExpression ID $args)
            	        {
            	        StapleTree root_1 = (StapleTree)adaptor.nil();
            	        root_1 = (StapleTree)adaptor.becomeRoot(
            	        (StapleTree)adaptor.create(CALL, "CALL")
            	        , root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());

            	        adaptor.addChild(root_1, 
            	        stream_ID.nextNode()
            	        );

            	        adaptor.addChild(root_1, stream_args.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }


            	    retval.tree = root_0;
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:127:5: '.' p= primary
            	    {
            	    char_literal69=(Token)match(input,37,FOLLOW_37_in_postfixExpression812); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_37.add(char_literal69);


            	    pushFollow(FOLLOW_primary_in_postfixExpression816);
            	    p=primary();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_primary.add(p.getTree());

            	    // AST REWRITE
            	    // elements: postfixExpression, p
            	    // token labels: 
            	    // rule labels: retval, p
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {

            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_p=new RewriteRuleSubtreeStream(adaptor,"rule p",p!=null?p.tree:null);

            	    root_0 = (StapleTree)adaptor.nil();
            	    // 127:21: -> ^( FIELDACCESS $postfixExpression $p)
            	    {
            	        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:127:24: ^( FIELDACCESS $postfixExpression $p)
            	        {
            	        StapleTree root_1 = (StapleTree)adaptor.nil();
            	        root_1 = (StapleTree)adaptor.becomeRoot(
            	        (StapleTree)adaptor.create(FIELDACCESS, "FIELDACCESS")
            	        , root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());

            	        adaptor.addChild(root_1, stream_p.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }


            	    retval.tree = root_0;
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "postfixExpression"


    public static class arguments_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arguments"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:131:1: arguments : '(' ( expressionList )? ')' -> ^( ARGS ( expressionList )* ) ;
    public final StapleParser.arguments_return arguments() throws RecognitionException {
        StapleParser.arguments_return retval = new StapleParser.arguments_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal70=null;
        Token char_literal72=null;
        StapleParser.expressionList_return expressionList71 =null;


        StapleTree char_literal70_tree=null;
        StapleTree char_literal72_tree=null;
        RewriteRuleTokenStream stream_32=new RewriteRuleTokenStream(adaptor,"token 32");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleSubtreeStream stream_expressionList=new RewriteRuleSubtreeStream(adaptor,"rule expressionList");
        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:5: ( '(' ( expressionList )? ')' -> ^( ARGS ( expressionList )* ) )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:9: '(' ( expressionList )? ')'
            {
            char_literal70=(Token)match(input,31,FOLLOW_31_in_arguments852); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_31.add(char_literal70);


            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:13: ( expressionList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ID||LA18_0==INT||LA18_0==StringLiteral||LA18_0==29||LA18_0==51||LA18_0==53) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:13: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments854);
                    expressionList71=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expressionList.add(expressionList71.getTree());

                    }
                    break;

            }


            char_literal72=(Token)match(input,32,FOLLOW_32_in_arguments857); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_32.add(char_literal72);


            // AST REWRITE
            // elements: expressionList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 132:33: -> ^( ARGS ( expressionList )* )
            {
                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:36: ^( ARGS ( expressionList )* )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(ARGS, "ARGS")
                , root_1);

                // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:132:43: ( expressionList )*
                while ( stream_expressionList.hasNext() ) {
                    adaptor.addChild(root_1, stream_expressionList.nextTree());

                }
                stream_expressionList.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arguments"


    public static class expressionList_return extends ParserRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expressionList"
    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:135:1: expressionList : expression ( ',' expression )* ;
    public final StapleParser.expressionList_return expressionList() throws RecognitionException {
        StapleParser.expressionList_return retval = new StapleParser.expressionList_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        Token char_literal74=null;
        StapleParser.expression_return expression73 =null;

        StapleParser.expression_return expression75 =null;


        StapleTree char_literal74_tree=null;

        try {
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:136:5: ( expression ( ',' expression )* )
            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:136:9: expression ( ',' expression )*
            {
            root_0 = (StapleTree)adaptor.nil();


            pushFollow(FOLLOW_expression_in_expressionList889);
            expression73=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression73.getTree());

            // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:136:20: ( ',' expression )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==35) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:136:21: ',' expression
            	    {
            	    char_literal74=(Token)match(input,35,FOLLOW_35_in_expressionList892); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal74_tree = 
            	    (StapleTree)adaptor.create(char_literal74)
            	    ;
            	    adaptor.addChild(root_0, char_literal74_tree);
            	    }

            	    pushFollow(FOLLOW_expression_in_expressionList894);
            	    expression75=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression75.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (StapleTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    // $ANTLR start synpred1_Staple
    public final void synpred1_Staple_fragment() throws RecognitionException {
        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:86:4: ( leftside '=' )
        // /home/paul/workspace/Staple/src/com/devsmart/Staple.g:86:6: leftside '='
        {
        pushFollow(FOLLOW_leftside_in_synpred1_Staple543);
        leftside();

        state._fsp--;
        if (state.failed) return ;

        match(input,42,FOLLOW_42_in_synpred1_Staple545); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Staple

    // Delegated rules

    public final boolean synpred1_Staple() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Staple_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_packageDefinition_in_compilationUnit106 = new BitSet(new long[]{0x0002800000000000L});
    public static final BitSet FOLLOW_importDefinition_in_compilationUnit109 = new BitSet(new long[]{0x0002800000000000L});
    public static final BitSet FOLLOW_classDefinition_in_compilationUnit112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_packageDefinition138 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_classname_in_packageDefinition140 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_packageDefinition142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_classname162 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_37_in_classname165 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_classname167 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_49_in_importDefinition185 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_classname_in_importDefinition187 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_importDefinition189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_classDefinition209 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_classDefinition213 = new BitSet(new long[]{0x0081000000000000L});
    public static final BitSet FOLLOW_48_in_classDefinition218 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_classDefinition222 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_classDefinition228 = new BitSet(new long[]{0x0144400000010000L});
    public static final BitSet FOLLOW_fieldDefinition_in_classDefinition235 = new BitSet(new long[]{0x0144400000010000L});
    public static final BitSet FOLLOW_methodDefinition_in_classDefinition241 = new BitSet(new long[]{0x0144400000010000L});
    public static final BitSet FOLLOW_56_in_classDefinition250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinition_in_fieldDefinition324 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_fieldDefinition326 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_fieldDefinition328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinition_in_methodDefinition348 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_methodDefinition350 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_methodDefinition352 = new BitSet(new long[]{0x0044400100010000L});
    public static final BitSet FOLLOW_formalArgs_in_methodDefinition354 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_methodDefinition357 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_block_in_methodDefinition359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalArg_in_formalArgs393 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_formalArgs396 = new BitSet(new long[]{0x0044400000010000L});
    public static final BitSet FOLLOW_formalArg_in_formalArgs398 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_typeDefinition_in_formalArg418 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_formalArg420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinition_in_variableDefinition468 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_variableDefinition470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_block492 = new BitSet(new long[]{0x01EC400001050000L});
    public static final BitSet FOLLOW_statement_in_block494 = new BitSet(new long[]{0x01EC400001050000L});
    public static final BitSet FOLLOW_56_in_block497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement523 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_statement525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_statementExpression551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_leftside_in_statementExpression556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_leftside572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDefinition_in_leftside577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_leftside_in_assignment590 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_assignment592 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_expression_in_assignment596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additive_expression_in_expression619 = new BitSet(new long[]{0x00003B0040000002L});
    public static final BitSet FOLLOW_set_in_expression622 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_additive_expression_in_expression637 = new BitSet(new long[]{0x00003B0040000002L});
    public static final BitSet FOLLOW_multiplicative_expression_in_additive_expression650 = new BitSet(new long[]{0x0000001400000002L});
    public static final BitSet FOLLOW_set_in_additive_expression653 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_multiplicative_expression_in_additive_expression660 = new BitSet(new long[]{0x0000001400000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicative_expression673 = new BitSet(new long[]{0x0000004200000002L});
    public static final BitSet FOLLOW_set_in_multiplicative_expression676 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicative_expression683 = new BitSet(new long[]{0x0000004200000002L});
    public static final BitSet FOLLOW_29_in_unaryExpression697 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_unaryExpression712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_primary724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_primary738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_primary743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_primary748 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_classname_in_primary750 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_arguments_in_primary752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression775 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_37_in_postfixExpression786 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_postfixExpression788 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_arguments_in_postfixExpression792 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_37_in_postfixExpression812 = new BitSet(new long[]{0x0028000001050000L});
    public static final BitSet FOLLOW_primary_in_postfixExpression816 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_31_in_arguments852 = new BitSet(new long[]{0x0028000121050000L});
    public static final BitSet FOLLOW_expressionList_in_arguments854 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_arguments857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList889 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_expressionList892 = new BitSet(new long[]{0x0028000021050000L});
    public static final BitSet FOLLOW_expression_in_expressionList894 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_leftside_in_synpred1_Staple543 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred1_Staple545 = new BitSet(new long[]{0x0000000000000002L});

}