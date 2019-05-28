
#include "stdafx.h"
#include "Sempass.h"

namespace staple {

Sempass1Visitor::Sempass1Visitor(CompilerContext& ctx)
 : mCtx(ctx)
{}


static
bool searchClasspathFor(const CompilerContext& ctx, const FQPath& path, File& found)
{
    for(const File& include : ctx.includeDirs) {

        std::stringbuf buf;
        std::ostream os(&buf);

        for(size_t i=0; i<path.getNumParts(); i++) {
            
            os << path.part(i);
        }

        os << ".stp";

        File srcFile(include, buf.str());
        if(srcFile.isFile()) {
            found = srcFile;
            return true;
        }
    }

    return false;
}

void Sempass1Visitor::visit(NCompileUnit* compileUnit)
{
    mCompileUnitCtx.push_back(compileUnit);
    visitChildren(compileUnit);
    mCompileUnitCtx.pop_back();
}

void Sempass1Visitor::visit(NImport* import)
{
    File file;
    if(mCtx.mParsedFiles.find(import->mPath) == mCtx.mParsedFiles.end()) {
        if(searchClasspathFor(mCtx, import->mPath, file)){
            CompilerContext ctx;
            ctx.setInputFile(file);
            if(ctx.parse()){
                import->add(ctx.rootNode);
                mCtx.mParsedFiles[import->mPath] = ctx.rootNode;
                ctx.rootNode->accept(this);
            }
        }
    }
}

void Sempass1Visitor::visit(NClassDecl* classDecl)
{
    FQPath path = mCompileUnitCtx.back()->mPackage;
    path.add(classDecl->mName);

    mCtx.mKnownTypes[path] = new ClassType();
}

void Sempass1Visitor::visit(NExternFunctionDecl* externalFunDecl)
{
    FQPath path(externalFunDecl->mName);
    mCtx.mKnownTypes[path] = new FunctionType();
}

void Sempass1Visitor::visit(NFunctionDecl* funDecl)
{
    FQPath path = mCompileUnitCtx.back()->mPackage;
    path.add(funDecl->mName);

    mCtx.mKnownTypes[path] = new FunctionType();
}

}