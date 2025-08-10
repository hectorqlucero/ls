# ls

A Professional Leiningen Template for Lucero Systems Web Applications

---

## Overview

**ls** is a modern [Leiningen](https://leiningen.org/) project template for rapidly building robust,
scalable, and maintainable Clojure web applications. It lets you scaffold CRUD grids, dashboards,
reports, and subgrids in seconds, following Lucero Systems conventions. With a clear
handler/view/model structure, Hiccup-based HTML, and seamless DB integration, **ls** helps you
launch your next Clojure web project fast.

---

## ✨ Features

- **Rapid Project Scaffolding**: Instantly create a new web app with `lein new ls your-project-name`.
- **Powerful Code Generators**: Generate CRUD grids, dashboards, reports, and subgrids for any database table.
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

- Edit [`project.clj`](project.clj) and replace all `Change me` and `xxxxx` placeholders with your
  actual project configuration.
- Edit [`resources/private/config.clj`](resources/private/config.clj) and update DB connection
  settings and other relevant values.

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
- Use the [Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva)
  extension to jack in and connect to your REPL.

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

- `lein migrate` — Run all migrations in [`resources/migrations`](resources/migrations/)
- `lein rollback` — Roll back the last migration
- `lein database` — Seed users and other records (see [`src/myapp/models/cdb.clj`](src/myapp/models/cdb.clj))
- `lein grid <table> [:rights [U A S]]` — Scaffold a full CRUD grid (optional `:rights`)
- `lein dashboard <table> [:rights [U A S]]` — Scaffold a dashboard (optional `:rights`)
- `lein report <report> [:rights [U A S]]` — Scaffold a report (optionally restrict access with `:rights`)
- `lein subgrid <table> <parent-table> <parent-key> [:rights [U A S]]` — Scaffold a subgrid linked
  to a parent table (optional `:rights`)

---

## 🗂️ Project Structure

| Feature         | Location                                              | Description                    |
|-----------------|------------------------------------------------------|--------------------------------|
| Grids           | [`src/myapp/handlers/admin/`](src/myapp/handlers/admin/)         | CRUD grids |
| Subgrids        | [`src/myapp/handlers/admin/`](src/myapp/handlers/admin/)         | Child tables linked to parent |
| Dashboards      | [`src/myapp/handlers/`](src/myapp/handlers/)                      | Dashboards |
| Reports         | [`src/myapp/handlers/reports/`](src/myapp/handlers/reports/)      | Reports |
| Private routes  | [`src/myapp/routes/proutes.clj`](src/myapp/routes/proutes.clj)    | Authenticated routes |
| Public routes   | [`src/myapp/routes/routes.clj`](src/myapp/routes/routes.clj)      | Publicly accessible routes |
| Menu (Navbar)   | [`src/myapp/menu.clj`](src/myapp/menu.clj)                        | Bootstrap 5 navbar |

Each grid, subgrid, dashboard, and report contains:
- `controller.clj`
- `model.clj`
- `view.clj`

---

## 📦 Development Environment

- **Java SDK**
- **MySQL** (configured with a password)
- [Leiningen](https://leiningen.org)
- [VS Code](https://code.visualstudio.com/) with
  [Calva: Clojure & ClojureScript](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva)
  extension

---

## 💡 Tips & Best Practices

- All code generation and migrations are managed via Leiningen commands.
- The Bootstrap 5 navbar is fully customizable in [`src/myapp/menu.clj`](src/myapp/menu.clj).
- Migrations are stored in [`resources/migrations/`](resources/migrations/).
- Use the provided code generators to keep your codebase consistent and DRY.
- Take advantage of Hiccup for safe, composable HTML rendering.
- **Subgrids**: Use subgrids to create master‑detail relationships. Example: a `users` table with a
  `usercontacts` subgrid.
- **Troubleshooting tip**: If something doesn’t load or routes don’t update, “touch”
  `src/myapp/core.clj` (just save this file), then reload the page. This forces a reload.
- **Naming tip (avoid underscores)**: For handler/view/model folders and route names, avoid
  underscores to prevent Clojure namespace conflicts (prefer `usercontacts` over `user_contacts`).
  SQL table/column names can still use underscores.

---

## 📝 Example Usage

```sh
# Generate a standard CRUD grid
lein grid users

# Generate a dashboard
lein dashboard sales

# Generate a report
lein report monthlySummary

# Generate a subgrid for user contacts linked to users table
lein subgrid usercontacts users user_id "Contact Name:contact_name" "Email:email"

# Auto-generate subgrid fields from database schema
lein subgrid usercontacts users user_id

# Restrict access with :rights (any generator)
lein grid users :rights [A S]
lein dashboard sales :rights ["A" "S"]
lein report monthlySummary :rights [U]
lein subgrid usercontacts users user_id :rights [A S]
```

---

## 🔐 Access control with :rights

All generators accept an optional `:rights` parameter to control who can access generated pages.
The vector is checked in the generated controller via `allowed-rights`.

- Default (if omitted): ["U" "A" "S"]
- Accepts bare tokens or quoted strings: `:rights [U A S]` or `:rights ["U" "A" "S"]`
- Typical levels: "U" (User), "A" (Admin), "S" (Super)

Examples

```sh
lein grid contactos :rights ["U" "A" "S"]
lein grid customers :rights [A S]
lein dashboard kpis :rights [S]
lein report quarterlySales :rights [A]
lein subgrid orderitems orders order_id :rights [A S]
```

In the generated controller, you’ll see:

```clojure
(def allowed-rights ["A" "S"]) ; from your :rights vector
;; requests are allowed only when (user-level request) is one of these
```


## 🔗 Working with Subgrids

Subgrids create master‑detail relationships, linking a child table to a parent via a foreign key.
Use cases include:

- Users and their contact information
- Orders and order items
- Companies and their employees
- Categories and products

### Creating a Subgrid

**Syntax:**
```sh
lein subgrid <child-table> <parent-table> <foreign-key> [field-specifications...]
```

**Parameters:**
- `<child-table>`: The name of the child table (subgrid)
- `<parent-table>`: The name of the parent table
- `<foreign-key>`: The foreign key column in the child table that references the parent
- `[field-specifications...]`: Optional field specifications in the format `"Label:field_name"`

**Examples:**

1. Auto-generate fields from database schema
   ```sh
lein subgrid usercontacts users user_id
   ```
Automatically detects all columns in `usercontacts` (except `id` and `user_id`) and creates labels.

2. Specify custom fields
   ```sh
lein subgrid usercontacts users user_id "Contact Name:contact_name" "Email:email" "Phone:phone_number"
   ```

3. Complex example with user roles
   ```sh
lein subgrid userroles users user_id "Role Name:role_name" "Permissions:permissions" "Active:is_active"
   ```

### What Gets Generated

When you create a subgrid, the following files are generated:

1. **Controller** (`src/myapp/handlers/admin/<table>/controller.clj`)
   - RESTful endpoints for CRUD operations
   - Parent-aware filtering (shows only records belonging to the parent)
   - Automatic foreign key handling

2. **Model** (`src/myapp/handlers/admin/<table>/model.clj`)
   - Database queries filtered by parent relationship
   - Foreign key constraint handling

3. **View** (`src/myapp/handlers/admin/<table>/view.clj`)
   - Bootstrap 5 modal-based interface
   - Embedded within parent grid
   - DataTables integration with filtering

4. **Routes** (automatically added to `src/myapp/routes/proutes.clj`)
   - GET, POST, PUT, DELETE routes for the subgrid
   - Parent-aware routing

### Database Requirements

For subgrids to work properly, ensure your database tables have:

1. **Primary keys**: Both parent and child tables should have primary key columns (typically `id`)
2. **Foreign key**: Child table must have a foreign key column referencing the parent table
3. **Proper naming**: Follow `<parent_table>_id` for FKs (e.g., `user_id` for users)

**Example database schema:**
```sql
-- Parent table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

-- Child table (subgrid)
CREATE TABLE user_contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    contact_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Integration with Parent Grids

Subgrids are automatically integrated into their parent grids as modal windows. When viewing a parent grid:

1. Each parent record will have action buttons to manage its subgrid records
2. Clicking on a subgrid button opens a modal with the child records
3. Full CRUD operations are available within the modal
4. Changes are immediately reflected without page refresh

### Defining subgrids in a grid view (parent-side wiring)

To plug subgrids into a parent grid, pass a `:subgrids` vector in the `args` map to the grid view.
When present, the view uses `build-grid-with-subgrids` under the hood.

Keys per subgrid entry:
- `:title` — Display title for the subgrid
- `:table-name` — Child table name
- `:foreign-key` — FK column in child table pointing to the parent (e.g., `user_id`)
- `:href` — Subgrid endpoint from the generator. Format: `/admin/<child><parent>` (concatenate
  child+parent), e.g. `/admin/usercontactsusers`
- `:icon` — Optional Bootstrap icon class
- `:label` — Button/trigger label

Example (users grid with Contacts and Roles subgrids):

```clojure
(ns myapp.handlers.admin.users.view
  (:require [myapp.models.grid :refer [build-grid build-grid-with-subgrids]]
            [myapp.models.form :refer [form build-field build-modal-buttons]]))

(defn users-view
  [title rows]
  (let [labels ["Name" "Email"]
        db-fields [:firstname :email]
        fields (apply array-map (interleave db-fields labels))
        table-id "users_table"
        href "/admin/users"
    args {:new true :edit true :delete true
      :subgrids [{:title "Contacts"
          :table-name "usercontacts"
          :foreign-key "user_id"
          :href "/admin/usercontactsusers"
          :icon "bi bi-people"
          :label "Contacts"}
         {:title "Roles"
          :table-name "userroles"
          :foreign-key "user_id"
          :href "/admin/userrolesusers"
          :icon "bi bi-shield-lock"
          :label "Roles"}]}]
    (build-grid-with-subgrids title rows table-id fields href args)))
```

Notes:
- The subgrid `:href` follows `/admin/<child><parent>`; e.g., `usercontacts` under `users` becomes
  `/admin/usercontactsusers`.
- If you don’t pass `:subgrids`, the default rendering uses `build-grid` without subgrid actions.

### Best Practices for Subgrids

1. **Naming Convention**: Use descriptive names that clearly indicate the relationship (e.g., `usercontacts`, `orderitems`)
2. **Foreign Key Naming**: Follow the `<parent_table>_id` convention for consistency
3. **Field Selection**: When specifying custom fields, choose the most relevant ones for the subgrid view
4. **Database Constraints**: Always use proper foreign key constraints with appropriate CASCADE options
5. **Indexing**: Add indexes on foreign key columns for better performance

---

## 🌐 Why Choose ls?

- **SEO-Ready**: Clean, semantic HTML and best practices for discoverability.
- **Enterprise-Grade**: Built for Lucero Systems, but flexible for any Clojure web project.
- **Community-Driven**: Contributions welcome! Join the growing Clojure web community.
- **Documentation & Support**: Inline comments, clear structure, and responsive maintainers.

---

## 📖 API Documentation: ls Leiningen Template

This document overviews the main namespaces, functions, and code generation APIs in the **ls**
Leiningen template.

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

The **ls** template generates a Clojure web app with a modular handler/view/model structure,
automatic CRUD, dashboard, and report scaffolding, and seamless DB integration. The generated code
is idiomatic, extensible, and ready for production.

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

- `lein grid <table> [:rights [U A S]]` — Scaffold a CRUD grid (optional `:rights`).

- `lein dashboard <table> [:rights [U A S]]` — Scaffold a dashboard (optional `:rights`).

- `lein report <report> [:rights [U A S]]` — Scaffold a report (optional `:rights`).

- `lein subgrid <table> <parent-table> <parent-key> [:rights [U A S]]` — Scaffold a subgrid linked
  to a parent table (optional `:rights`).

- `lein migrate` &mdash; Run all migrations in `resources/migrations/`.

- `lein rollback` &mdash; Roll back the last migration.

- `lein database` &mdash; Seed users and other records (see `src/myapp/models/cdb.clj`).

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
- For questions or contributions, open an issue or pull request on the
  [GitHub repository](https://github.com/your-org/ls).

---

&copy; Lucero Systems. All rights reserved.

<!--
SEO keywords: Clojure API docs, Leiningen template, CRUD generator, Clojure web app, Lucero Systems,
handler/view/model, code generation, project structure, enterprise Clojure, dashboard/report/CRUD APIs
-->
