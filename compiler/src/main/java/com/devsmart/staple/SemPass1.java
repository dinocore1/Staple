package com.devsmart.staple;


import com.devsmart.staple.type.ClassType;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

public class SemPass1 extends StapleBaseVisitor<Void> {

    private final CompilerContext compilerContext;
    private Scope currentScope;
    private Namespace namespace;

    public SemPass1(CompilerContext ctx) {
        compilerContext = ctx;
        currentScope = compilerContext.rootScope;
        namespace = Namespace.defaultNameSpace;
        currentScope.put("Object", com.devsmart.staple.runtime.Runtime.BaseObject);
    }

    @Override
    public Void visitNamespaceDecl(@NotNull StapleParser.NamespaceDeclContext ctx) {
        namespace = new Namespace(Collections2.transform(ctx.Identifier(), new Function<TerminalNode, String>() {
            @Override
            public String apply(TerminalNode input) {
                return input.getText();
            }
        }));

        return null;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        ClassType classType = new ClassType(ctx.Identifier().getText());
        classType.namespace = namespace;
        classType.scope = new Scope(currentScope);

        currentScope.put(classType.name, classType);
        compilerContext.symbols.put(ctx, classType);

        currentScope = classType.scope;
        visitChildren(ctx);
        currentScope = currentScope.mParent;

        return null;
    }
}
