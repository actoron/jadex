include $(CLEAR_VARS)
LOCAL_MODULE := nativehelper
LOCAL_PATH := .
LOCAL_SRC_FILES := NativeHelperUtil.cpp NativeHelper.cpp
include $(BUILD_SHARED_LIBRARY)

