
#ifndef STAPLE_ILGENERATOR_H
#define STAPLE_ILGENERATOR_H


namespace staple {

class ILGenerator {
public:

    ILGenerator(Node* rootNode);
    void generate();

private:
    Node* mRootNode;
};

}

#endif //STAPLE_ILGENERATOR_H
