

LOCAL_OUTPUT_DIR_PATH := $(LOCAL_OUTPUT_DIR)/dir

$(LOCAL_OUTPUT_DIR_PATH):
	@echo "making $(LOCAL_OUTPUT_DIR)"
	$(SILENT)mkdir -p $(LOCAL_OUTPUT_DIR)
	$(SILENT)touch $@

local_staple_src := $(filter %.stp, $(LOCAL_SRC))

OBJS += $(addprefix $(LOCAL_OUTPUT_DIR)/, $(local_staple_src:%.stp=%.o))


STP_INCLUDES := $(addprefix -I , $(LOCAL_PATH) $(LOCAL_INCLUDE))

$(foreach stp_src, $(local_staple_src), $(eval $(call stp-to-stpp)))
$(foreach stp_src, $(local_staple_src), $(eval $(call stpp-to-ll)))
$(foreach stp_src, $(local_staple_src), $(eval $(call ll-to-s)))
$(foreach stp_src, $(local_staple_src), $(eval $(call s-to-o)))
