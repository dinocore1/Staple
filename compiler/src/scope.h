

#ifndef STAPLE_SCOPE_H
#define STAPLE_SCOPE_H

#include <string>
#include <map>

#include "types/stapletype.h"

namespace staple {

    class Scope {
    public:
        Scope* parent;
        std::map<string, StapleType*> table;

        Scope() : parent(nullptr) {}
        Scope(Scope* parent) : parent(parent) {}

        StapleType* get(const string& name) const {
            StapleType* retval = nullptr;

            auto it = table.find(name);
            if(it != table.end()) {
                retval = it->second;
            } else if(parent != NULL) {
                retval = parent->get(name);
            }

            return retval;
        }
    };

}


#endif //STAPLE_SCOPE_H
