
set(CMAKE_CXX_COMPILER "g++")


execute_process(COMMAND llvm-config --cxxflags
        OUTPUT_VARIABLE llvm_cxxflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -D__STDC_LIMIT_MACROS -D__STDC_CONSTANT_MACROS")

#find_package(LLVM REQUIRED CONFIG)

#message(STATUS "Found LLVM ${LLVM_PACKAGE_VERSION}")
#message(STATUS "Using LLVMConfig.cmake in: ${LLVM_DIR}")

# Set your project compile flags.
# E.g. if using the C++ header files
# you will need to enable C++11 support
# for your compiler.

#include_directories(${LLVM_INCLUDE_DIRS})
#add_definitions(${LLVM_DEFINITIONS})

FIND_PACKAGE(BISON REQUIRED)
SET(BisonOutput ${CMAKE_CURRENT_SOURCE_DIR}/src/parser.cpp)
IF(BISON_FOUND)
    ADD_CUSTOM_COMMAND(
      OUTPUT ${BisonOutput}
      COMMAND ${BISON_EXECUTABLE}
                -d
                --output=${BisonOutput}
                ${CMAKE_CURRENT_SOURCE_DIR}/src/parser.y
      COMMENT "Generating parser.cpp"
    )
ENDIF()

FIND_PACKAGE(FLEX REQUIRED)
SET(FlexOutput ${CMAKE_CURRENT_SOURCE_DIR}/src/tokens.cpp)
IF(FLEX_FOUND)
    ADD_CUSTOM_COMMAND(
      OUTPUT ${FlexOutput}
      COMMAND ${FLEX_EXECUTABLE}
              --outfile=${FlexOutput}
              ${CMAKE_CURRENT_SOURCE_DIR}/src/tokens.l
      COMMENT "Generating tokens.cpp"
    )
ENDIF()

set(SOURCE_FILES
    src/compilercontext.cpp
    src/compilercontext.h
    src/main.cpp
    src/sempass.cpp
    src/node.h
    src/codegen/pointerscopepass.cpp
    src/codegen/pointerscopepass.h
    src/types/stapletype.h
    src/types/stapletype.cpp
    src/codegen/LLVMCodeGenerator.cpp
    src/codegen/LLVMStapleObject.cpp
    )

add_executable(stp ${FlexOutput} ${BisonOutput} ${SOURCE_FILES})


execute_process(COMMAND llvm-config --libs
        OUTPUT_VARIABLE llvm_libs
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )

execute_process(COMMAND llvm-config --ldflags
        OUTPUT_VARIABLE llvm_ldflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )


# Link against LLVM libraries
target_link_libraries(stp "${llvm_ldflags} ${llvm_libs} -ltinfo -ldl")