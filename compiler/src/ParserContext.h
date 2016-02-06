
#ifndef PARSER_CONTEXT_H_
#define PARSER_CONTEXT_H_

namespace staple {
	
class ParserContext {
	public:
	void* mScanner;
	int readBytes(char* buf, const int max);
	
};
	
} // namespace staple


#endif // PARSER_CONTEXT_H_