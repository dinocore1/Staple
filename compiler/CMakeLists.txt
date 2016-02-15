
execute_process(COMMAND llvm-config-3.5 --cxxflags
        OUTPUT_VARIABLE llvm_cxxflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )



set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(SOURCE_FILES
  src/FileUtils.cpp src/FileUtils.h
  src/staple_parser.cpp
  src/staple_lex.cpp
  src/Node.cpp src/Node.h
  src/ILGenerator.cpp src/ILGenerator.h
  src/CompilerContext.cpp src/CompilerContext.h
  src/main.cpp
  )

include_directories("/usr/lib/llvm-3.5/include")

add_executable(stp ${SOURCE_FILES})

execute_process(COMMAND llvm-config-3.5 --libs
        OUTPUT_VARIABLE llvm_libs
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )

execute_process(COMMAND llvm-config-3.5 --ldflags
        OUTPUT_VARIABLE llvm_ldflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )


# Link against LLVM libraries
target_link_libraries(stp "${llvm_ldflags} ${llvm_libs} -ltinfo -ldl -lpthread")