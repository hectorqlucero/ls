(ns {{name}}.menu)

(def reports-items
  [["/reports/users" "Users"]])

(def admin-items
  [["/admin/users" "Users" "S"]]) ; Only system users

(def menu-config
  {:nav-links [["/" "Home"]
               ["/users" "Users"]]
   :dropdowns {:reports {:id "navdrop0"
                         :data-id "reports"
                         :label "Reports"
                         :items reports-items}
               :admin {:id "navdrop1"
                       :data-id "admin"
                       :label "Administration"
                       :items admin-items}}})
