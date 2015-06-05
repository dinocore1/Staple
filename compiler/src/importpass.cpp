
#include "importpass.h"
#include "typehelper.h"

#include <llvm/Support/FileSystem.h>

#include <set>
#include <fstream>

#define path_sep '/'

using namespace std;
using namespace llvm;

namespace staple {

#define CONTAINS(x, set) (set.find(x) != set.end())

    /*
    bool treenodecomp(const ScopeTreeNode* lhs, const ScopeTreeNode* rhs) {
        return lhs->name.compare(rhs->name) < 0;
    }

    ScopeTreeNode::ScopeTreeNode(ScopeTreeNode *parent, const string &name, Scope* scope = nullptr)
    : name(name), parent(parent), mChildren(treenodecomp), scope(scope) {

    }
     */

    set<string> Pass1ClassVisitor::mPass1VisitedPaths;

    void Pass1ClassVisitor::visit(NCompileUnit* compileUnit) {

        //first pass class declaration
        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            StapleClass* stpClass = new StapleClass(fqClassName);
            mContext->mRootScope.table[fqClassName] = stpClass;
        }

        for(string import : compileUnit->includes) {
            string importPath = import;
            replace(importPath.begin(), importPath.end(), '.', path_sep);

            for (string searchPath : mContext->searchPaths) {
                if (sys::fs::is_directory(searchPath)) {

                    string srcFilePath = searchPath + path_sep + importPath + ".stp";
                    if (sys::fs::is_regular_file(srcFilePath)) {
                        ifstream inputFileStream(srcFilePath);
                        if (!inputFileStream) {
                            fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                        } else if (!CONTAINS(srcFilePath, mPass1VisitedPaths)) {

                            mPass1VisitedPaths.insert(srcFilePath);

                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            Pass1ClassVisitor visitor(mContext);
                            visitor.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }

    }


    set<string> Pass2ClassVisitor::mPass2VisitedPaths;

    void Pass2ClassVisitor::visit(NField* field) {
        StapleType* stpType = getType(&field->type);
        mCurrentClass->addField(field->name, stpType);
    }

    void Pass2ClassVisitor::visit(NType* type) {
        StapleType* retType = getStapleType(type, mContext, mCompileUnit, mContext->mRootScope);
        mType[type] = retType;
    }

    void Pass2ClassVisitor::visit(NCompileUnit* compileUnit) {

        mCompileUnit = compileUnit;

        //second pass class declaration
        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);

            for(NField* field : classDeclaration->fields){
                field->accept(this);
            }
        }

        for(string import : compileUnit->includes) {
            string importPath = import;
            replace(importPath.begin(), importPath.end(), '.', path_sep);

            for (string searchPath : mContext->searchPaths) {
                if (sys::fs::is_directory(searchPath)) {

                    string srcFilePath = searchPath + path_sep + importPath + ".stp";
                    if (sys::fs::is_regular_file(srcFilePath)) {
                        ifstream inputFileStream(srcFilePath);
                        if (!inputFileStream) {
                            fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                        } else if (!CONTAINS(srcFilePath, mPass2VisitedPaths)) {

                            mPass2VisitedPaths.insert(srcFilePath);

                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            Pass2ClassVisitor visitor(mContext);
                            visitor.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }

    }

}