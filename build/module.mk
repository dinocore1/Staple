

LOCAL_OUTPUT_DIR_PATH := $(LOCAL_OUTPUT_DIR)/dir

$(eval $(call output-dir))

local_staple_src := $(filter %.stp, $(LOCAL_SRC))
local_obj := $(addprefix $(LOCAL_OUTPUT_DIR)/, $(local_staple_src:%.stp=%.o))
 
ifneq ($(strip $(LOCAL_STATIC_LIBS)),)
local_static_libs := $(addprefix $(OUT)/, $(join $(LOCAL_STATIC_LIBS)/, lib$(LOCAL_STATIC_LIBS).a))
endif


OBJS += $(local_obj) $(local_static_libs)

STP_INCLUDES := $(addprefix -I , $(LOCAL_PATH) $(LOCAL_INCLUDE))

$(foreach stp_src, $(local_staple_src), $(eval $(call stp-to-d)))
$(foreach stp_src, $(local_staple_src), $(eval $(call stp-to-stpp)))
$(foreach stp_src, $(local_staple_src), $(eval $(call stpp-to-ll)))
$(foreach stp_src, $(local_staple_src), $(eval $(call ll-to-s)))
$(foreach stp_src, $(local_staple_src), $(eval $(call s-to-o)))
