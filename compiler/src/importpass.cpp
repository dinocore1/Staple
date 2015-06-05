
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

    bool treenodecomp(const ScopeTreeNode* lhs, const ScopeTreeNode* rhs) {
        return lhs->name.compare(rhs->name) < 0;
    }

    ScopeTreeNode::ScopeTreeNode(ScopeTreeNode *parent, const string &name, Scope* scope = nullptr)
    : name(name), parent(parent), mChildren(treenodecomp), scope(scope) {

    }

    class Pass1ClassVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        CompilerContext* mContext;
        static set<string> mPass1VisitedPaths;

    public:
        Pass1ClassVisitor(CompilerContext* context) : mContext(context)
        { }

        void visit(NCompileUnit* compileUnit) {

            //first pass class declaration
            for(NClassDeclaration* classDeclaration : compileUnit->classes) {
                string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
                StapleClass* stpClass = new StapleClass(fqClassName);
                mContext->mRootScope.table[fqClassName] = stpClass;
            }

            for(string import : compileUnit->includes) {
                for (string searchPath : mContext->searchPaths) {
                    if (sys::fs::is_directory(searchPath)) {

                        string srcFilePath = searchPath + path_sep + import + ".stp";
                        if (sys::fs::is_regular_file(srcFilePath)) {
                            ifstream inputFileStream(srcFilePath);
                            if (!inputFileStream) {
                                fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                            } else if (!CONTAINS(srcFilePath, mPass1VisitedPaths)) {

                                mPass1VisitedPaths.insert(srcFilePath);

                                ParserContext parserContext(&inputFileStream);
                                yyparse(&parserContext);

                                Pass1Visitor visitor(mContext);
                                visitor.visit(parserContext.compileUnit);

                            }
                            break;
                        }
                    }
                }
            }

        }

    };

    class Pass2ClassVisitor : public ASTVisitor {
        using ASTVisitor::visit;
    private:
        CompilerContext* mContext;
        static set<string> mPass2VisitedPaths;
        StapleClass* mCurrentClass;
        map<ASTNode*, StapleType*> mType;
        NCompileUnit* mCompileUnit;

        StapleType* getType(ASTNode* node) {
            node->accept(this);
            return mType[node];
        }

    public:
        Pass2ClassVisitor(CompilerContext* context) : mContext(context)
        { }

        void visit(NField* field) {
            StapleType* stpType = getType(&field->type);
            mCurrentClass->addField(field->name, stpType);
        }

        void visit(NType* type) {
            StapleType* retType = getStapleType(type, mContext, mCompileUnit, mContext->mRootScope);
            mType[type] = retType;
        }

        void visit(NCompileUnit* compileUnit) {

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
                for (string searchPath : mContext->searchPaths) {
                    if (sys::fs::is_directory(searchPath)) {

                        string srcFilePath = searchPath + path_sep + import + ".stp";
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

    };

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

        void visitImport(const string& import);

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

    ImportManager::ImportManager(CompilerContext *ctx)
    : mContext(ctx) {
        mScopeTreeRoot = new ScopeTreeNode(nullptr, "", &mContext->mRootScope);
    }



    StapleType* ImportManager::resolveClassType(NCompileUnit *startingCompileUnit, const string &className) {
        for(string include : mContext->mCompileUnit->includes) {

            string includePath = include;
            replace(includePath.begin(), includePath.end(), '.', path_sep);

            for(string searchPath : mContext->searchPaths) {
                if(sys::fs::is_directory(searchPath)) {

                    string srcFilePath = searchPath + path_sep + includePath + ".stp";
                    if(sys::fs::is_regular_file(srcFilePath)) {
                        ifstream inputFileStream(srcFilePath);
                        if (!inputFileStream) {
                            fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                        } else if(!CONTAINS(srcFilePath, mVisitedPaths)){

                            mVisitedPaths.insert(srcFilePath);

                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            ImportVisitor visitor(mVisitedPaths, mContext);
                            visitor.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }
    }


    void ImportVisitor::visitImport(const string& import) {

        for(string searchPath : mContext->searchPaths) {
            if(sys::fs::is_directory(searchPath)) {

                string srcFilePath = searchPath + path_sep + import + ".stp";
                if(sys::fs::is_regular_file(srcFilePath)) {
                    ifstream inputFileStream(srcFilePath);
                    if (!inputFileStream) {
                        fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                    } else if(!CONTAINS(srcFilePath, mVisitedPaths)){

                        mVisitedPaths.insert(srcFilePath);

                        ParserContext parserContext(&inputFileStream);
                        yyparse(&parserContext);

                        ImportVisitor visitor(mVisitedPaths, mContext);
                        visitor.visit(parserContext.compileUnit);

                    }
                    break;
                }
            }
        }

    }


    void ImportVisitor::visit(NCompileUnit *compileUnit) {



        for(string import : compileUnit->includes) {
            visitImport(import);
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


}