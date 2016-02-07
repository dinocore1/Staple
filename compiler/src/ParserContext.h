
#ifndef PARSER_CONTEXT_H_
#define PARSER_CONTEXT_H_

namespace staple {

class ParserContext {
	public:
	ParserContext();
	virtual ~ParserContext();
	
	void* mScanner;
	int readBytes(char* buf, const int max);

	bool parse(const File& file);
	bool parse(const std::string& filepath);
	bool parse(const std::string& streamName, std::istream& is);
	void parseError(const int line, const int column, const char* errMsg);

	Node* rootNode;

protected:
  void init_scanner();
  void destroy_scanner();

private:
	std::istream* mInputStream;
	std::string mStreamName;
	bool mSuccess;

};

} // namespace staple


#endif // PARSER_CONTEXT_H_
