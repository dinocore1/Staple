// $ANTLR 3.4 /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g 2012-12-09 23:56:25

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
public class BlockReorg extends TreeRewriter {
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
    public TreeRewriter[] getDelegates() {
        return new TreeRewriter[] {};
    }

    // delegators


    public BlockReorg(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public BlockReorg(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return BlockReorg.tokenNames; }
    public String getGrammarFileName() { return "/home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g"; }


       
        ErrorStream errorstream;
        Scope currentScope;
        
        public BlockReorg(TreeNodeStream input, ErrorStream estream) {
            this(input);
            errorstream = estream;
        }


    public static class topdown_return extends TreeRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "topdown"
    // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:27:1: topdown :;
    public final BlockReorg.topdown_return topdown() throws RecognitionException {
        BlockReorg.topdown_return retval = new BlockReorg.topdown_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleTree _first_0 = null;
        StapleTree _last = null;

        try {
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:28:2: ()
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:29:2: 
            {
            if ( state.backtracking==1 ) {
            retval.tree = (StapleTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (StapleTree)adaptor.getParent(retval.tree);
            }
            }

        }
        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "topdown"


    public static class bottomup_return extends TreeRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "bottomup"
    // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:31:1: bottomup : objAssign ;
    public final BlockReorg.bottomup_return bottomup() throws RecognitionException {
        BlockReorg.bottomup_return retval = new BlockReorg.bottomup_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleTree _first_0 = null;
        StapleTree _last = null;

        BlockReorg.objAssign_return objAssign1 =null;



        try {
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:32:2: ( objAssign )
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:32:4: objAssign
            {
            _last = (StapleTree)input.LT(1);
            pushFollow(FOLLOW_objAssign_in_bottomup72);
            objAssign1=objAssign();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==1 ) 
             
            if ( _first_0==null ) _first_0 = objAssign1.tree;


            if ( state.backtracking==1 ) {
            retval.tree = (StapleTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (StapleTree)adaptor.getParent(retval.tree);
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
    // $ANTLR end "bottomup"


    public static class objAssign_return extends TreeRuleReturnScope {
        StapleTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "objAssign"
    // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:35:1: objAssign : ^( ASSIGN lside= . rside= . ) -> { ((VarableSymbol)$lside.symbol).type instanceof ClassType }? ^( BLOCK ^( CALL $lside ID[\"release\"] ^( ARGS $lside) ) ^( ASSIGN $lside $rside) ) -> ^( ASSIGN $lside $rside) ;
    public final BlockReorg.objAssign_return objAssign() throws RecognitionException {
        BlockReorg.objAssign_return retval = new BlockReorg.objAssign_return();
        retval.start = input.LT(1);


        StapleTree root_0 = null;

        StapleTree _first_0 = null;
        StapleTree _last = null;

        StapleTree ASSIGN2=null;
        StapleTree lside=null;
        StapleTree rside=null;

        StapleTree ASSIGN2_tree=null;
        StapleTree lside_tree=null;
        StapleTree rside_tree=null;
        RewriteRuleNodeStream stream_ASSIGN=new RewriteRuleNodeStream(adaptor,"token ASSIGN");

        try {
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:36:2: ( ^( ASSIGN lside= . rside= . ) -> { ((VarableSymbol)$lside.symbol).type instanceof ClassType }? ^( BLOCK ^( CALL $lside ID[\"release\"] ^( ARGS $lside) ) ^( ASSIGN $lside $rside) ) -> ^( ASSIGN $lside $rside) )
            // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:36:4: ^( ASSIGN lside= . rside= . )
            {
            _last = (StapleTree)input.LT(1);
            {
            StapleTree _save_last_1 = _last;
            StapleTree _first_1 = null;
            _last = (StapleTree)input.LT(1);
            ASSIGN2=(StapleTree)match(input,ASSIGN,FOLLOW_ASSIGN_in_objAssign85); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_ASSIGN.add(ASSIGN2);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = ASSIGN2;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (StapleTree)input.LT(1);
            lside=(StapleTree)input.LT(1);

            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = lside;


            _last = (StapleTree)input.LT(1);
            rside=(StapleTree)input.LT(1);

            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = rside;


            match(input, Token.UP, null); if (state.failed) return retval;
            _last = _save_last_1;
            }


            // AST REWRITE
            // elements: ASSIGN, lside, ASSIGN, lside, rside, rside, lside, lside
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: rside, lside
            if ( state.backtracking==1 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_rside=new RewriteRuleSubtreeStream(adaptor,"wildcard rside",rside);
            RewriteRuleSubtreeStream stream_lside=new RewriteRuleSubtreeStream(adaptor,"wildcard lside",lside);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (StapleTree)adaptor.nil();
            // 37:4: -> { ((VarableSymbol)$lside.symbol).type instanceof ClassType }? ^( BLOCK ^( CALL $lside ID[\"release\"] ^( ARGS $lside) ) ^( ASSIGN $lside $rside) )
            if (  ((VarableSymbol)lside.symbol).type instanceof ClassType ) {
                // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:37:70: ^( BLOCK ^( CALL $lside ID[\"release\"] ^( ARGS $lside) ) ^( ASSIGN $lside $rside) )
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:37:78: ^( CALL $lside ID[\"release\"] ^( ARGS $lside) )
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(CALL, "CALL")
                , root_2);

                adaptor.addChild(root_2, stream_lside.nextTree());

                adaptor.addChild(root_2, 
                (StapleTree)adaptor.create(ID, "release")
                );

                // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:37:106: ^( ARGS $lside)
                {
                StapleTree root_3 = (StapleTree)adaptor.nil();
                root_3 = (StapleTree)adaptor.becomeRoot(
                (StapleTree)adaptor.create(ARGS, "ARGS")
                , root_3);

                adaptor.addChild(root_3, stream_lside.nextTree());

                adaptor.addChild(root_2, root_3);
                }

                adaptor.addChild(root_1, root_2);
                }

                // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:37:122: ^( ASSIGN $lside $rside)
                {
                StapleTree root_2 = (StapleTree)adaptor.nil();
                root_2 = (StapleTree)adaptor.becomeRoot(
                stream_ASSIGN.nextNode()
                , root_2);

                adaptor.addChild(root_2, stream_lside.nextTree());

                adaptor.addChild(root_2, stream_rside.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            else // 38:3: -> ^( ASSIGN $lside $rside)
            {
                // /home/paul/Documents/Staple/src/com/devsmart/BlockReorg.g:38:6: ^( ASSIGN $lside $rside)
                {
                StapleTree root_1 = (StapleTree)adaptor.nil();
                root_1 = (StapleTree)adaptor.becomeRoot(
                stream_ASSIGN.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_lside.nextTree());

                adaptor.addChild(root_1, stream_rside.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = (StapleTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);
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
    // $ANTLR end "objAssign"

    // Delegated rules


 

    public static final BitSet FOLLOW_objAssign_in_bottomup72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_objAssign85 = new BitSet(new long[]{0x0000000000000004L});

}