set RS-WAR=arms-rs
del ..\webapps\%RS-WAR%.war
del /Q /S ..\webapps\%RS-WAR%
rmdir /Q /S ..\webapps\%RS-WAR%


set XR-WAR=arms-xr
set RELEASED_WAR=arms-xr-1.0-SNAPSHOT

del ..\webapps\%XR-WAR%.war
del /Q /S ..\webapps\%XR-WAR%
rmdir /Q /S ..\webapps\%XR-WAR%
copy ..\..\projects\ArmsXrServlet\target\%RELEASED_WAR%.war ..\webapps\%XR-WAR%.war
