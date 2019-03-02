
execute_process(COMMAND llvm-config --cxxflags
        OUTPUT_VARIABLE llvm_cxxflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )

FIND_PACKAGE(BISON REQUIRED)
SET(BisonOutput ${CMAKE_CURRENT_SOURCE_DIR}/src/staple_parser.cpp)
SET(BisonInput ${CMAKE_CURRENT_SOURCE_DIR}/src/staple_parser.y)
IF(BISON_FOUND)
    ADD_CUSTOM_COMMAND(
      OUTPUT ${BisonOutput} ${CMAKE_CURRENT_SOURCE_DIR}/src/staple_parser.hpp
      MAIN_DEPENDENCY ${BisonInput}
      COMMAND ${BISON_EXECUTABLE}
                -d
                --output=${BisonOutput}
                ${BisonInput}
      COMMENT "Generating staple_parser.cpp"
    )
ENDIF()

FIND_PACKAGE(FLEX REQUIRED)
SET(FlexOutput ${CMAKE_CURRENT_SOURCE_DIR}/src/staple_lex.cpp)
SET(FlexInput ${CMAKE_CURRENT_SOURCE_DIR}/src/staple_lex.l)
IF(FLEX_FOUND)
    ADD_CUSTOM_COMMAND(
      OUTPUT ${FlexOutput}
      MAIN_DEPENDENCY ${FlexInput}
      COMMAND ${FLEX_EXECUTABLE}
              --outfile=${FlexOutput}
              ${FlexInput}
      COMMENT "Generating staple_lex.cpp"
    )
ENDIF()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 ${llvm_cxxflags} -O0 -g")
set(SOURCE_FILES
  src/staple_lex.cpp
  src/staple_parser.cpp
  src/FileUtils.cpp src/FileUtils.h
  src/Utils.cpp
  src/Node.cpp src/Node.h
  src/Type.cpp
  src/Sempass.cpp
  src/ILGenerator.cpp src/ILGenerator.h
  src/CompilerContext.cpp src/CompilerContext.h
  src/main.cpp
  )

include_directories("/usr/lib/llvm/include")

add_executable(stp ${SOURCE_FILES})

execute_process(COMMAND llvm-config --libs
        OUTPUT_VARIABLE llvm_libs
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )

execute_process(COMMAND llvm-config --ldflags
        OUTPUT_VARIABLE llvm_ldflags
        OUTPUT_STRIP_TRAILING_WHITESPACE
        )


# Link against LLVM libraries
target_link_libraries(stp "${llvm_ldflags} ${llvm_libs} -ltinfo -ldl -lpthread")