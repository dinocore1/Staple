

#ifndef STAPLE_PARSERCONTEXT_H
#define STAPLE_PARSERCONTEXT_H


#include <iostream>
using namespace std;

namespace staple {

    class NCompileUnit;

    class ParserContext {
    public:
        void* scanner;
        istream* is;
        NCompileUnit* compileUnit;

        ParserContext(istream* is) : scanner(nullptr), is(is), compileUnit(nullptr) {
            init_scanner();

        }

        virtual ~ParserContext() {
            destroy_scanner();
        }

        int readBytes(char* buf, const int max) {
            is->read(buf, max);
            int bytesRead = is->gcount();
            return bytesRead;
        }

    protected:
        void init_scanner();
        void destroy_scanner();
    };
}

#endif //STAPLE_PARSERCONTEXT_H
