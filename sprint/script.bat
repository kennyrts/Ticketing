@echo off
setlocal EnableDelayedExpansion

rem Définir le chemin d'accès au répertoire des sources et au répertoire de destination des fichiers compilés
set "sourceDirectory=.\src"
set "destinationDirectory=.\bin"
set "outputJar=D:\ITU\L3\S5\Naina\Ticketing\lib\sprint.jar"

rem Chemin vers le répertoire contenant les bibliothèques nécessaires
set "libDirectory=.\lib"  

rem Nettoyage du contenu du répertoire de destination (sans supprimer le dossier)
if exist "%destinationDirectory%" (
    echo Nettoyage du contenu du dossier bin...
    for /d %%D in ("%destinationDirectory%\*") do rd /s /q "%%D"
    for %%F in ("%destinationDirectory%\*") do del /q "%%F"
) else (
    mkdir "%destinationDirectory%"
)

rem Initialiser la liste des fichiers Java à compiler
set "javaFiles="
rem Récupérer la liste de tous les fichiers Java dans les sous-dossiers de %sourceDirectory%
for /r "%sourceDirectory%" %%G in (*.java) do (
    rem Extraire la structure des packages à partir du chemin complet du fichier source
    set "javaFile=%%~fG"
    set "packagePath=!javaFile:%sourceDirectory%=!"
    set "packagePath=!packagePath:~0,-\%%~nG%%~xG!"

    rem Créer les répertoires de sortie si nécessaire
    if not exist "%destinationDirectory%!packagePath!" (
        mkdir "%destinationDirectory%!packagePath!" >nul
    )

    rem Ajouter le fichier Java à la liste des fichiers à compiler
    set "javaFiles=!javaFiles! "%%G""
)

rem Construire le chemin de classe pour toutes les bibliothèques dans le dossier "lib"
set "classpath="
for %%I in ("%libDirectory%\*.jar") do (
    set "classpath=!classpath!;"%%I""
)

rem Compiler tous les fichiers Java en une seule commande avec les bibliothèques nécessaires
javac -parameters -cp "%classpath%" -d "%destinationDirectory%" !javaFiles!

rem Creation du fichier jar
echo Creation du jar
jar cvf "%outputJar%" -C "%destinationDirectory%" .
echo Fichier jar cree avec succes : sprint.jar

rd /s /q "%destinationDirectory%"

pause