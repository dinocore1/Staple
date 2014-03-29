package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.ArrayAccess;
import com.devsmart.staple.AST.Assignment;
import com.devsmart.staple.AST.Block;
import com.devsmart.staple.AST.ClassDecl;
import com.devsmart.staple.AST.ClassFunction;
import com.devsmart.staple.AST.ClassMember;
import com.devsmart.staple.AST.IntLiteral;
import com.devsmart.staple.AST.MathOp;
import com.devsmart.staple.AST.Relation;
import com.devsmart.staple.AST.SymbolRef;
import com.devsmart.staple.AST.VarDecl;
import com.devsmart.staple.symbol.ClassSymbol;
import com.devsmart.staple.symbol.MemberFunctionSymbol;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.BoolType;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.FloatType;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.IntType;
import com.devsmart.staple.type.Type;
import com.devsmart.staple.type.VoidType;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemPass2 extends StapleBaseVisitor<ASTNode> {

    private final CompilerContext mCompilerContext;
    private Scope currentScope;

    public SemPass2(CompilerContext ctx) {
        mCompilerContext = ctx;
    }

    private void pushScope() {
        currentScope = new Scope(currentScope);
    }

    private void popScope() {
        if(currentScope != null){
            currentScope = currentScope.mParent;
        }
    }

    @Override
    public ASTNode visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassDecl classDecl = (ClassDecl) mCompilerContext.astTreeProperties.get(ctx);
        currentScope = classDecl.scope;

        for(StapleParser.MemberVarDeclContext m : ctx.m){
            classDecl.members.add((ClassMember) visit(m));
        }

        for(StapleParser.MemberFunctionDeclContext f : ctx.f) {
            classDecl.functions.add((ClassFunction) visit(f));
        }


        return classDecl;
    }

    @Override
    public ASTNode visitMemberVarDecl(@NotNull StapleParser.MemberVarDeclContext ctx) {
        ClassMember retval = new ClassMember();

        retval.scope = currentScope;


        ASTNode typeNode = visit(ctx.t);
        final String varName = ctx.n.getText();

        retval.type = typeNode.type;
        Symbol symbol = new Symbol(varName, typeNode.type);
        currentScope.define(symbol);

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitMemberFunctionDecl(@NotNull StapleParser.MemberFunctionDeclContext ctx) {
        ClassFunction retval = new ClassFunction();

        retval.returnType = visit(ctx.r).type;
        retval.name = ctx.n.toString();
        for(ParserRuleContext arg : ctx.args){
            retval.args.add((VarDecl) visit(arg));
        }

        MemberFunctionSymbol symbol = new MemberFunctionSymbol(retval.name,
                new FunctionType(retval.returnType, retval.getArgTypes()));

        currentScope.define(symbol);

        retval.block = (Block) visit(ctx.block());

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitArg(@NotNull StapleParser.ArgContext ctx) {
        ASTNode type = visit(ctx.t);
        Symbol symbol = new Symbol(ctx.n.getText(), type.type);
        VarDecl retval = new VarDecl(symbol);

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitBlock(@NotNull StapleParser.BlockContext ctx) {
        pushScope();
        Block block = new Block();
        block.scope = currentScope;
        for(ParserRuleContext stmt : ctx.stmt()){
            block.statements.add(visit(stmt));
        }
        popScope();

        mCompilerContext.astTreeProperties.put(ctx, block);
        return block;
    }

    @Override
    public ASTNode visitLocalVarDecl(@NotNull StapleParser.LocalVarDeclContext ctx) {
        ASTNode type = visitType(ctx.t);
        String name = ctx.id.getText();

        Symbol var = new Symbol(name, type.type);
        currentScope.define(var);

        VarDecl retval = new VarDecl(var);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitAssign(@NotNull StapleParser.AssignContext ctx) {

        ASTNode left = visit(ctx.l);
        ASTNode right = visit(ctx.r);

       if(!right.type.isAssignableTo(left.type)) {
           String errStr = String.format("'%s' is not assignable to '%s'",
                   right.type.name, left.type.name);
           mCompilerContext.errorStream.error(errStr, ctx.l.start);
       }

        Assignment retval = new Assignment(left, right);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        MathOp retval = new MathOp(op, visit(ctx.l), visit(ctx.r));
        retval.type = retval.left.type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitRelation(@NotNull StapleParser.RelationContext ctx) {
        Relation.Operator op = Relation.Operator.getOperation(ctx.op.getText());
        Relation retval = new Relation(op, visit(ctx.l), visit(ctx.r));
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitSymbolReference(@NotNull StapleParser.SymbolReferenceContext ctx) {
        Symbol symbol = currentScope.get(ctx.v.getText());
        SymbolRef retval = new SymbolRef(symbol);
        mCompilerContext.astTreeProperties.put(ctx, retval);

        return retval;
    }

    @Override
    public ASTNode visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        IntLiteral retval = new IntLiteral(ctx.v.getText());
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitArrayAccess(@NotNull StapleParser.ArrayAccessContext ctx) {
        Symbol symbol = currentScope.get(ctx.a.getText());
        ArrayList<ASTNode> dim = new ArrayList<ASTNode>(ctx.dim.size());
        for(ParserRuleContext d : ctx.dim) {
            dim.add(visit(d));
        }
        ArrayAccess arrayAccess = new ArrayAccess(symbol, dim);
        mCompilerContext.astTreeProperties.put(ctx, arrayAccess);
        return arrayAccess;
    }

    public static final Pattern INT_REGEX = Pattern.compile("int([0-9]*)");
    public static final Pattern FLOAT_REGEX = Pattern.compile("float([0-9]*)");

    @Override
    public ASTNode visitType(@NotNull StapleParser.TypeContext ctx) {
        final String text = ctx.getText();
        Type type = null;

        Matcher m = null;
        if((m = INT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new IntType(Integer.parseInt(m.group(1)));
            } else {
                type = IntType.INT32;
            }
        } else if((m = FLOAT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new FloatType(Integer.parseInt(m.group(1)));
            } else {
                type = FloatType.FLOAT32;
            }
        } else if("bool".equals(text)) {
            type = BoolType.BOOL;
        } else if("void".equals(text)) {
            type = VoidType.VOID;
        } else {
            Symbol classSymbol = currentScope.get(text);
            if(classSymbol == null || !(classSymbol instanceof ClassSymbol)){
                mCompilerContext.errorStream.error(String.format("unknown class: '%s'", text), ctx.getStart());
            } else {
                type = classSymbol.type;
            }
        }

        ASTNode retval = new ASTNode();
        retval.type = type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }
}
