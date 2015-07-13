#include "stringutils.h"

namespace staple {

    bool strStartWith(const std::string &string, const std::string &prefix) {
        return string.compare(0, prefix.length(), prefix) == 0;
    }
}
