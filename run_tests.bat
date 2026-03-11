@echo off
call mvnw.cmd test > test.log 2>&1
echo DONE
