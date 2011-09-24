
#include <staple.h>

#include <utils/linkedlist.h>

typedef void(*finalize)(void* ptr);

typedef struct gc_ptr {
	void* ptr;
	uint32_t internal_data;
	void** members;
} memptr;

void gc_init();

/**
 * create a new object with <code>numMembers</code>
 * @param numMembers the number of member pointers this new object has.
 * note: numMembers can have a min of 1 and a max of 64.
 */
void* gc_malloc(uint8_t numMembers);

/**
 * Wrap a pointer to an object. The garbage collector will now manage
 * this pointer. finalize_callback will be called (if not NULL) when this
 * object should be recycled.
 */
void gc_wrapper(void* ptr, finalize finalize_callback);

/**
 * Sets an gc_object's member
 * @param gc_object the object
 * @param memberIndex the index of the member to set
 * @param gc_member the member to assign to the <code>memberIndex</code>'s member.
 * Note: gc_member should have previously been created with either gc_malloc or gc_wrapper
 * @return success
 */
BOOL gc_setMember(void* gc_object, uint8_t memberIndex, void* gc_member);


void gc_addstatic(void* gc_object);

/*
 * Run the garbage collector
 */
void gc();
