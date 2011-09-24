
#include <staple.h>


typedef struct utils_linkedlist_node {
	struct utils_linkedlist_node* next;
	void* data;
} utils_linkedlist_node;


typedef struct utils_linkedlist {
	utils_linkedlist_node* begin;
	utils_linkedlist_node* end;
	unsigned int listsize;
} utils_linkedlist;

typedef struct utils_linkedlist_iterator {
	utils_linkedlist_node* currentPtr;
} utils_linkedlist_iterator;


/**
 * Create new linkedlist.
 * @param list list to be initialized
 */
void utils_linkedlist_init(utils_linkedlist* list);

/**
 * Adds a new item to the end of the list. This method will copy <code>len_bytes</code>
 * bytes of data from <code>data</code> and add to the end of the list.
 * @list the linkedlist
 * @data pointer to the item's data.
 * @len_bytes number of bytes to copy from <code>pointer</code>
 */
void utils_linkedlist_add(utils_linkedlist* list, void* data, unsigned int len_bytes);

/**
 * Initialize a iterator to the begining of the list
 */
void utils_linkedlist_iterator_init(utils_linkedlist_iterator* it, utils_linkedlist* list);

/**
 *
 */
BOOL utils_linkedlist_iterator_hasnext(utils_linkedlist_iterator* it);

void utils_linkedlist_iterator_next(utils_linkedlist_iterator* it, void** data);


