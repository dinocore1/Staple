
#include <utils/linkedlist.h>

void utils_linkedlist_init(utils_linkedlist* list) {
	list->begin = NULL;
	list->end = NULL;
	list->listsize = 0;
}

void utils_linkedlist_add(utils_linkedlist* list, void* data, unsigned int len_bytes) {
	utils_linkedlist_node* newnode = (utils_linkedlist_node*)STP_ALLOC(sizeof(void*) + len_bytes);
	newnode->next = NULL;
	if(list->listsize == 0){
		list->begin = newnode;
		list->end = newnode;
	} else {
		list->end->next = newnode;
		list->end = newnode;
	}

	memcpy(&newnode->data, data, len_bytes);
	list->listsize++;
}

void utils_linkedlist_iterator_init(utils_linkedlist_iterator* it, utils_linkedlist* list) {
	it->currentPtr = list->begin;
}

BOOL utils_linkedlist_iterator_hasnext(utils_linkedlist_iterator* it) {
	return it->currentPtr != NULL;
}

void utils_linkedlist_iterator_next(utils_linkedlist_iterator* it, void** data) {
	*data = it->currentPtr->data;
	it->currentPtr = it->currentPtr->next;
}
