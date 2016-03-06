
#include "stdafx.h"

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wcast-qual"
#include "optionparser.h"
#pragma GCC diagnostic pop

using namespace staple;

static void printError(const char* msg1, const option::Option& opt, const char* msg2) {
  fprintf(stderr, "ERROR: %s", msg1);
  fwrite(opt.name, opt.namelen, 1, stderr);
  fprintf(stderr, "%s", msg2);
}

static option::ArgStatus NonEmpty(const option::Option& option, bool msg) {
  if(option.arg != 0 && option.arg[0] != 0) {
    return option::ARG_OK;
  }

  if(msg) {
    printError("Option '", option, "' requires a non-empty argument\n");
  }
  return option::ARG_ILLEGAL;
}

enum  optionIndex { UNKNOWN, HELP, GEN_DEBUG, INCLUDE_DIR };
const option::Descriptor usage[] = {
  {
    UNKNOWN, 0,"" , ""    ,option::Arg::None, "USAGE: stp [options] file.stp\n\n"
    "Options:"
  },
  {GEN_DEBUG,    0,"g" , "debug",option::Arg::None, "  -g  \tGenerate debug symbols." },
  {INCLUDE_DIR,    0,"I", "",NonEmpty, "  -I  \tInclude directory." },
  {HELP, 0, "", "help", option::Arg::None, "  --help \tprint help"},
  {
    UNKNOWN, 0,"" ,  ""   ,option::Arg::None, "\nExample:\n"
    "  stp -g -I path/to/inclue test.stp\n"
  },
  {0,0,0,0,0,0}
};


int main(int argc, char** argv) {
  argc-=(argc>0);
  argv+=(argc>0); // skip program name argv[0] if present
  option::Stats  stats(usage, argc, argv);
  option::Option options[stats.options_max], buffer[stats.buffer_max];
  option::Parser parse(usage, argc, argv, options, buffer);

  if(parse.error()) {
    return 1;
  }

  if(options[HELP] || argc == 0) {
    option::printUsage(std::cout, usage);
    return 1;
  }

  if(parse.nonOptionsCount() == 0) {
    fprintf(stderr, "ERROR: no compile file entered");
    return 1;
  }
  for(int i = 0; i < parse.nonOptionsCount(); ++i) {
    CompilerContext ctx;
    ctx.generateDebugSymobols = options[GEN_DEBUG];

    for(option::Option* opt = options[INCLUDE_DIR]; opt; opt = opt->next()) {
      ctx.addIncludeDir(opt->arg);
    }

    ctx.setInputFile(parse.nonOption(i));

    if(!ctx.parse()){
      return 1;
    }

    ILGenerator ilGenerator(&ctx);
    ilGenerator.generate();

  }



}
