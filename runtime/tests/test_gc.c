
#include <staple.h>
#include <gc.h>

void my_finalizer(void* ptr) {
	printf("0x%x freed\n", ptr);
}

int main() {
	int retval = 0;
	gc_stats stats;

	gc_init();

	void* obj1 = gc_malloc(3, my_finalizer);
	printf("0x%x alloc\n", obj1);
	gc_getStats(&stats);
	printf("heap size: %d bytes, %d objects\n", stats.heapBytes, stats.numObjects);

	gc_addstatic(obj1);

	void* obj2 = gc_malloc(1, my_finalizer);
	printf("0x%x alloc\n", obj2);
	gc_getStats(&stats);
	printf("heap size: %d bytes, %d objects\n", stats.heapBytes, stats.numObjects);

	gc_setMember(obj1, 0, obj2);

	//call garbage collect. No objects should be recycled
	gc();

	gc_getStats(&stats);
	printf("heap size: %d bytes, %d objects\n", stats.heapBytes, stats.numObjects);

	//detach obj2 from obj1. Now obj2 should be recycled
	gc_setMember(obj1, 0, NULL);
	gc();

	gc_getStats(&stats);
	printf("heap size: %d bytes, %d objects\n", stats.heapBytes, stats.numObjects);

	if(stats.numObjects != 1){
		printf("GC test failed\n");
		retval = 1;
	}

	return retval;
}
