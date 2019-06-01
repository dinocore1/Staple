#include <gtest/gtest.h>

#include "Utils.h"

using namespace staple;

TEST(FQPath, parse_trivial)
{
    FQPath path("com");
    EXPECT_EQ(1, path.getNumParts());
    EXPECT_EQ(0, path.part(0).compare("com"));
}

TEST(FQPath, parse_name_correctly)
{
    FQPath path("com.devsmart.cool");
    EXPECT_EQ(3, path.getNumParts());
    EXPECT_EQ(0, path.part(0).compare("com"));
    EXPECT_EQ(0, path.part(1).compare("devsmart"));
    EXPECT_EQ(0, path.part(2).compare("cool"));
}

TEST(FQPath, getFullString)
{
    FQPath path("com.devsmart.cool");
    EXPECT_EQ(0, path.getFullString().compare("com.devsmart.cool"));
}

TEST(FQPath, getPackageName)
{
    FQPath path("com.devsmart.cool");
    EXPECT_EQ(0, path.getPackageName().compare("com.devsmart"));
}

TEST(FQPath, getSimpleName)
{
    FQPath path("com.devsmart.cool");
    EXPECT_EQ(0, path.getSimpleName().compare("cool"));
}

TEST(FQPath, less_than_operator_works)
{
    FQPath a("a.b.c");
    FQPath b("a.b.a");

    EXPECT_TRUE(b < a);
    EXPECT_FALSE(a < b);
}

TEST(FQPath, shorter_path_less)
{
    FQPath a("a.b.c");
    FQPath b("a.b");

    EXPECT_TRUE(b < a);
    EXPECT_FALSE(a < b);
}

TEST(FQPath, path_equal)
{
    FQPath a("a.b.c");
    FQPath b("a.b.c");

    EXPECT_FALSE(b < a);
    EXPECT_FALSE(a < b);
}

TEST(File, local_absolutepath)
{
    char pathBuf[FILENAME_MAX];
    getcwd(pathBuf, FILENAME_MAX);

    File f(".");

    EXPECT_EQ(std::string(pathBuf), f.getAbsolutePath());
}

TEST(File, local_child)
{
    File f(".");
    File f1(f, "cool.txt");

    char pathBuf[FILENAME_MAX];
    getcwd(pathBuf, FILENAME_MAX);

    EXPECT_EQ(std::string( pathBuf) + std::string("/cool.txt"), f1.getAbsolutePath());
}

