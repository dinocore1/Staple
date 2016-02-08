
#include "stdafx.h"

using namespace staple;

int main(int argc, char** argv) {

    ParserContext ctx;

    printf("parse: %d", ctx.parse(argv[1]));
    ILGenerator ilGenerator(ctx.rootNode);
    ilGenerator.generate();


}