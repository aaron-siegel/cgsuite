:: This script replaces the icon for the NetBeans executables.
:: Be careful! It will modify files in your NetBeans installation dir.
:: The script assumes that:
:: - ReplaceVistaIcon.exe is in desktop-app/local
:: - NetBeans harness folder is mapped to drive letter N:

..\local\ReplaceVistaIcon.exe "N:\launchers\app.exe" cgsuite.ico
..\local\ReplaceVistaIcon.exe "N:\launchers\app64.exe" cgsuite.ico
