
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

set(STP_CORE_FILES
  src/staple_lex.cpp
  src/staple_parser.cpp
  src/FileUtils.cpp
  src/Utils.cpp
  src/Node.cpp
  src/Type.cpp
  src/Sempass.cpp
  src/ILGenerator.cpp
  src/CompilerContext.cpp
  )


include_directories(src)

add_library(stp_core STATIC ${STP_CORE_FILES})
target_compile_features(stp_core
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
target_link_libraries(stp_core ${llvm_libs})

add_executable(stp src/main.cpp)
target_link_libraries(stp stp_core)

if(BUILD_TESTS)
  add_executable(compiler_tests
    "tests/utils_tests.cpp"
  )
  target_link_libraries(compiler_tests stp_core gtest gmock_main)
  add_test(NAME compilter_tests COMMAND compiler_tests)
endif()