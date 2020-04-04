set XR-WAR=arms-xr
del ..\webapps\%XR-WAR%.war
del /Q /S ..\webapps\%XR-WAR%
rmdir /Q /S ..\webapps\%XR-WAR%


set RS-WAR=arms-rs
set RELEASED_WAR=arms-rs-1.0-SNAPSHOT

del ..\webapps\%RS-WAR%.war
del /Q /S ..\webapps\%RS-WAR%
rmdir /Q /S ..\webapps\%RS-WAR%
copy ..\..\projects\ArmsRsServlet\target\%RELEASED_WAR%.war ..\webapps\%RS-WAR%.war