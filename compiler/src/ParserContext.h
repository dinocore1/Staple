
#ifndef PARSER_CONTEXT_H
#define PARSER_CONTEXT_H

namespace staple {
	
class ParserContext {
	public:
	void* mScanner;
	int readBytes(char* buf, const int max);
	
};
	
} // namespace staple


#endif // PARSER_CONTEXT_H