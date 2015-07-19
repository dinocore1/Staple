

#ifndef STAPLE_PARSERCONTEXT_H
#define STAPLE_PARSERCONTEXT_H

#include <string>
#include <iostream>

using namespace std;

namespace staple {

    class NCompileUnit;

    class ParserContext {
    private:
        std::istream* mInputStream;
        std::string mStreamName;
        bool mSuccess;

    public:
        void* mScanner;
        NCompileUnit* compileUnit;

        ParserContext();
        virtual ~ParserContext();

        bool parse(const std::string& filePath);
        bool parse(const std::string& streamName, std::istream& is);
        void parseError(const int line, const int column, const char* errMsg);

        int readBytes(char* buf, const int max);

    protected:
        void init_scanner();
        void destroy_scanner();
    };
}

#endif //STAPLE_PARSERCONTEXT_H
