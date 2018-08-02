#!/bin/sh
java $JVM_ARGS -cp lib/jadex-platform-base-${jadex_build_version}.jar jadex.platform.DynamicStarter -gui false $@
