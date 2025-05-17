(ns {{name}}.layout
  (:require
   [clj-time.core :as t]
   [hiccup.page :refer [html5 include-css include-js]]
   [{{name}}.models.crud :refer [config]]
   [{{name}}.models.util :refer [user-level user-name]]))

(defn build-reports [request]
  (when (or (= (user-level request) "A") (= (user-level request) "S"))
    (list
     nil
     (when (or
            (= (user-level request) "A")
            (= (user-level request) "S"))
       (list
        [:li [:a.dropdown-item {:href "/reports/users"} "Users"]]
        (when (= (user-level request) "S")
          nil))))))

(defn build-admin [request]
  (list
   nil
   (when (or
          (= (user-level request) "A")
          (= (user-level request) "S"))
     (list
      nil
      (when (= (user-level request) "S")
        [:li [:a.dropdown-item {:href "/admin/users"} "Users"]])))))

(defn menus-private [request]
  (list
   [:nav.navbar.navbar-expand-lg.navbar-light.bg-light.fixed-top
    [:div.container-fluid
     [:a.navbar-brand {:href "#"}
      [:img {:src "/images/logo.png"
             :alt (:site-name config)
             :style "width:40px;height:40px;"}]]
     [:button.navbar-toggler {:type "button"
                              :data-bs-toggle "collapse"
                              :data-bs-target "#collapsibleNavbar"
                              :aria-controls "collapsibleNavbar"
                              :aria-expanded "false"
                              :aria-label "Toggle navigation"}
      [:span.navbar-toggler-icon]]
     [:div#collapsibleNavbar.collapse.navbar-collapse
      [:ul.navbar-nav.ms-auto
       [:li.nav-item [:a.nav-link.active {:href "/"
                                          :aria-current "page"} "Home"]]
       [:li.nav-item [:a.nav-link {:href "/users"} "Dashboard"]]
       (when
        (or
         (= (user-level request) "U")
         (= (user-level request) "A")
         (= (user-level request) "S"))
         [:li.nav-item.dropdown
          [:a.nav-link.dropdown-toggle {:href "#"
                                        :id "navdrop0"
                                        :role "button"
                                        :data-bs-toggle "dropdown"
                                        :aria-expanded "false"} "Reports"]
          [:ul.dropdown-menu {:aria-labelledby "navdrop0"}
           (build-reports request)]])
       (when
        (or
         (= (user-level request) "U")
         (= (user-level request) "A")
         (= (user-level request) "S"))
         [:li.nav-item.dropdown
          [:a.nav-link.dropdown-toggle {:href "#"
                                        :id "navdrop0"
                                        :role "button"
                                        :data-bs-toggle "dropdown"
                                        :aria-expanded "false"} "Administration"]
          [:ul.dropdown-menu {:aria-labelledby "navdrop0"}
           (build-admin request)]])
       [:li.nav-item [:a.nav-link {:href "/home/logoff"} (str "Logout [" (user-name request) "]")]]]]]]))

(defn menus-public []
  (list
   [:nav.navbar.navbar-expand-lg.navbar-light.bg-light.fixed-top
    [:div.container-fluid
     [:a.navbar-brand {:href "#"}
      [:img {:src "/images/logo.png"
             :alt (:site-name config)
             :style "width:40px;height:40px;"}]]
     [:button.navbar-toggler {:type "button"
                              :data-bs-toggle "collapse"
                              :data-bs-target "#collapsibleNavbar"
                              :aria-controls "collapsibleNavbar"
                              :aria-expanded "false"
                              :aria-label "Toggle navigation"}
      [:span.navbar-toggler-icon]]
     [:div#collapsibleNavbar.navbar-collapse
      [:ul.navbar-nav.ms-auto
       [:li.nav-item [:a.nav-link.active {:aria-current "page"
                                          :href "/"} "Home"]]
       [:li.nav-item [:a.nav-link {:href "/home/login"
                                   :aria-current "page"} "Login"]]]]]]))

(defn menus-none []
  (list
   [:nav.navbar.navbar-expand-lg.navbar-light.bg-light.fixed-top
    [:div.container-fluid
     [:a.navbar-brand {:href "#"}
      [:img {:src "/images/logo.png"
             :alt (:site-name config)
             :style "width:40px;height:40px;"}]]
     [:button.navbar-toggler {:type "button"
                              :data-bs-toggle "collapse"
                              :data-bs-target "#collapsibleNavbar"
                              :aria-controls "collapsibleNavbar"
                              :aria-expanded "false"
                              :aria-label "Toggle navigation"}
      [:span.navbar-toggler-icon]]
     [:div#collapsibleNavbar.collapse.navbar-collapse]]]))

(defn app-css []
  (list
   (include-css "/bootstrap5/css/bootstrap.min.css")
   (include-css "/bootstrap-icons/font/bootstrap-icons.css")
   (include-css "/bootstrap-table-master/dist/bootstrap-table.min.css")
   (include-css "/css/extra.css")))

(defn app-js []
  (list
   (include-js "/js/jquery.min.js")
   (include-js "/bootstrap5/js/bootstrap.bundle.min.js")
   (include-js "/bootstrap-table-master/dist/extensions/export/tableExport.min.js")
   (include-js "/bootstrap-table-master/dist/extensions/export/jspdf.umd.min.js")
   (include-js "/bootstrap-table-master/dist/extensions/export/bootstrap-table.min.js")
   (include-js "/bootstrap-table-master/dist/extensions/export/bootstrap-table-export.min.js")
   (include-js "/bootstrap-table-master/dist/extensions/export/bootstrap-table-print.min.js")
   (include-js "/bootstrap-table-master/dist/locale/bootstrap-table-en-US.min.js")
   (include-js "/js/extra.js")))

(defn application [request title ok js & content]
  (html5 {:ng-app (:site-name config) :lang "en"}
         [:head
          [:title (if title
                    title
                    (:site-name config))]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1"}]
          (app-css)
          [:link {:rel "shortcut icon"
                  :type "image/x-icon"
                  :href "data:image/x-icon;,"}]]
         [:body
          [:div.container.flex-nowrap.overflow-auto.margin-top {:style "margin-top:75px;margin-bottom:25px;"}
           (cond
             (= ok -1) (menus-none)
             (= ok 0) (menus-public)
             (> ok 0) (menus-private request))
           [:div {:style "padding-left:14px;"} content]]
          (app-js)
          js
          [:footer.bg-light.text-center.fixed-bottom
           [:span  "Copyright &copy;" (t/year (t/now)) " " (:company-name config) " - All Rights Reserved"]]]))

(defn error-404 [content return-url]
  (html5 {:ng-app (:site-name config) :lang "es"}
         [:head
          [:title "Mesaje"]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1"}]
          (app-css)
          [:link {:rel "shortcut iconcompojure"
                  :type "image/x-icon"
                  :href "data:image/x-icon;,"}]]
         [:body
          [:div.container.flex-nowrap.overflow-auto.margin-top {:style "margin-top:75px;margin-bottom:25px;"}
           (menus-none)
           [:div {:style "padding-left:14px;"}
            [:div.text-center
             [:p [:h3 [:b "Message: "]] [:h3 content]]
             [:p [:h3 [:a.text-info {:href return-url} "Clic here to " [:strong "Continue"]]]]]]]

          (app-js)
          nil
          [:footer.bg-light.text-center.fixed-bottom
           [:span  "Copyright &copy;" (t/year (t/now)) " " (:company-name config) " - All Rights Reserved"]]]))
