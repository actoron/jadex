#include "jadex_bytecode_vmhacks_NativeHelper.h"

/*
 * Class:     jadex_bytecode_NativeHelper
 * Method:    setAccessible
 * Signature: (Ljava/lang/String;Ljava/lang/reflect/AccessibleObject;)V
 */
JNIEXPORT void JNICALL Java_jadex_bytecode_vmhacks_NativeHelper_setAccessible(JNIEnv *env, jclass nhclazz, jstring flagname, jobject accobj, jboolean flag)
{
	jclass accclazz = env->GetObjectClass(accobj);
	const char* cflagname = env->GetStringUTFChars(flagname, NULL);
	jfieldID fid = env->GetFieldID(accclazz, cflagname, "Z");
	env->ReleaseStringUTFChars(flagname, cflagname);
	env->SetBooleanField(accobj, fid, flag);
}

/*
 * Class:     jadex_bytecode_NativeHelper
 * Method:    defineClass
 * Signature: (Ljava/lang/String;[BILjava/lang/ClassLoader;)Ljava/lang/Class;
 */
JNIEXPORT jclass JNICALL Java_jadex_bytecode_vmhacks_NativeHelper_defineClass(JNIEnv *env, jclass nhclass,
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

