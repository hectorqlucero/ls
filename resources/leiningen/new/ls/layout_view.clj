(ns {{name}}.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn menus-private []
  (list
  [:nav.navbar.navbar-expand-sm.navbar-info.bg-info.fixed-top
   [:a.navbar-brand {:href "/"
                     :style "margin-bottom: 0;"} "Mi Inventario"]
   [:button.navbar-toggler {:type "button"
                            :data-toggle "collapse"
                            :data-target "#navbarSupportedContent"
                            :aria-controls "navbarSupportedContent"
                            :aria-expanded "false"
                            :aria-label "Toggle navigation"}
    [:span.navbar-toggler-icon]]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}
    [:ul.navbar-nav.mr-auto {:id "cum"}
     [:li.nav-item [:a.nav-link {:href "/inventario"} "Inventario"]]
     [:li.nav-item [:a.nav-link {:href "/compras"} "Compras"]]
     [:li.nav-item [:a.nav-link {:href "/ordenes"} "Ordenes"]]
     [:li.nav-item.dropdown
      [:a.nav-link.dropdown-toggle {:href "#"
                                    :id "navbardrop"
                                    :data-toggle "dropdown"} "Reportes"]
      [:div.dropdown-menu
       [:a.dropdown-item {:href "/consultas/inventario"} "Inventario"]
       [:a.dropdown-item {:href "/consultas/recibido"} "Recibido"]
       [:a.dropdown-item {:href "/consultas/enviado"} "Enviado"]
       [:a.dropdown-item {:href "/consultas/reordenar"} "Reordenar"]]]
     [:li.nav-item [:a.nav-link {:href "/table_ref/barcodes"} "Codigo de barras"]]
     [:li.nav-item [:a.nav-link {:href "/logoff"} "Salir"]]]]]))

(defn menus-public []
  (list
  [:nav.navbar.navbar-expand-sm.navbar-info.bg-info.fixed-top
   [:a.navbar-brand {:href "/"
                     :style "margin-bottom: 0;"} "Mi Inventario"]
   [:button.navbar-toggler {:type "button"
                            :data-toggle "collapse"
                            :data-target "#navbarSupportedContent"
                            :aria-controls "navbarSupportedContent"
                            :aria-expanded "false"
                            :aria-label "Toggle navigation"}
    [:span.navbar-toggler-icon]]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}
    [:ul.navbar-nav.mr-auto {:id "cum"}
     [:li.nav-item [:a.nav-link {:href "/login"} "Entrar"]]]]]))

(defn extra-css []
  (list
    (include-css "/easyui/themes/default/easyui.css")
    (include-css "/easyui/themes/icon.css")
    (include-css "/easyui/themes/color.css")
    (include-css "/css/loader.css")))


(defn scripts []
  (list
    (include-js "/easyui/jquery.min.js")
    (include-js "/popper/popper.min.js")
    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/easyui/jquery.easyui.min.js")
    (include-js "/easyui/plugins/jquery.datagrid.js")
    (include-js "/easyui/datagrid-detailview.js")
    (include-js "/js/jquery.maskedinput.min.js")
    (include-js "/easyui/locale/easyui-lang-es.js")
    (include-js "/js/defaultGrid.js")
    (include-js "/js/extra.js")))

(defn application [title ok js & content]
  (html5 {:ng-app "Inventario" :lang "es"}
         [:head
          [:title title]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1.0, shrink-to-fit=no"}]
          (include-css "/bootstrap/css/bootstrap.min.css")
          (include-css "/bootstrap/css/lumen.min.css")
          (extra-css)
          ]
         [:body
          (cond
            (= ok 0) (menus-public)
            (= ok 1) (menus-private))
          [:div.loader]
          [:div.container-fluid {:style "margin-top:75px;"} content]
          (scripts)
          js]))

(defn error-404 [error return-url]
  [:div
   [:p [:h3 [:b "Error: "]] error]
   [:p [:h3 [:a {:href return-url} "Clic aqui para " [:strong "Regresar"]]]]])
