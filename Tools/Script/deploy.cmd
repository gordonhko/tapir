set WAR=foundry
set RELEASED_WAR=WsFacade-1.0-SNAPSHOT

del ..\webapps\%WAR%.war
del /Q /S ..\webapps\%WAR%
rmdir /Q /S ..\webapps\%WAR%
copy ..\..\projects\WsFacade\target\%RELEASED_WAR%.war ..\webapps\%WAR%.war