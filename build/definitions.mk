
CC:=gcc
CPP:=gcc -E -x c -P -C
LCC:=llc

#%.stpp : %.stp
#	$(CPP) $(STP_INCLUDES) $< > $@
#
#%.ll : %.stpp $(STP_JAR)
#	java -jar $(STP_JAR) $< > $@
#
#%.s : %.ll
#	$(LCC) $(LCC_CFLAGS) $< -o $@
#
#%.o : %.s
#	$(CC) -c $< -o $@

stp_file = $(LOCAL_PATH)/$(stp_src)
stpp_file = $(LOCAL_OUTPUT_DIR)/$(stp_src:%.stp=%.stpp)
ll_file = $(LOCAL_OUTPUT_DIR)/$(stp_src:%.stp=%.ll)
s_file = $(LOCAL_OUTPUT_DIR)/$(stp_src:%.stp=%.s)

define output-dir
$(LOCAL_OUTPUT_DIR_PATH):
	@echo "making $(LOCAL_OUTPUT_DIR)"
	$(SILENT)mkdir -p $(LOCAL_OUTPUT_DIR)
	$(SILENT)touch $(LOCAL_OUTPUT_DIR_PATH)
endef

define stp-to-stpp
$(stpp_file): $(LOCAL_OUTPUT_DIR_PATH) $(stp_file) 
	$(CPP) $(STP_INCLUDES) $(stp_file) > $(stpp_file)

endef

define stpp-to-ll
$(ll_file): $(STP_JAR) $(LOCAL_OUTPUT_DIR_PATH) $(stpp_file) 
	java -jar $(STP_JAR) $(stpp_file) > $(ll_file)

endef

define ll-to-s
$(s_file) : $(ll_file)
	$(LCC) $(LCC_CFLAGS) $(ll_file) -o $(s_file)
endef

define s-to-o
$(o_file) : $(s_file)
	$(CC) -c $(s_file) -o $(o_file)
endef

define objs-to-a
$(local_static_libfile): $(OBJS)
	ar rcs -o $(local_static_libfile) $(OBJS)
endef

define a-to-so
$(local_shared_libfile): $(local_static_libfile)
	$(CC) -shared -Wl,-soname,$(notdir $(local_shared_libfile)) -o $(local_shared_libfile) $(local_static_libfile)
endef

define create-exe
$(local_exefile): $(OBJS)
	$(CC) -o $(local_exefile) $(OBJS)
endef