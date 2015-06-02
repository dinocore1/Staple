
#include "importpass.h"
#include "typehelper.h"

#include <llvm/Support/FileSystem.h>

#include <set>
#include <fstream>

#define path_sep '/'

using namespace std;
using namespace llvm;

namespace staple {

    class ImportVisitor : public ASTVisitor {
    using ASTVisitor::visit;

    private:
        set<string>& mVisitedPaths;
        CompilerContext* mContext;
        Scope* mScope;
        map<ASTNode*, StapleType*> mType;
        StapleClass* mCurrentClass;

        StapleType* getType(ASTNode* node) {
            node->accept(this);
            return mType[node];
        }

    public:
        ImportVisitor(set<string>& visitedPaths, CompilerContext* context) : mVisitedPaths(visitedPaths), mContext(context) {
            mScope = new Scope(&mContext->mRootScope);
        }

        virtual ~ImportVisitor() {
            delete mScope;
        }


        virtual void visit(NCompileUnit *compileUnit) override;
        virtual void visit(NField* field) override;
        virtual void visit(NType* type) override;
    };

    ImportPass::ImportPass(CompilerContext *ctx)
    : mContext(ctx) { }

    void ImportPass::doIt() {

        for(string include : mContext->mCompileUnit->includes) {

            for(string searchPath : mContext->searchPaths) {
                if(sys::fs::is_directory(searchPath)) {
                    string includePath = include;
                    replace(includePath.begin(), includePath.end(), '.', path_sep);
                    string srcFilePath = searchPath + path_sep + includePath + ".stp";
                    if(sys::fs::is_regular_file(srcFilePath)) {
                        ifstream inputFileStream(srcFilePath);
                        if (!inputFileStream) {
                            fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                        } else {


                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            mVisitedPaths.insert(srcFilePath);
                            ImportVisitor visitor(mVisitedPaths, mContext);
                            visitor.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }
    }


    void ImportVisitor::visit(NCompileUnit *compileUnit) {

        //first pass class declaration
        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            StapleClass* stpClass = new StapleClass(fqClassName);
            mContext->mRootScope.table[fqClassName] = stpClass;
            mScope->table[classDeclaration->name] = stpClass;
        }

        //second pass class declaration
        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);

            for(NField* field : classDeclaration->fields){
                field->accept(this);
            }
        }

    }

    void ImportVisitor::visit(NField* field) {
        StapleType* stpType = getType(&field->type);
        mCurrentClass->addField(field->name, stpType);
    }

    void ImportVisitor::visit(NType* type) {

        StapleType* retType = getStapleType(type, mScope);
        mType[type] = retType;
    }
}