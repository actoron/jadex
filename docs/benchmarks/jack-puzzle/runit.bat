REM *
REM * Try:	runit
REM *		runit -Dchoice=all
REM *		runit -Dchoice=same
REM *		runit -Dchoice=alter

set CLASSPATH=.;%CLASSPATH%
java %1 Sokrates %2 %3
