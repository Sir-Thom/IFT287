# Utilise l'image officielle PostgreSQL comme base
FROM postgres:17

# Définir quelques variables d'environnement
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=admin
ENV POSTGRES_DB=ift287

# Copier des scripts SQL de création de tables ou d'initialisation si nécessaire
# COPY init.sql /docker-entrypoint-initdb.d/

# Port exposé (par défaut PostgreSQL écoute sur le port 5432)
EXPOSE 5432

