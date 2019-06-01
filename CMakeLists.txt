cmake_minimum_required(VERSION 3.4.3)
list(APPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_LIST_DIR}/cmake")
include(DownloadProject)

project(Staple)

set (Staple_VERSION_MAJOR 1)
set (Staple_VERSION_MINOR 0)

option(BUILD_TESTS "Build unit tests" ON)

find_package(LLVM REQUIRED CONFIG)

message(STATUS "Found LLVM ${LLVM_PACKAGE_VERSION}")
message(STATUS "Using LLVMConfig.cmake in: ${LLVM_DIR}")

if(BUILD_TESTS)
    download_project(PROJ               googletest
                    GIT_REPOSITORY      https://github.com/google/googletest.git
                    GIT_TAG             release-1.8.0
    )
    add_subdirectory(${googletest_SOURCE_DIR} ${googletest_BINARY_DIR})
endif()

include_directories(${LLVM_INCLUDE_DIRS})
add_definitions(${LLVM_DEFINITIONS})

add_subdirectory("compiler")
