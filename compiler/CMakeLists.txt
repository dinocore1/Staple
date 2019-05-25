
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

add_executable(stp ${SOURCE_FILES})

target_compile_features(stp
    PRIVATE
        cxx_auto_type
        cxx_lambdas
        cxx_variadic_templates
        cxx_nullptr
)

# Find the libraries that correspond to the LLVM components
# that we wish to use
llvm_map_components_to_libnames(llvm_libs support core irreader codegen)

# Link against LLVM libraries
target_link_libraries(stp ${llvm_libs})