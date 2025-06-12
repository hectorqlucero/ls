# lsgen

A utility to quickly scaffold and build a full stack web application with Clojure, MySQL, and Bootstrap 5.

---

## 🚀 Quick Start

1. **Clone the repository**
   ```sh
   git clone <your-repo-url>
   cd contactos
   ```

2. **Create your MySQL database**  
   Use your favorite MySQL client to create a new database.

3. **Configure the project**
   - Edit [`project.clj`](project.clj) and replace all `Change me` and `xxxxx` placeholders with your configuration.
   - Rename `resources/private/config_example` to `config.clj` and update it as needed.

4. **Start the REPL in VS Code**
   - Open the project in VS Code.
   - Click the **REPL** button (Calva extension) to jack-in and connect.

5. **Run the development server**
   ```sh
   lein with-profile dev run
   ```

6. **Run database migrations and seed users**
   ```sh
   lein migrate      # Creates tables from migrations
   lein database     # Seeds default users (user, admin, system)
   ```

7. **Open your app**  
   Visit [http://localhost:3000](http://localhost:3000) in your browser.

---

## 🛠️ Leiningen Aliases

- `lein migrate` &mdash; Run all migrations in [`resources/migrations`](resources/migrations/)
- `lein rollback` &mdash; Roll back the last migration
- `lein database` &mdash; Seed users and other records (see [`src/contactos/models/cdb.clj`](src/contactos/models/cdb.clj))
- `lein grid <table>` &mdash; Scaffold a full CRUD grid for an existing table
- `lein dashboard <table>` &mdash; Scaffold a dashboard for an existing table
- `lein report <report>` &mdash; Scaffold a report for a given name

---

## 🗂️ Project Structure

| Feature      | Location                                    | Description                        |
|--------------|---------------------------------------------|------------------------------------|
| Grids        | [`src/contactos/handlers/admin/`](src/contactos/handlers/admin/)         | CRUD grids                         |
| Dashboards   | [`src/contactos/handlers/`](src/contactos/handlers/)                    | Dashboards                         |
| Reports      | [`src/contactos/handlers/reports/`](src/contactos/handlers/reports/)     | Reports                            |
| Private routes | [`src/contactos/routes/proutes.clj`](src/contactos/routes/proutes.clj) | Authenticated routes               |
| Public routes  | [`src/contactos/routes/routes.clj`](src/contactos/routes/routes.clj)   | Publicly accessible routes         |
| Menu (Navbar)  | [`src/contactos/layout.clj`](src/contactos/layout.clj)                 | Bootstrap 5 navigation bar         |

Each grid, dashboard, and report contains:
- `controller.clj`
- `model.clj`
- `view.clj`

---

## 📦 Requirements

- Java SDK
- MySQL (configured with a password)
- [Leiningen](https://leiningen.org)
- [VS Code](https://code.visualstudio.com/) with [Calva: Clojure & ClojureScript](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) extension

---

## 💡 Tips

- All code generation and migrations are managed via Leiningen commands.
- The Bootstrap 5 navbar is fully customizable in [`src/contactos/layout.clj`](src/contactos/layout.clj).
- Migrations are stored in [`resources/migrations/`](resources/migrations/).

---

## 📝 Example Usage

```sh
lein grid users
lein dashboard sales
lein report monthly-summary
```

---

For more details, see the inline comments in each source file or open an issue if you need help!
