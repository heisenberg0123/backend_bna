# BNA Spring Backend

Ce projet est le backend de l'application de gestion bancaire pour la banque BNA, développé dans le cadre d'un stage d'ingénierie.
Il fournit des APIs RESTful pour gérer les opérations bancaires, avec une authentification basée sur les rôles et une intégration avec des APIs externes.

## Fonctionnalités
- Gestion des tables : `employees`, `materials`, `transactions`, `suppliers`, `orders_suppliers`.
- Authentification JWT avec quatre rôles : Admin, Financiere, OperationsManager, Clerk.
- Permissions basées sur les rôles (ex. : Admin a un accès total, Clerk en lecture seule).
- Intégration d'APIs externes pour les taux de change, actualités fournisseurs, et prix des matériaux.
- Exportation CSV programmée pour les rapports.
- Endpoints pour le chatbot Rasa et le modèle ML de classification des transactions.

## Prérequis
- Java 17
- Maven 3.8+
- MySQL 8.0+
- Variables d'environnement pour les configurations sensibles (ex. : `DB_URL`, `API_KEY_EXCHANGE`).

## Installation
1. Clonez le dépôt :
   ```bash
   git clone git@github.com:votre-utilisateur/bna-spring-backend.git
   cd bna-spring-backend
