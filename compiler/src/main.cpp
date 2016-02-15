
#include "stdafx.h"

using namespace staple;

int main(int argc, char** argv) {


    CompilerContext ctx;
    //ctx.generateDebugSymobols = true;
    ctx.setInputFile(argv[1]);

    ctx.parse();

    ILGenerator ilGenerator(&ctx);
    ilGenerator.generate();


}
