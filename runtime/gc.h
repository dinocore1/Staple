
#include <staple.h>

#include <utils/linkedlist.h>

typedef struct gc_stats {
	size_t heapBytes;
	size_t numObjects;
} gc_stats;

typedef void(*finalize)(void* ptr);

typedef struct gc_ptr {
	void* ptr;
	uint32_t internal_data;
	void** members;
} memptr;

void gc_init();


void gc_getStats(gc_stats* stats);

/**
 * create a new object with <code>numMembers</code>
 * @param numMembers the number of member pointers this new object has.
 * note: numMembers can have a min of 1 and a max of 128.
 * @param finalize_callback finalize function called when this object
 * is to be recycled
 */
void* gc_malloc(uint8_t numMembers, finalize finalize_callback);

/**
 * Wrap a pointer to a non-gc object. The garbage collector will now manage
 * this pointer. finalize_callback will be called (if not NULL) when this
 * object should be recycled.
 * @param ptr pointer to a non-garabage collected object that will now be mannaged by
 * the garabage collector
 * @param finalize_callback finalize function called when this object
 * is to be recycled
 */
void gc_wrapper(void* ptr, finalize finalize_callback);

/**
 * Sets a gc_object's member
 * @param gc_object the object
 * @param memberIndex the index of the member to set
 * @param gc_member the member to assign to the <code>memberIndex</code>'s member.
 * Note: gc_member should have previously been created with either gc_malloc or gc_wrapper
 */
void gc_setMember(void* gc_object, uint8_t memberIndex, void* gc_member);

/**
 * Get a gc_object's member
 * @param gc_object the object
 * @param memberIndex the index of the member to get
 * @return a pointer to the gc_object
 */
void* gc_getMember(void* gc_object, uint8_t memberIndex);

/**
 * Add a gc_object to the list of static objects.
 * This object and all of its recursive members will
 * always be kept alive.
 */
void gc_addstatic(void* gc_object);

/*
 * Run the garbage collector
 */
void gc();
