
.phony: dirs clean

all: dirs

ROOT := $(PWD)
OUT := $(ROOT)/bin

STP_JAR := $(OUT)/staple-compiler.jar

dirs:
	mkdir -p $(OUT)

clean:
	rm -rf $(OUT)

$(STP_JAR): dirs 
	cd compiler && mvn package
	cp compiler/target/Staple-1.0-SNAPSHOT.jar $(STP_JAR)

include build/staple.mk 

