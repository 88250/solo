@echo off
cd %cd%
%~d0
java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter
pause:
