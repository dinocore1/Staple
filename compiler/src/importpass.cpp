
#include <fstream>

#include "importpass.h"
#include "typehelper.h"

#include <llvm/Support/FileSystem.h>

#define path_sep '/'

using namespace std;
using namespace llvm;

namespace staple {

#define CONTAINS(x, container) (container.find(x) != container.end())


    void Pass1ClassVisitor::visit(NCompileUnit* compileUnit) {

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
                        } else if (!CONTAINS(srcFilePath, mContext->mFilesParsed)) {

                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            mContext->mFilesParsed.insert(srcFilePath);

                            size_t pos = import.find_last_of('.');
                            compileUnit->usingNamespaces.insert(import.substr(0, pos));

                            Pass1ClassVisitor pass1(mContext);
                            pass1.visit(parserContext.compileUnit);

                            Pass2ClassVisitor pass2(mContext);
                            pass2.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }

        for(NFunctionPrototype* functionPrototype : compileUnit->externFunctions) {
            const string fqFunctionName = functionPrototype->name;

            if(CONTAINS(fqFunctionName, mContext->mRootScope.table)) {
                mContext->logError(functionPrototype->location, "redefination of function '%s'", functionPrototype->name.c_str());
            } else {
                StapleFunction* function = new StapleFunction(fqFunctionName);
                mContext->mRootScope.table[fqFunctionName] = function;
                mFQFunctions.insert(fqFunctionName);
            }

        }

        for(NFunction* functionDecl : compileUnit->functions) {
            string fqFunctionName = !compileUnit->package.empty() ? (compileUnit->package + "." + functionDecl->name)
                                                           : functionDecl->name;

            if(CONTAINS(fqFunctionName, mContext->mRootScope.table)) {
                mContext->logError(functionDecl->location, "redefination of function '%s'", functionDecl->name.c_str());
            } else {
                StapleFunction* function = new StapleFunction(fqFunctionName);
                mContext->mRootScope.table[fqFunctionName] = function;
                mFQFunctions.insert(fqFunctionName);
            }
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;

            if(CONTAINS(fqClassName, mContext->mRootScope.table)) {
                mContext->logError(classDeclaration->location, "redefination of class '%s'", classDeclaration->name.c_str());
            } else {
                StapleClass *stpClass = new StapleClass(fqClassName);
                mContext->mRootScope.table[fqClassName] = stpClass;
                mFQClasses.insert(fqClassName);
            }
        }


    }

    ///// Pass2ClassVisitor ////

#define CheckType(type, location, name, positive) \
if(type == NULL) { \
    mContext->logError(location, "undefined type: '%s'", name.c_str()); \
} else { \
    positive \
}

    set<string> Pass2ClassVisitor::mPass2VisitedPaths;

    void Pass2ClassVisitor::visit(NField* field) {
        StapleType* stpType = getType(&field->type);
        CheckType(stpType, field->location, field->name,
                  StapleField* stpField = mCurrentClass->addField(field->name, stpType);
                  mType[field] = stpField;
                  if(mContext->mCompileUnit == mCompileUnit){
                      mContext->typeTable[field] = stpField;
                  }
        )
    }

    void Pass2ClassVisitor::visit(NArgument* argument) {
        StapleType* stpType = getType(&argument->type);
        if(stpType != nullptr) {
            mType[argument] = stpType;
            if (mContext->mCompileUnit == mCompileUnit) {
                mContext->typeTable[argument] = stpType;
            }
        }
    }

    void Pass2ClassVisitor::visit(NType* type) {
        StapleType* retType = getStapleType(type, mContext, mCompileUnit, mContext->mRootScope);
        mType[type] = retType;
        if(mContext->mCompileUnit == mCompileUnit){
            mContext->typeTable[type] = retType;
        }
    }

    void Pass2ClassVisitor::visit(NCompileUnit* compileUnit) {

        mCompileUnit = compileUnit;

        for(NFunctionPrototype* functionPrototype : compileUnit->externFunctions) {
            functionPrototype->accept(this);
        }

        for(NFunction* function : compileUnit->functions) {
            function->accept(this);
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);

            for(NField* field : classDeclaration->fields){
                field->accept(this);
            }

            for(NMethodFunction* methodFunction : classDeclaration->functions) {
                methodFunction->accept(this);
            }
        }

    }

    void Pass2ClassVisitor::visit(NFunctionPrototype* functionPrototype) {
        string fqFunctionName = functionPrototype->name;
        StapleFunction* function = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

        std::vector<StapleType*> args;
        for(NArgument* arg : functionPrototype->arguments) {
            StapleType* type = getType(arg);
            args.push_back(type);
        }
        function->setArgumentTypes(args, functionPrototype->isVarg);

        StapleType* returnType = getType(&functionPrototype->returnType);
        CheckType(returnType, functionPrototype->returnType.location, functionPrototype->returnType.name,
                  function->setReturnType(returnType);)

        mContext->typeTable[functionPrototype] = function;
    }

    void Pass2ClassVisitor::visit(NFunction* functionDecl) {
        string fqFunctionName = !mCompileUnit->package.empty() ? (mCompileUnit->package + "." + functionDecl->name)
                                                                             : functionDecl->name;
        StapleFunction* function = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

        std::vector<StapleType*> args;
        for(NArgument* arg : functionDecl->arguments) {
            StapleType* type = getType(arg);
            args.push_back(type);
        }

        function->setArgumentTypes(args, functionDecl->isVarg);

        StapleType* returnType = getType(&functionDecl->returnType);
        CheckType(returnType, functionDecl->returnType.location, functionDecl->returnType.name,
                  function->setReturnType(returnType);)

        mContext->typeTable[functionDecl] = function;
    }

    void Pass2ClassVisitor::visit(NMethodFunction* methodFunction) {

        std::vector<StapleType*> args;
        for(NArgument* arg : methodFunction->arguments){
            StapleType* type = getType(&arg->type);
            args.push_back(type);
        }

        StapleType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        StapleMethodFunction* stpMethod = mCurrentClass->addMethod(methodFunction->name, returnType, args, methodFunction->isVarg);

        if(mContext->mCompileUnit == mCompileUnit) {
            mContext->typeTable[methodFunction] = stpMethod;
        }
    }

}