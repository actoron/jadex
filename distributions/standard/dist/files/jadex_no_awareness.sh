#!/bin/sh
java -cp lib/jadex-platform-${jadex_build_version}.jar jadex.platform.DynamicStarter -awareness false $@
