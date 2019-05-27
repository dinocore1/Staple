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

