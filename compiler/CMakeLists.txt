set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(SOURCE_FILES
  src/FileUtils.cpp
  src/staple_parser.cpp
  src/staple_lex.cpp
  src/Node.cpp
  src/main.cpp
  )

add_executable(stp ${SOURCE_FILES})
