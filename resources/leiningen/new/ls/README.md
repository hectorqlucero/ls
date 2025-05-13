# lsgen
A utility that helps build a full stack web app

## Requirements
1. Java sdk installed
2. MySQL installed and configured with a password
3. Leiningen installled. https://leiningen.org
4. vscode installed with the extension calva:clojure

## How to use the utility
1. Clone this repository in your computer
2. Create a database in your favorite mysql client.
3. Configure **project.clj**
4. Rename **resources/private/config_example** to **config.clj**
5. Configure **project.clj**. Replace **Change me** with your own configuration
6. Configure all **xxxxx** with your own configuration
7. On your vscode status click **REPL** to  **Start your project with a REPL and connect (a.k.a. jack-in)**
8. Open a new terminal: 
   * `lein with-profile dev run`
9. Open another terminal and run:
   * `lein migrate`
      * Will create the **users** table
   * `lein database`
      * Creates a user user@example.com password **user** in the table **users**
      * Creates an admin user admin@example.com password **admin** in the table **users**
      * Creates a system user system@example.com password **system** in the table **users**
10. Run from one of your terminals: **Lein with-profile dev run**
    * Go to `http:localhost:3000` on your favorite browser

## Leiningen aliases to scaffold code, populate tables or run migrations
 1. **lein migrate** will create migrations defined on **resource/migrations** (ragtime migrations)
 2. **lein rollback** will rollback a migration
 3. **lein database** will create users or whatever other records needed from **sk/models/cdb.clj**
 4. **lein grid table-name** will scaffold a full crud grid on an existing table-name
 5. **lein dashboard table-name** will scaffold a dashboard on an existing table-name
 6. **lein report table-name** will scaffold a report on an existing table-name

 ## bootstrap5 navbar defined in **src/sk/layout.clj**
