# ls

A Leiningen template for Lucero Systems web applications.

---

## Overview

**ls** is a [Leiningen](https://leiningen.org/) project template designed to rapidly scaffold Clojure web applications following Lucero Systems conventions. It provides code generators for CRUD grids, dashboards, and reports, using a consistent handler/view/model structure and Hiccup for HTML rendering.

---

## Features

- **Project scaffolding**: Quickly create a new web app with `lein new ls your-project-name`.
- **Code generators**: Generate CRUD grids, dashboards, and reports for any database table.
- **Template-based**: Uses customizable Clojure string templates for handlers, views, and models.
- **Database integration**: Auto-generates fields from your database schema.
- **Separation of concerns**: Follows a clear handler/view/model directory structure.
- **Hiccup integration**: Uses Hiccup for HTML generation.
- **Extensible**: Easily add new templates or customize existing ones.

---

## Requirements

- **Clojure**: Version 1.10 or higher
- **Java**: Version 17.x.x or higher
- **Leiningen**: Version 2.9.0 or higher

---

## 🚀 Quick Start

### 1. Create a New Project

```sh
lein new ls myapp
cd myapp
```

### 2. Configure the Project

- Edit [`project.clj`](project.clj) and replace all `Change me` and `xxxxx` placeholders with your actual project configuration.
- Edit [`resources/private/config.clj`](resources/private/config.clj) and update the database connection settings and any other relevant configuration values as needed.

**Example database config in `resources/private/config.clj`:**

```clojure
{:db {:classname   "com.mysql.cj.jdbc.Driver"
      :subprotocol "mysql"
      :subname     "//localhost:3306/mydb"
      :user        "myuser"
      :password    "mypassword"}}
```

---

### 3. Create Your Database

Use your preferred MySQL client to create a new database for your project.

```sql
CREATE DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 4. Start the REPL in VS Code

- Open the project folder in [VS Code](https://code.visualstudio.com/).
- Use the [Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) extension to jack-in and connect to your REPL.

---

### 5. Run the Development Server

```sh
lein with-profile dev run
```

---

### 6. Run Database Migrations and Seed Users

```sh
lein migrate      # Creates tables from migrations in resources/migrations/
lein database     # Seeds default users (user, admin, system)
```

**Default users created:**

| Email                | Password |
|----------------------|----------|
| user@example.com     | user     |
| admin@example.com    | admin    |
| system@example.com   | system   |

---

### 7. Open Your App

Visit [http://localhost:3000](http://localhost:3000) in your browser.

---

## 🛠️ Leiningen Aliases

- `lein migrate` &mdash; Run all migrations in [`resources/migrations`](resources/migrations/)
- `lein rollback` &mdash; Roll back the last migration
- `lein database` &mdash; Seed users and other records (see [`src/myapp/models/cdb.clj`](src/myapp/models/cdb.clj))
- `lein grid <table>` &mdash; Scaffold a full CRUD grid for an existing table
- `lein dashboard <table>` &mdash; Scaffold a dashboard for an existing table
- `lein report <report>` &mdash; Scaffold a report for a given name

---

## 🗂️ Project Structure

| Feature         | Location                                         | Description                  |
|-----------------|--------------------------------------------------|------------------------------|
| Grids           | [`src/myapp/handlers/admin/`](src/myapp/handlers/admin/)         | CRUD grids                   |
| Dashboards      | [`src/myapp/handlers/`](src/myapp/handlers/)                    | Dashboards                   |
| Reports         | [`src/myapp/handlers/reports/`](src/myapp/handlers/reports/)     | Reports                      |
| Private routes  | [`src/myapp/routes/proutes.clj`](src/myapp/routes/proutes.clj)   | Authenticated routes         |
| Public routes   | [`src/myapp/routes/routes.clj`](src/myapp/routes/routes.clj)     | Publicly accessible routes   |
| Menu (Navbar)   | [`src/myapp/layout.clj`](src/myapp/layout.clj)                   | Bootstrap 5 navigation bar   |

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
- The Bootstrap 5 navbar is fully customizable in [`src/myapp/layout.clj`](src/myapp/layout.clj).
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
