
#include <staple.h>
#include <gc.h>

int main() {
	int retval = 0;

	void* obj1 = gc_malloc(3);
	void* obj2 = gc_malloc(1);

	gc_setMember(obj1, 0, obj2);

	return retval;
}
