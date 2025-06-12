# ls

A Professional Leiningen Template for Lucero Systems Web Applications

---

## Overview

**ls** is a [Leiningen](https://leiningen.org/) project template designed to rapidly scaffold robust Clojure web applications following Lucero Systems conventions. It provides code generators for CRUD grids, dashboards, and reports, using a consistent handler/view/model structure and Hiccup for HTML rendering.

---

## ✨ Features

- **Rapid Project Scaffolding**: Instantly create a new web app with `lein new ls your-project-name`.
- **Powerful Code Generators**: Generate CRUD grids, dashboards, and reports for any database table.
- **Customizable Templates**: Easily adapt Clojure string templates for handlers, views, and models.
- **Automatic Database Integration**: Auto-generates fields from your database schema.
- **Separation of Concerns**: Enforces a clear handler/view/model directory structure.
- **Hiccup for HTML**: Leverages Hiccup for safe, idiomatic HTML generation.
- **Highly Extensible**: Effortlessly add or modify templates to suit your needs.

---

## ⚙️ Requirements

- **Clojure**: 1.10 or higher
- **Java**: 17.x.x or higher
- **Leiningen**: 2.9.0 or higher

---

## 🛠️ Installing the Template Locally

To use this template on your computer:

1. **Clone the repository:**
   ```sh
   git clone <your-repo-url>
   cd ls
   ```

2. **Build and install the template into your local Maven repository:**
   ```sh
   lein clean
   lein deps
   lein install
   ```

   You can now use `ls` as a template for new projects on your machine.

---

## 🚀 Quick Start

### 1. Create a New Project

```sh
lein new ls myapp
cd myapp
```

### 2. Configure the Project

- Edit [`project.clj`](project.clj) and replace all `Change me` and `xxxxx` placeholders with your actual project configuration.
- Edit [`resources/private/config.clj`](resources/private/config.clj) and update the database connection settings and any other relevant configuration values.

**Example database config in `resources/private/config.clj`:**

```clojure
{:db {:classname   "com.mysql.cj.jdbc.Driver"
      :subprotocol "mysql"
      :subname     "//localhost:3306/mydb"
      :user        "myuser"
      :password    "mypassword"}}
```

### 3. Create Your Database

Use your preferred MySQL client to create a new database:

```sql
CREATE DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. Start the REPL in VS Code

- Open the project folder in [VS Code](https://code.visualstudio.com/).
- Use the [Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) extension to jack-in and connect to your REPL.

### 5. Run the Development Server

```sh
lein with-profile dev run
```

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

| Feature         | Location                                              | Description                    |
|-----------------|------------------------------------------------------|--------------------------------|
| Grids           | [`src/myapp/handlers/admin/`](src/myapp/handlers/admin/)         | CRUD grids                     |
| Dashboards      | [`src/myapp/handlers/`](src/myapp/handlers/)                      | Dashboards                     |
| Reports         | [`src/myapp/handlers/reports/`](src/myapp/handlers/reports/)      | Reports                        |
| Private routes  | [`src/myapp/routes/proutes.clj`](src/myapp/routes/proutes.clj)    | Authenticated routes           |
| Public routes   | [`src/myapp/routes/routes.clj`](src/myapp/routes/routes.clj)      | Publicly accessible routes     |
| Menu (Navbar)   | [`src/myapp/menu.clj`](src/myapp/menu.clj)                        | Bootstrap 5 navigation bar     |

Each grid, dashboard, and report contains:
- `controller.clj`
- `model.clj`
- `view.clj`

---

## 📦 Development Environment

- **Java SDK**
- **MySQL** (configured with a password)
- [Leiningen](https://leiningen.org)
- [VS Code](https://code.visualstudio.com/) with [Calva: Clojure & ClojureScript](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) extension

---

## 💡 Tips

- All code generation and migrations are managed via Leiningen commands.
- The Bootstrap 5 navbar is fully customizable in [`src/myapp/menu.clj`](src/myapp/menu.clj).
- Migrations are stored in [`resources/migrations/`](resources/migrations/).

---

## 📝 Example Usage

```sh
lein grid users
lein dashboard sales
lein report monthly-summary
```

---

## 📚 Further Reading & Support

- Inline comments are provided in each source file for guidance.
- For questions, feature requests, or contributions, please open an issue or pull request on the project repository.

---

&copy; Lucero Systems. All rights reserved.
