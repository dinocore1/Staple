
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
	it->list = list;
	it->currentPtr = NULL;
	it->lastPtr = list->begin;
}

BOOL utils_linkedlist_iterator_hasnext(utils_linkedlist_iterator* it) {
	if(it->currentPtr == NULL){
		return it->lastPtr != NULL;
	} else {
		return it->currentPtr->next != NULL;
	}
}

void utils_linkedlist_iterator_next(utils_linkedlist_iterator* it, void** data) {
	if(it->currentPtr == NULL){
		it->currentPtr = it->lastPtr;
		*data = it->currentPtr->data;
	} else {
		it->lastPtr = it->currentPtr;
		it->currentPtr = it->currentPtr->next;
		*data = it->currentPtr->data;
	}
}

void utils_linkedlist_iterator_remove(utils_linkedlist_iterator* it) {
	utils_linkedlist_node* rmnode = it->currentPtr;
	it->lastPtr->next = it->lastPtr->next != NULL ? it->lastPtr->next->next : NULL;
	if(rmnode == it->list->begin){
		it->list->begin = NULL;
	}
	if(rmnode == it->list->end) {
		it->list->end = it->lastPtr;
	}
	STP_FREE(rmnode);
	it->list->listsize--;
}
