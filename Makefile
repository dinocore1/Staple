
.PHONY: all clean

ROOT := $(PWD)
OUT := bin

STP_JAR := $(OUT)/staple-compiler.jar

all: 


$(STP_JAR): 
	cd compiler && mvn package
	mkdir -p $(OUT)
	cp compiler/target/Staple-1.0-SNAPSHOT.jar $(STP_JAR)


clean:
	rm -rf $(OUT)

include build/staple.mk
