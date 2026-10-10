@echo off
rem Ejecuta Fondo de Bikini con un doble clic. Necesita un JDK 17 o superior instalado.
rem Se usa la ruta completa de mvnw.cmd porque algunos Windows no buscan programas en la carpeta actual.
cd /d "%~dp0"
call "%~dp0mvnw.cmd" -q compile exec:exec
if errorlevel 1 pause
