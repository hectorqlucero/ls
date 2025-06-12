# ls

A Professional Leiningen Template for Lucero Systems Web Applications

---

## Overview

**ls** is a modern [Leiningen](https://leiningen.org/) project template for rapidly building robust, scalable, and maintainable Clojure web applications. Designed for productivity and best practices, it empowers developers to scaffold CRUD grids, dashboards, and reports in seconds, following Lucero Systems conventions. With a clear handler/view/model structure, Hiccup-based HTML, and seamless database integration, **ls** is the fastest way to launch your next Clojure web project.

---

## ✨ Features

- **Rapid Project Scaffolding**: Instantly create a new web app with `lein new ls your-project-name`.
- **Powerful Code Generators**: Generate CRUD grids, dashboards, and reports for any database table.
- **Customizable Templates**: Easily adapt Clojure string templates for handlers, views, and models.
- **Automatic Database Integration**: Auto-generates fields from your database schema.
- **Separation of Concerns**: Enforces a clear handler/view/model directory structure.
- **Hiccup for HTML**: Leverages Hiccup for safe, idiomatic HTML generation.
- **Highly Extensible**: Effortlessly add or modify templates to suit your needs.
- **Bootstrap 5 Ready**: Modern, responsive UI out of the box.
- **VS Code & Calva Friendly**: Optimized for a smooth developer experience.
- **Open Source**: MIT/EPL licensed and ready for your contributions.

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

## 💡 Tips & Best Practices

- All code generation and migrations are managed via Leiningen commands.
- The Bootstrap 5 navbar is fully customizable in [`src/myapp/menu.clj`](src/myapp/menu.clj).
- Migrations are stored in [`resources/migrations/`](resources/migrations/).
- Use the provided code generators to keep your codebase consistent and DRY.
- Take advantage of Hiccup for safe, composable HTML rendering.

---

## 📝 Example Usage

```sh
lein grid users
lein dashboard sales
lein report monthlySummary
```

---

## 🌐 Why Choose ls?

- **SEO-Ready**: Clean, semantic HTML and best practices for discoverability.
- **Enterprise-Grade**: Built for Lucero Systems, but flexible for any Clojure web project.
- **Community-Driven**: Contributions welcome! Join the growing Clojure web community.
- **Documentation & Support**: Inline comments, clear structure, and responsive maintainers.

---

## 📖 API Documentation: ls Leiningen Template

This document provides an overview of the main namespaces, functions, and code generation APIs available in the **ls** Leiningen template for Lucero Systems web applications.

---

## Table of Contents

- [Overview](#overview)
- [Namespace Structure](#namespace-structure)
- [Key Namespaces & Responsibilities](#key-namespaces--responsibilities)
- [Core API Functions](#core-api-functions)
- [Code Generation Commands](#code-generation-commands)
- [Configuration](#configuration)
- [Extending the Template](#extending-the-template)
- [Further Reading](#further-reading)

---

## Overview

The **ls** template generates a Clojure web application with a modular handler/view/model structure, automatic CRUD, dashboard, and report scaffolding, and seamless database integration. The generated code is idiomatic, extensible, and ready for production.

---

## Namespace Structure

```
src/
└── myapp/
    ├── handlers/
    │   ├── admin/
    │   │   └── <table>/
    │   │       ├── controller.clj
    │   │       ├── model.clj
    │   │       └── view.clj
    │   ├── reports/
    │   │   └── <report>/
    │   │       ├── controller.clj
    │   │       ├── model.clj
    │   │       └── view.clj
    ├── models/
    │   ├── crud.clj
    │   └── ...
    ├── routes/
    │   ├── proutes.clj
    │   └── routes.clj
    ├── menu.clj
    └── layout.clj
resources/
└── migrations/
```

---

## Key Namespaces & Responsibilities

- **handlers.admin.\<table\>.controller**  
  RESTful endpoints for CRUD operations on a table.

- **handlers.admin.\<table\>.model**  
  Database access and business logic for the table.

- **handlers.admin.\<table\>.view**  
  Hiccup-based HTML rendering for the table's UI.

- **handlers.reports.\<report\>.\***  
  Controller, model, and view for custom reports.

- **models.crud**  
  Generic CRUD utilities, schema introspection, and helpers.

- **menu**  
  Bootstrap 5 navigation bar configuration.

- **layout**  
  Application-wide HTML layout and shared UI components.

- **routes/routes.clj**  
  Public routes.

- **routes/proutes.clj**  
  Authenticated/private routes.

---

## Core API Functions

### Example: `handlers.admin.<table>.controller`

```clojure
(ns myapp.handlers.admin.users.controller
  (:require
    [myapp.handlers.admin.users.model :refer [get-users get-users-id]]
    [myapp.handlers.admin.users.view :refer [users-view users-form-view]]
    [myapp.layout :refer [application error-404]]
    [myapp.models.crud :refer [build-form-delete build-form-save]]
    [myapp.models.util :refer [get-session-id user-level]]
    [hiccup.core :refer [html]]))

(defn users
  [request]
  (let [title "Users"
        ok (get-session-id request)
        js nil
        rows (get-users)
        content (users-view title rows)]
    (if (= (user-level request) "S")
      (application request title ok js content)
      (application request title ok nil "Not authorized to access this item! (level 'S')"))))

(defn users-add-form
  [_]
  (let [title "New User"
        row nil
        content (users-form-view title row)]
    (html content)))

(defn users-edit-form
  [_ id]
  (let [title "Edit User"
        row (get-users-id id)
        content (users-form-view title row)]
    (html content)))

(defn users-save
  [{params :params}]
  (let [table "users"
        result (build-form-save params table)]
    (if result
      {:status 200 :headers {"Content-Type" "application/json"} :body "{\"ok\":true}"}
      {:status 500 :headers {"Content-Type" "application/json"} :body "{\"ok\":false}"}
    )))
```

### Example: `models.crud`

```clojure
(ns myapp.models.crud)

(defn get-table-columns
  "Returns a vector of column names for the given table."
  [table-name]
  ;; Implementation depends on your DB library
  )

(defn build-form-save
  "Handles saving a form submission for the given table."
  [params table]
  ;; Implementation
  )

(defn build-form-delete
  "Handles deleting a record from the given table."
  [table id]
  ;; Implementation
  )
```

---

## Code Generation Commands

The following Leiningen commands are available for code generation:

- `lein grid <table>`  
  Scaffold a full CRUD grid for an existing table.

- `lein dashboard <table>`  
  Scaffold a dashboard for an existing table.

- `lein report <report>`  
  Scaffold a report for a given name.

- `lein migrate`  
  Run all migrations in `resources/migrations/`.

- `lein rollback`  
  Roll back the last migration.

- `lein database`  
  Seed users and other records (see `src/myapp/models/cdb.clj`).

---

## Configuration

- **Database**:  
  Edit `resources/private/config.clj` to set your database connection parameters.

- **Menu**:  
  Configure your Bootstrap 5 navigation bar in `src/myapp/menu.clj`.

- **Routes**:  
  Add or modify routes in `src/myapp/routes/routes.clj` (public) and `src/myapp/routes/proutes.clj` (private).

---

## Extending the Template

- **Custom Templates**:  
  Edit `resources/leiningen/new/ls/builder.clj` to add or modify code generation templates.

- **Add New Features**:  
  Follow the handler/view/model pattern for new resources.

- **UI Customization**:  
  Modify `src/myapp/layout.clj` and `src/myapp/menu.clj` for branding and navigation.

---

## Further Reading

- Inline comments are provided in each generated source file.
- For advanced usage, see the [Leiningen documentation](https://leiningen.org/).
- For questions or contributions, open an issue or pull request on the [GitHub repository](https://github.com/your-org/ls).

---

&copy; Lucero Systems. All rights reserved.

<!--
SEO keywords: Clojure API documentation, Leiningen template API, CRUD generator API, Clojure web app API, Lucero Systems, handler/view/model, Clojure code generation, Clojure project structure, open source, enterprise Clojure, Clojure dashboard API, Clojure report API, Clojure CRUD API
-->
