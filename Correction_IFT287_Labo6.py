# -*- coding: utf-8 -*-
# script Python3 à executer  pour un train turninWeb
import subprocess as sub
import os
import sys
import shutil
import glob
import csv

#PARAMÈTRES
TP = "Labo6"
FICHIER_SOUMIS = "Labo6.zip"
FICHIER_EXECUTABLE_A = "SeauS.SeauS"
FICHIER_A_PATH = "Labo6/SeauS/SeauS.class"

SERVEURSQL = 'bd-info2.dinf.usherbrooke.ca'
PORT = '5432'
BDUSER = 'maym2104'
BDPASSWORD = 'ohChohhoo1Ba'
BDNAME = 'maym2104db'

FICHIERS_REQUIS = ['TP2/creation.sql', 'TP2/destruction.sql', 'TP2/affichage.sql']

fichiers_test = [
        "test1_compagnies_communaute.dat",
        "test2_projets.dat",
        "test3_modifications_liste.dat",
        "test4_relations_parents.dat",
        "test5_suppressions.dat"
    ]

# Fichiers fournis par l'enseignant avec le script de correction qui sont nécessaire au script
FICHIER_PROF = ['postgresql.jar','creation.sql', 'destruction.sql', 'affichage.sql', 'Connexion.java', 'GestionSeauS.java', 'SeauS.java', 'SeauSException.java']


ENCODAGE_UTF8 = 'utf-8'
ENCODAGE_CP1250 = 'cp1250'
ENCODAGE_LATIN1 = 'latin1'

def log(output, fileOutput, studentVisible):
    """Fonction affichant un message

    :param output: Message à afficher
    :param fileOutput: Fichier dans lequel écrire le message
    :param studentVisible: Est-ce qu'on imprime aussi à l'écran pour l'étudiant?
    :return: None
    """
    if studentVisible:
        print(output)
    fileOutput.write(output)


def compile_java(encoding=None):
    if encoding is None:
        encoding = ENCODAGE_UTF8

    try:
        resultat_compil = sub.check_output("javac -encoding " + encoding + " -cp " + FICHIER_PROF[0] + " -d \"" + TP + "/.\" `find . -type f -name '*.java'`", shell=True, stderr=sub.STDOUT).decode(encoding)
    except UnicodeError as ex:
        raise ex
    except sub.CalledProcessError as er:
        print(er.output.decode(encoding))
        raise er

    return resultat_compil


def execute_java(className:str, jars:list, args:str, encoding=None)->str:
    """Exécute un programme Java à l'aide des arguments données

    :param className: Nom de la classe Java a exécuté
    :param jars: Nom des fichiers JAR à ajouter au ClassPath de Java
    :param args: Les arguments à envoyé à la classe Java
    :param encoding: L'encodage des caractères attendu pour les résultats de l'exécution
    :return: Le résultat de l'exécution du programme
    """

    if encoding is None:
        encoding = ENCODAGE_UTF8

    classPath = ".:./" + TP
    for cp in jars:
        classPath += ":" + cp

    command = 'java -cp "' + classPath + '" ' + className + ' ' + args
    resultat_execution = sub.check_output(command, shell=True, stderr=sub.STDOUT).decode(encoding)
    return resultat_execution


def unzipFile(fileName):
    outputB = sub.check_output('unzip -U ' + fileName, shell=True)
    try:
        return outputB.decode(ENCODAGE_UTF8)
    except UnicodeError:
        return outputB.decode(ENCODAGE_LATIN1)


def execute_sql_file(fileName):
    commande = "PGPASSWORD='" + BDPASSWORD + "' psql -a -d " + BDNAME + " -h " + SERVEURSQL + " -p " + PORT + " -U " + BDUSER + " -t -f " + fileName

    try:
        resultat_exec = sub.check_output(commande, shell=True, stderr=sub.STDOUT)
        try:
            return resultat_exec.decode(ENCODAGE_UTF8)
        except UnicodeError:
            try:
                return resultat_exec.decode(ENCODAGE_LATIN1)
            except:
                return resultat_exec
    except sub.CalledProcessError as CE:
        raise CE


def create_header(title, car='='):
    """Crée un texte d'entète entouré du caractère indiqué.
    Si le texte est plus long qu'une ligne, l'entête sera multiligne

    :param title: Texte à écrire
    :param car: Caractère pour entouré le texte
    :return: Le texte d'entête
    """

    header = car * 80
    header += '\n'

    titleLen = len(title)
    # Entête multi-lignes
    if titleLen > 76:
        titles = []
        words = title.split(' ')
        lineLenght = 0
        line = ''
        for w in words:
            lineLenght += len(w)
            if lineLenght > 76:
                titles.append(line)
                line = w + ' '
                lineLenght = len(w) + 1
            else:
                line += w + ' '
                lineLenght += 1
        titles.append(line)

        for line in titles:
            header += car + ' '
            header += line
            header += ' ' * (77 - len(line)) + car + '\n'
    else:
        spaceLeft = int((78 - titleLen) / 2)
        spaceRight = int(78 - titleLen - spaceLeft)
        header += car
        header += ' ' * spaceLeft
        header += title
        header += ' ' * spaceRight
        header += car + '\n'

    header += car * 80
    header += '\n'

    return header

def read_credentials(cip):
    with open('credentials.csv') as credentials:
        credentials = csv.reader(credentials, delimiter=';')
        for row in credentials:
            if row[0] == cip:
                BDUSER = row[1]
                BDPASSWORD = row[3]
                BDNAME = row[2]


def execute_train(cipEtd):
    score = 0
    with open('output' + cipEtd + '.txt', 'w+') as outputFile:
        # aller chercher ses credentials de bd
        read_credentials(cip=cipEtd)

        if os.path.exists(FICHIER_SOUMIS):
            #ETAPE 1 Décompresser le fichier soumis
            try:
                unzipFile(FICHIER_SOUMIS)
            except Exception as e:
                log('Erreur lors de la décompression : ' + str(e), outputFile, True)
                log('Impossible de poursuivre la correction...', outputFile, True)
                log(create_header('Exécution terminée.', '*'), outputFile, True)
                return score


            # Les fichiers requis sont présents, on peut porusuivre
            header = create_header('COMPILATION DES SOURCES JAVA', '*')
            log(header, outputFile, True)

            try:
                os.rename('Connexion.java', 'Labo6/src/SeauS/bdd/Connexion.java')
                os.rename('SeauS.java', 'Labo6/src/SeauS/SeauS.java')
                os.rename('GestionSeauS.java', 'Labo6/src/SeauS/GestionSeauS.java')
                os.rename('SeauSException.java', 'Labo6/src/SeauS/SeauSException.java')
            except Exception as UE:
                header = create_header("ERREUR LORS DU REMPLACEMENT DES FICHIERS", '?')
                log(header, outputFile, True)
                log(str(UE), outputFile, True)
                log("Impossible de continuer l'exécution...", outputFile, True)
                return score

            # Compilation du programme JAVA
            try:
                resultat_compilation = compile_java(ENCODAGE_UTF8)
                log(resultat_compilation, outputFile, True)
                log('Compilation réussie', outputFile, True)
            except sub.CalledProcessError as e:
                log('ERREUR de compilation.', outputFile, True)
                log(str(e.output), outputFile, True)
                log("Impossible de continuer l'exécution...", outputFile, True)
                log(create_header('Exécution terminée.', '*'), outputFile, True)
                return score

            h = create_header('EXÉCUTION DU PROGRAMME', '*')
            log(h, outputFile, True)
            fichierResultat = FICHIER_EXECUTABLE_A
            if not os.path.exists(FICHIER_A_PATH):
                log(' ERREUR DANS VOTRE NOM DE CLASSE ' + FICHIER_EXECUTABLE_A + '... ', outputFile, True)
                fichierResultat = ''

            if fichierResultat != '':
                try:
                    resultat = execute_sql_file(FICHIER_PROF[-3])
                    log(resultat, outputFile, True)
                except sub.CalledProcessError as e:
                    log("ERREUR lors de la création des tables : " + e.output.decode(ENCODAGE_UTF8), outputFile, True)
                    log("Impossible de continuer l'exécution...", outputFile, True)
                    log(create_header('Exécution terminée.', '*'), outputFile, True)
                    return score
                for fichier_test in fichiers_test:
                    try:
                        try:
                            resultat_java = execute_java(
                                className=fichierResultat,
                                jars=[FICHIER_PROF[0]],
                                args='dinf ' + BDNAME + ' ' + BDUSER + ' \'' + BDPASSWORD + '\' ' + fichier_test,
                                encoding=ENCODAGE_UTF8
                            )
                            
                        except sub.CalledProcessError as e:
                            resultat_java = e.output.decode(ENCODAGE_UTF8)
                            return score

                        log(resultat_java, outputFile, True)

                        try:
                            resultat_sql = execute_sql_file(FICHIER_PROF[-1])
                            log(resultat_sql, outputFile, True)
                            if "PSQLException" not in resultat_java: # indique qu'il y a une erreur dans le script SQL
                                score +=1
                        except sub.CalledProcessError as e:
                            log("ERREUR lors de l'affichage des données : " + e.output.decode(ENCODAGE_UTF8), outputFile, True)
                            log("Impossible de continuer l'exécution...", outputFile, True)
                            log(create_header('Exécution terminée.', '*'), outputFile, True)
                            #return score


                    except Exception as e:
                        log(str(e), outputFile, True)
                    
            else:
                log('Impossible de trouver le document ' + fichierResultat + ' ou une classe appropriée dans votre projet', outputFile, True)
                return score
            try: # destruction.sql
                resultat = execute_sql_file(FICHIER_PROF[-2])
                log(resultat, outputFile, True)
            except sub.CalledProcessError as e:
                log("ERREUR lors de la suppression des tables : " + e.output.decode(ENCODAGE_UTF8), outputFile, True)
                #return score
            
        else:
            log(FICHIER_SOUMIS + ' introuvable... Impossible de poursuivre la correction.', outputFile, True)
            return score

        log(create_header('Exécution terminée.', '*'), outputFile, True)
        return score

#
#   PRÉPARER SORTIE du TRAIN pour un utilisateur
#
if __name__ == "__main__":
    cip = sys.argv[1]

    score = 0
    try:
        score = execute_train(cipEtd=cip)
        print(f"Score : {score}")
    except Exception as exception:
        print('Erreur : ' + str(exception))
    sys.exit(score)
