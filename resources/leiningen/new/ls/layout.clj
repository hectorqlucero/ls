(ns {{name}}.layout
  (:require
   [clj-time.core :as t]
   [clojure.string :as str]
   [hiccup.page :refer [html5]]
   [{{name}}.models.crud :refer [config]]
   [{{name}}.models.util :refer [user-level user-name]]
   [{{name}}.menu :refer [menu-config]]))

(defn generate-data-id [href]
  (-> href
      (str/replace #"^/" "")
      (str/replace #"/" "-")
      (str/replace #"[^a-zA-Z0-9\-]" "")
      (str/replace #"^$" "home")))

(defn build-link [request href label]
  (let [uri (:uri request)
        data-id (generate-data-id href)
        is-active (= uri href)]
    [:li.nav-item
     [:a.nav-link.fw-semibold.px-3.py-2.rounded.transition
      {:href href
       :data-id data-id
       :class (str (when is-active "active bg-gradient text-primary-emphasis shadow-sm"))
       :aria-current (when is-active "page")
       :onclick "localStorage.setItem('active-link', this.dataset.id)"}
      label]]))

(defn build-dropdown-link [request href label]
  (let [uri (:uri request)
        is-active (= uri href)]
    [:li
     [:a.dropdown-item.fw-semibold
      {:href href
       :class (when is-active "active bg-primary-subtle text-primary-emphasis")
       :aria-current (when is-active "page")
       :data-id (generate-data-id href)
       :onclick "localStorage.setItem('active-link', this.dataset.id)"}
      label]]))

(defn build-menu [request items]
  (when (some #{(user-level request)} ["A" "S" "U"])
    (for [{:keys [href label role]} items
          :when (or (nil? role)
                    (= (user-level request) role)
                    (some #{(user-level request)} ["A" "S"]))]
      (build-dropdown-link request href label))))

(defn build-dropdown [request dropdown-id data-id label items]
  (when (some #{(user-level request)} ["A" "S" "U"])
    [:li.nav-item.dropdown
     [:a.nav-link.dropdown-toggle.fw-semibold.px-3.py-2.rounded.transition
      {:href "#"
       :id dropdown-id
       :data-id data-id
       :onclick "localStorage.setItem('active-link', this.dataset.id)"
       :role "button"
       :data-bs-toggle "dropdown"
       :aria-expanded "false"}
      label]
     [:ul.dropdown-menu.shadow-lg.border-0.rounded.mt-2
      {:aria-labelledby dropdown-id}
      (build-menu request items)]]))

;; HELPER FUNCTIONS
(defn menu-item->map [[href label & [role]]]
  {:href href :label label :role role})

(defn create-nav-links [request nav-links]
  (map (fn [[href label]] (build-link request href label)) nav-links))

(defn create-dropdown [request {:keys [id data-id label items]}]
  (let [menu-items (map menu-item->map items)]
    (build-dropdown request id data-id label menu-items)))

(defn brand-logo []
  [:a.navbar-brand.fw-bold.fs-4.d-flex.align-items-center.gap-2 {:href "/"}
   [:img {:src "/images/logo.png"
          :alt (:site-name config)
          :style "width: 44px; height: 44px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.10);"}]
   [:span.d-none.d-md-inline (:site-name config)]])

(defn logout-button [request]
  [:li.nav-item.ms-3
   [:a.btn.btn-outline-danger.btn-sm.px-3.rounded-pill.fw-semibold
    {:href "/home/logoff"
     :onclick "localStorage.removeItem('active-link')"}
    [:i.bi.bi-box-arrow-right.me-1]
    (str "Logout " (user-name request))]])

;; MENU FUNCTIONS
(defn menus-private [request]
  (let [{:keys [nav-links dropdowns]} menu-config]
    [:nav.navbar.navbar-expand-lg.navbar-dark.bg-gradient.bg-primary.shadow-lg.fixed-top
     [:div.container-fluid
      (brand-logo)
      [:button.navbar-toggler
       {:type "button"
        :data-bs-toggle "collapse"
        :data-bs-target "#mainNavbar"
        :aria-controls "mainNavbar"
        :aria-expanded "false"
        :aria-label "Toggle navigation"}
       [:span.navbar-toggler-icon]]
      [:div#mainNavbar.collapse.navbar-collapse
       [:ul.navbar-nav.ms-auto.align-items-lg-center.gap-2
        (create-nav-links request nav-links)
        (create-dropdown request (:reports dropdowns))
        (create-dropdown request (:admin dropdowns))
        (logout-button request)]]]]))

(defn menus-public []
  [:nav.navbar.navbar-expand-lg.navbar-light.bg-white.shadow.fixed-top
   [:div.container-fluid
    (brand-logo)
    [:button.navbar-toggler
     {:type "button"
      :data-bs-toggle "collapse"
      :data-bs-target "#mainNavbar"
      :aria-controls "mainNavbar"
      :aria-expanded "false"
      :aria-label "Toggle navigation"}
     [:span.navbar-toggler-icon]]
    [:div#mainNavbar.collapse.navbar-collapse
     [:ul.navbar-nav.ms-auto.align-items-lg-center.gap-2
      (build-link {} "/" "Home")
      [:li.nav-item.ms-3
       [:a.btn.btn-outline-primary.btn-sm.px-3.rounded-pill.fw-semibold
        {:href "/home/login"}
        [:i.bi.bi-box-arrow-in-right.me-1 {:style "font-size: 0.9rem;"}]
        "Login"]]]]]])

(defn menus-none []
  [:nav.navbar.navbar-expand-lg.navbar-light.bg-white.shadow.fixed-top
   [:div.container-fluid
    (brand-logo)]])

;; ASSETS (CDN)
(defn app-css []
  (list
   [:link {:rel "stylesheet" :href "/vendor/bootstrap.min.css"}]
   [:link {:rel "stylesheet" :href "/vendor/dataTables.bootstrap5.min.css"}]
   [:link {:rel "stylesheet" :href "/vendor/buttons.bootstrap5.min.css"}]
   [:link {:rel "stylesheet" :href "/vendor/jquery.dataTables.min.css"}]
   [:link {:rel "stylesheet" :href "/vendor/buttons.dataTables.min.css"}]
   [:link {:rel "stylesheet" :href "/vendor/bootstrap-icons.css"}]
   [:link {:rel "stylesheet" :href "/vendor/app.css"}])) ; <-- your custom CSS

(defn app-js []
  (list
   [:script {:src "/vendor/jquery-3.7.1.min.js"}]
   [:script {:src "/vendor/bootstrap.bundle.min.js"}]
   [:script {:src "/vendor/jquery.dataTables.min.js"}]
   [:script {:src "/vendor/dataTables.bootstrap5.min.js"}]
   [:script {:src "/vendor/buttons.dataTables.min.js"}]
   [:script {:src "/vendor/buttons.bootstrap5.min.js"}]
   [:script {:src "/vendor/jszip.min.js"}]
   [:script {:src "/vendor/pdfmake.min.js"}]
   [:script {:src "/vendor/vfs_fonts.js"}]
   [:script {:src "/vendor/buttons.html5.min.js"}]
   [:script {:src "/vendor/buttons.print.min.js"}]
   [:script {:src "/vendor/app.js"}])) ; <-- your custom JS

;; LAYOUT FUNCTIONS
(defn application [request title ok js & content]
  (html5 {:ng-app (:site-name config) :lang "en"}
         [:head
          [:title (or title (:site-name config))]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
          (app-css)
          [:link {:rel "shortcut icon" :type "image/x-icon" :href "data:image/x-icon;,"}]]
         [:body
          [:div {:style "height: 70px;"}]
          [:div.container-fluid.pt-3
           {:style "min-height: 100vh; background: #f8fafc;"}
           (cond
             (= ok -1) (menus-none)
             (= ok 0) (menus-public)
             (> ok 0) (menus-private request))
           [:div.container-fluid.px-4
            {:style "margin-top:32px; max-height:calc(100vh - 200px); overflow-y:auto; padding-bottom:80px;"}
            (doall content)]]
          [:div#exampleModal.modal.fade
           {:tabindex "-1" :aria-labelledby "exampleModalLabel" :aria-hidden "true"}
           [:div.modal-dialog
            [:div.modal-content
             [:div.modal-header.bg-primary.text-white
              [:h5#exampleModalLabel.modal-title "Modal"]
              [:button.btn-close {:type "button" :data-bs-dismiss "modal" :aria-label "Close"}]]
             [:div.modal-body]]]]
          (app-js)
          js
          [:footer.bg-light.text-center.fixed-bottom.py-2.shadow-sm
           [:span "Copyright © "
            (t/year (t/now)) " " (:company-name config) " - All Rights Reserved"]]]))

(defn error-404
  ([msg] (error-404 msg nil))
  ([msg redirect-url]
   [:div
    [:h1 "Error 404"]
    [:p msg]
    (when redirect-url
      [:a {:href redirect-url} "Go back"])]))
