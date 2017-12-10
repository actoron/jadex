#include "jadex_bytecode_NativeHelper.h"

#include <iostream>

jmethodID defclass = NULL;

/*
 * Class:     jadex_bytecode_NativeHelper
 * Method:    defineClass
 * Signature: (Ljava/lang/String;[BILjava/lang/ClassLoader;)Ljava/lang/Class;
 */
JNIEXPORT jclass JNICALL Java_jadex_bytecode_NativeHelper_defineClass(JNIEnv *env, jclass nhclass,
								      jstring name,
								      jbyteArray bytecode,
								      jint length,
								      jobject classloader)
{
	jclass cls = env->GetObjectClass(classloader);
	const char* cname = name == NULL? NULL : env->GetStringUTFChars(name, JNI_FALSE);
	jbyte* cbytecode = env->GetByteArrayElements(bytecode, JNI_FALSE);
	jclass ret = env->DefineClass(cname, classloader, cbytecode, length);
	env->ReleaseStringUTFChars(name, cname);
	env->ReleaseByteArrayElements(bytecode, cbytecode, length);
	return ret;
}
