
#include <gc.h>

/*
 * A garbage collected pointer has the following data structor:
 *
 * 							1st byte
 *  +---------------------------------------------------------+
 *  |  GC_MARK 1-bit |  IS_WRAPPER 1-bit |  NUMFIELDS 6-bits  |
 *  +---------------------------------------------------------+
 *  |     RESERVED 8-bits                                     |
 *  +---------------------------------------------------------+
 *  |     RESERVED 8-bits                                     |
 *  +---------------------------------------------------------+
 *  |     RESERVED 8-bits                                     |
 *  +---------------------------------------------------------+
 *  |                                                         |
 *  |                                                         |
 *  |    Member Pointer Array sizeof(void*) * NUMFIELDS       |
 *  |                                                         |
 *  |                                                         |
 *
 */

#define GC_MARKED_FLAG 0x80
#define GC_WRAPPER_FLAG 0x40
#define GC_NUMFIELDS 0x3f

typedef struct gc_object_t {
	uint8_t info;
	uint8_t reserved[3];

} gc_object_t;

typedef struct gc_wrapper_obj_t {
	gc_object_t header;
	finalize finalize_callback;
} gc_wrapper_obj_t;

utils_linkedlist all_gc_objects;
utils_linkedlist alive_gc_static_objects;

void gc_init() {
	utils_linkedlist_init(&alive_gc_static_objects);
	utils_linkedlist_init(&all_gc_objects);
}

void* gc_malloc(uint8_t numMembers) {
	size_t totalsize = sizeof(gc_object_t) + sizeof(void*)*numMembers;
	void* retval = STP_ALLOC(totalsize);
	gc_object_t* gcobject = (gc_object_t*)retval;
	gcobject->info = GC_NUMFIELDS & numMembers;
	memset(gcobject->reserved, 0, 3 + sizeof(void*) * numMembers);

	utils_linkedlist_add(&all_gc_objects, retval, totalsize);

	return retval;
}

void gc_wrapper(void* ptr, finalize finalize_callback){
	size_t totalsize = sizeof(gc_wrapper_obj_t);
	gc_wrapper_obj_t* gc_wrapper = (gc_wrapper_obj_t*)STP_ALLOC(totalsize);
	gc_wrapper->header.info = GC_WRAPPER_FLAG;
	memset(gc_wrapper->header.reserved, 0, 3);
	gc_wrapper->finalize_callback = finalize_callback;

	utils_linkedlist_add(&all_gc_objects, gc_wrapper, totalsize);

}

BOOL gc_setMember(void* gc_object, uint8_t memberIndex, void* gc_member) {
	void** member = (void**)(((size_t)gc_object)+sizeof(gc_object_t));

#ifdef SAFTY_CHECK
	//ensure the memberIndex < objects's NUMFIELDS
	gc_object_t* obj = gc_object;
	if(memberIndex >= obj->info & GC_NUMFIELDS){
		return FALSE;
	}
#endif
	member[memberIndex] = gc_member;
	return TRUE;
}

typedef void(*visit_cb)(gc_object_t* obj);

void visit_obj(gc_object_t* obj, visit_cb callback){
	if(obj != NULL){
		callback(obj);
		void** member = (void**)((void*)obj)+sizeof(gc_object_t);
		//now visit all children
		uint8_t i;
		uint8_t numMember = obj->info & GC_NUMFIELDS;
		for(i=0; i<numMember;i++){
			visit_obj((gc_object_t*)member[i], callback);
		}
	}
}

void mark_alive(gc_object_t* obj) {
	obj->info |= GC_MARKED_FLAG;
}

void gc() {

	gc_object_t* obj;

	//mark all alive objects
	utils_linkedlist_iterator it;
	utils_linkedlist_iterator_init(&it, &alive_gc_static_objects);
	while(utils_linkedlist_iterator_hasnext(&it)){
		utils_linkedlist_iterator_next(&it, &obj);
		visit_obj(obj, mark_alive);
	}

	//sweep
}
