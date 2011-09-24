
#include <gc.h>

/*
 * A garbage collected pointer has the following data structor:
 *
 * 							1st byte
 *  +---------------------------------------------------------+
 *  |  GC_MARK 1-bit |          NUMFIELDS 7-bits              |
 *  +---------------------------------------------------------+
 *  |  FINALIZER_FLAG 1-bit | WRAPPER 1-bit | RESERVED 6-bits |
 *  +---------------------------------------------------------+
 *  |     RESERVED 8-bits                                     |
 *  +---------------------------------------------------------+
 *  |     RESERVED 8-bits                                     |
 *  +---------------------------------------------------------+
 *  |                                                         |
 *  |    Member Pointer Array sizeof(void*) * NUMFIELDS       |
 *  |                                                         |
 *  |                                                         |
 *  ?         ......           ........       .........       ?
 *
 *  |      Finalize function ptr (if FINALIZER_FLAG)          |
 *  +---------------------------------------------------------+
 */

#define MEMBERS(gc_object) ((void**)(((size_t)gc_object)+sizeof(gc_object_t)))
#define FINALIZER_FIELD(gc_object, numfields) ((finalize*)(((size_t)gc_object)+sizeof(gc_object_t)+sizeof(void*)*numfields))

#define GC_MARKED_MASK 0x80
#define GC_NUMFIELDS 0x7f
#define GC_FINALIZER_MASK 0x80
#define GC_WRAPPER_MASK 0x40

typedef struct gc_object_t {
	uint8_t r1;
	uint8_t r2;
	uint8_t r3[2];
} gc_object_t;


typedef struct gc_wrapper_object_t {
	gc_object_t header;
	void* ptr;
	finalize finalizer;

} gc_wrapper_object_t;


size_t heapBytes = 0;
utils_linkedlist all_gc_objects;
utils_linkedlist alive_gc_static_objects;

void gc_init() {
	utils_linkedlist_init(&alive_gc_static_objects);
	utils_linkedlist_init(&all_gc_objects);
}

void gc_getStats(gc_stats* stats) {
	stats->heapBytes = heapBytes;
	stats->numObjects = all_gc_objects.listsize;
}

void* gc_malloc(uint8_t numMembers, finalize finalize_callback) {

	size_t totalsize = sizeof(gc_object_t) + sizeof(void*)*numMembers;
	if(finalize_callback != NULL){
		totalsize += sizeof(finalize);
	}

	void* retval = STP_ALLOC(totalsize);
	memset(retval, 0, totalsize);

	gc_object_t* gcobject = (gc_object_t*)retval;

	gcobject->r1 |= numMembers & GC_NUMFIELDS;

	if(finalize_callback != NULL){
		gcobject->r2 |= GC_FINALIZER_MASK;
		finalize* f = FINALIZER_FIELD(gcobject, numMembers);
		f[0] = finalize_callback;
	}


	utils_linkedlist_add(&all_gc_objects, &retval, sizeof(void*));

	heapBytes += totalsize;

	return retval;
}

void gc_wrapper(void* ptr, finalize finalize_callback){

	size_t totalsize = sizeof(gc_object_t) + sizeof(void*);
	if(finalize_callback != NULL){
		totalsize += sizeof(finalize);
	}

	void* retval = STP_ALLOC(totalsize);
	memset(retval, 0, totalsize);

	gc_wrapper_object_t* gcobject = (gc_wrapper_object_t*)retval;

	gcobject->header.r2 |= GC_WRAPPER_MASK;
	gcobject->ptr = ptr;

	if(finalize_callback != NULL){
		gcobject->header.r2 |= GC_FINALIZER_MASK;
		gcobject->finalizer = finalize_callback;
	}

	utils_linkedlist_add(&all_gc_objects, &retval, sizeof(void*));

	heapBytes += totalsize;
}

void gc_setMember(void* gc_object, uint8_t memberIndex, void* gc_member) {
	void** member = MEMBERS(gc_object);

#ifdef SAFTY_CHECK
	//ensure the memberIndex < objects's NUMFIELDS
	gc_object_t* obj = gc_object;
	if(memberIndex >= obj->r1 & GC_NUMFIELDS){
		return;
	}
#endif
	member[memberIndex] = gc_member;
}

void* gc_getMember(void* gc_object, uint8_t memberIndex) {
	void** member = MEMBERS(gc_object);

#ifdef SAFTY_CHECK
	//ensure the memberIndex < objects's NUMFIELDS
	gc_object_t* obj = gc_object;
	if(memberIndex >= obj->r1 & GC_NUMFIELDS){
		return NULL;
	}
#endif

	return member[memberIndex];
}

void gc_addstatic(void* gc_object) {
	if(gc_object != NULL){
		//add the pointer to the list of alive objects
		utils_linkedlist_add(&alive_gc_static_objects, &gc_object, sizeof(void*));
	}
}

typedef void(*visit_cb)(gc_object_t* obj);

void visit_obj(gc_object_t* obj, visit_cb callback){
	if(obj != NULL){
		callback(obj);
		void** member = MEMBERS(obj);
		//now visit all children
		uint8_t i;
		uint8_t numMember = obj->r1 & GC_NUMFIELDS;
		for(i=0; i<numMember;i++){
			visit_obj((gc_object_t*)member[i], callback);
		}
	}
}

void mark_alive(gc_object_t* obj) {
	obj->r1 |= GC_MARKED_MASK;
}

size_t calcSize(gc_object_t* obj) {
	size_t retval = sizeof(gc_object_t) + sizeof(void*) * (obj->r1 & GC_NUMFIELDS);
	if(obj->r2 & GC_FINALIZER_MASK){
		retval += sizeof(finalize);
	}
	return retval;
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
	utils_linkedlist_iterator_init(&it, &all_gc_objects);
	while(utils_linkedlist_iterator_hasnext(&it)){
		utils_linkedlist_iterator_next(&it, &obj);
		if(!(obj->r1 & GC_MARKED_MASK)) {
			//recycle
			if(obj->r2 & GC_FINALIZER_MASK) {
				if(obj->r2 & GC_WRAPPER_MASK){
					gc_wrapper_object_t* wrapper = (gc_wrapper_object_t*)obj;
					wrapper->finalizer(wrapper->ptr);
				} else {
					finalize* f = FINALIZER_FIELD(obj, (obj->r1 & GC_NUMFIELDS));
					f[0](obj);
				}
			}
			utils_linkedlist_iterator_remove(&it);
			heapBytes -= calcSize(obj);
			STP_FREE(obj);

		} else {
			obj->r1 &= ~GC_MARKED_MASK;
		}
	}
}
