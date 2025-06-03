(ns {{name}}.layout
  (:require
   [clj-time.core :as t]
   [clojure.string :as str]
   [hiccup.page :refer [html5 include-css include-js]]
   [{{name}}.models.crud :refer [config]]
   [{{name}}.models.util :refer [user-level user-name]]))

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
     [:a.nav-link.px-3.fw-500 {:href href
                               :data-id data-id
                               :class (when is-active "active")
                               :aria-current (when is-active "page")
                               :onclick "localStorage.setItem('active-link', this.dataset.id)"
                               :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
                                      margin: 0 2px; 
                                      border-radius: 8px; 
                                      position: relative;
                                      color: #4a5568;
                                      font-weight: 500;
                                      font-size: 0.95rem;
                                      padding: 0.75rem 1rem !important;
                                      text-decoration: none;
                                      background: linear-gradient(135deg, transparent 0%, transparent 100%);
                                      border: 1px solid transparent;"
                               :onmouseover "this.style.background='linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)'; 
                                           this.style.color='#2d3748'; 
                                           this.style.borderColor='#e2e8f0'; 
                                           this.style.transform='translateY(-1px)'; 
                                           this.style.boxShadow='0 4px 12px rgba(0,0,0,0.1)';"
                               :onmouseout (if is-active
                                             "this.style.background='linear-gradient(135deg, #667eea 0%, #764ba2 100%)'; 
                                             this.style.color='white'; 
                                             this.style.borderColor='#667eea'; 
                                             this.style.transform='none'; 
                                             this.style.boxShadow='0 2px 8px rgba(102,126,234,0.3)';"
                                             "this.style.background='transparent'; 
                                             this.style.color='#4a5568'; 
                                             this.style.borderColor='transparent'; 
                                             this.style.transform='none'; 
                                             this.style.boxShadow='none';")}
      label]]))

(defn build-dropdown-link [request href label]
  (let [uri (:uri request)
        is-active (= uri href)]
    [:li [:a.dropdown-item.mx-1.px-3 {:href href
                                      :class (when is-active "active")
                                      :aria-current (when is-active "page")
                                      :data-id (generate-data-id href)
                                      :onclick "localStorage.setItem('active-link', this.dataset.id)"
                                      :style "transition: all 0.3s ease; 
                                             margin: 2px 0; 
                                             border-radius: 6px; 
                                             padding: 0.65rem 1rem;
                                             color: #4a5568;
                                             font-weight: 500;
                                             font-size: 0.9rem;
                                             text-decoration: none;
                                             background: transparent;
                                             border: none;"
                                      :onmouseover "this.style.background='linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%)'; 
                                                   this.style.color='#2d3748'; 
                                                   this.style.transform='translateX(4px)';"
                                      :onmouseout (if is-active
                                                    "this.style.background='linear-gradient(135deg, #667eea 0%, #764ba2 100%)'; 
                                                    this.style.color='white'; 
                                                    this.style.transform='translateX(4px)';"
                                                    "this.style.background='transparent'; 
                                                    this.style.color='#4a5568'; 
                                                    this.style.transform='none';")}
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
     [:a.nav-link.dropdown-toggle.px-3.fw-500
      {:href "#"
       :id dropdown-id
       :data-id data-id
       :onclick "localStorage.setItem('active-link', this.dataset.id)"
       :role "button"
       :data-bs-toggle "dropdown"
       :aria-expanded "false"
       :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
              margin: 0 2px; 
              border-radius: 8px; 
              color: #4a5568;
              font-weight: 500;
              font-size: 0.95rem;
              padding: 0.75rem 1rem !important;
              text-decoration: none;
              background: linear-gradient(135deg, transparent 0%, transparent 100%);
              border: 1px solid transparent;"
       :onmouseover "this.style.background='linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)'; 
                    this.style.color='#2d3748'; 
                    this.style.borderColor='#e2e8f0'; 
                    this.style.transform='translateY(-1px)'; 
                    this.style.boxShadow='0 4px 12px rgba(0,0,0,0.1)';"
       :onmouseout "this.style.background='transparent'; 
                   this.style.color='#4a5568'; 
                   this.style.borderColor='transparent'; 
                   this.style.transform='none'; 
                   this.style.boxShadow='none';"}
      label]
     [:ul.dropdown-menu.shadow.border-0.rounded.mt-2 {:aria-labelledby dropdown-id
                                                      :style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                                                             border: 1px solid #e2e8f0 !important;
                                                             box-shadow: 0 10px 25px rgba(0,0,0,0.15) !important;
                                                             border-radius: 12px !important;
                                                             padding: 0.5rem;
                                                             min-width: 200px;
                                                             backdrop-filter: blur(10px);"}
      (build-menu request items)]]))

;; MENU CONFIGURATION - Single place to define all menu items
;; You can define report-items and admin-items as an example to make menu maintenance easier

(def reports-items
  [["/reports/users" "Users"]])

(def admin-items
  [["/admin/users" "Users" "S"]]) ;note adding a role "S" is to only allow system users to access"

(def menu-config
  {:nav-links [["/" "Home"]
               ["/users" "Dashboard"]]

   :dropdowns {:reports {:id "navdrop0"
                         :data-id "reports"
                         :label "Reports"
                         :items reports-items}

               :admin {:id "navdrop1"
                       :data-id "admin"
                       :label "Administration"
                       :items admin-items}}})

;; HELPER FUNCTIONS
(defn menu-item->map [[href label & [role]]]
  {:href href :label label :role role})

(defn create-nav-links [request nav-links]
  (map (fn [[href label]] (build-link request href label)) nav-links))

(defn create-dropdown [request {:keys [id data-id label items]}]
  (let [menu-items (map menu-item->map items)]
    (build-dropdown request id data-id label menu-items)))

(defn navbar-structure [brand-content nav-content]
  [:nav.navbar.navbar-expand-lg.navbar-light.fixed-top
   {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
           border-bottom: 1px solid #e2e8f0;
           box-shadow: 0 4px 20px rgba(0,0,0,0.08);
           backdrop-filter: blur(10px);
           -webkit-backdrop-filter: blur(10px);
           padding: 0.75rem 0;
           z-index: 1030;"}
   [:div.container-fluid
    brand-content
    [:button.navbar-toggler.border-0 {:type "button"
                                      :data-bs-toggle "collapse"
                                      :data-bs-target "#collapsibleNavbar"
                                      :aria-controls "collapsibleNavbar"
                                      :aria-expanded "false"
                                      :aria-label "Toggle navigation"
                                      :style "border-radius: 8px; 
                                             padding: 0.5rem;
                                             background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
                                             transition: all 0.3s ease;"
                                      :onmouseover "this.style.background='linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%)'; 
                                                   this.style.transform='scale(1.05)';"
                                      :onmouseout "this.style.background='linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%)'; 
                                                  this.style.transform='scale(1)';"}
     [:span.navbar-toggler-icon {:style "background-image: url(\"data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%2833, 37, 41, 0.75%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e\");"}]]
    [:div#collapsibleNavbar.collapse.navbar-collapse nav-content]]])

(defn brand-logo []
  [:a.navbar-brand.fw-bold.fs-5 {:href "#"
                                 :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
                                        color: #2d3748;
                                        text-decoration: none;
                                        display: flex;
                                        align-items: center;
                                        font-weight: 700;
                                        font-size: 1.25rem;"
                                 :onmouseover "this.style.transform='scale(1.05)'; 
                                              this.style.color='#4a5568';"
                                 :onmouseout "this.style.transform='scale(1)'; 
                                             this.style.color='#2d3748';"}
   [:img.me-2 {:src "/images/logo.png"
               :alt (:site-name config)
               :style "width: 42px;
                      height: 42px;
                      filter: drop-shadow(0 3px 8px rgba(0,0,0,0.15));
                      border-radius: 8px;
                      transition: all 0.3s ease;"}]
   [:span.d-none.d-md-inline {:style "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                     -webkit-background-clip: text;
                                     -webkit-text-fill-color: transparent;
                                     background-clip: text;
                                     font-weight: 700;"}
    (:site-name config)]])

(defn logout-button [request]
  [:li.nav-item.ms-3
   [:a.btn.btn-sm.px-3
    {:href "/home/logoff"
     :onclick "localStorage.removeItem('active-link')"
     :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
            font-weight: 500; 
            border-radius: 8px;
            padding: 0.65rem 1.25rem;
            background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%);
            border: 1px solid #e53e3e;
            color: white;
            text-decoration: none;
            display: flex;
            align-items: center;
            font-size: 0.9rem;"
     :onmouseover "this.style.background='linear-gradient(135deg, #c53030 0%, #9c2626 100%)'; 
                  this.style.borderColor='#c53030'; 
                  this.style.transform='translateY(-2px)'; 
                  this.style.boxShadow='0 6px 16px rgba(197,48,48,0.4)';"
     :onmouseout "this.style.background='linear-gradient(135deg, #e53e3e 0%, #c53030 100%)'; 
                 this.style.borderColor='#e53e3e'; 
                 this.style.transform='none'; 
                 this.style.boxShadow='none';"}
    [:i.bi.bi-box-arrow-right.me-1 {:style "font-size: 0.9rem;"}]
    (str "Logout " (user-name request))]])

;; MENU FUNCTIONS
(defn menus-private [request]
  (let [{:keys [nav-links dropdowns]} menu-config]
    (navbar-structure
     (brand-logo)
     [:ul.navbar-nav.ms-auto.align-items-lg-center {:style "gap: 0.25rem;"}
      (create-nav-links request nav-links)
      (create-dropdown request (:reports dropdowns))
      (create-dropdown request (:admin dropdowns))
      (logout-button request)])))

(defn menus-public []
  (navbar-structure
   (brand-logo)
   [:ul.navbar-nav.ms-auto.align-items-lg-center
    (build-link {} "/" "Home")
    [:li.nav-item.ms-3
     [:a.btn.btn-sm.px-3
      {:href "/home/login"
       :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
              font-weight: 500; 
              border-radius: 8px;
              padding: 0.65rem 1.25rem;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              border: 1px solid #667eea;
              color: white;
              text-decoration: none;
              display: flex;
              align-items: center;
              font-size: 0.9rem;"
       :onmouseover "this.style.background='linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%)'; 
                    this.style.borderColor='#5a67d8'; 
                    this.style.transform='translateY(-2px)'; 
                    this.style.boxShadow='0 6px 16px rgba(102,126,234,0.4)';"
       :onmouseout "this.style.background='linear-gradient(135deg, #667eea 0%, #764ba2 100%)'; 
                   this.style.borderColor='#667eea'; 
                   this.style.transform='none'; 
                   this.style.boxShadow='none';"}
      [:i.bi.bi-box-arrow-in-right.me-1 {:style "font-size: 0.9rem;"}]
      "Login"]]]))

(defn menus-none []
  (navbar-structure (brand-logo) nil))

;; ASSETS
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
   (include-js "/js/extra.js")
   [:script
    "document.addEventListener('DOMContentLoaded', function () {
       var id = localStorage.getItem('active-link');
       if (id) {
         var el = document.querySelector('.navbar [data-id=\"' + id + '\"]');
         if (el) {
           el.classList.add('active');
           el.setAttribute('aria-current', 'page');
           el.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
           el.style.color = 'white';
           el.style.borderColor = '#667eea';
           el.style.boxShadow = '0 2px 8px rgba(102,126,234,0.3)';
           
           var dropdownItem = el.closest('.dropdown-item');
           if (dropdownItem) {
             dropdownItem.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
             dropdownItem.style.color = 'white';
             dropdownItem.style.transform = 'translateX(4px)';
             
             var dropdown = el.closest('.dropdown');
             if (dropdown) {
               var dropdownToggle = dropdown.querySelector('.dropdown-toggle');
               if (dropdownToggle) {
                 dropdownToggle.classList.add('active');
                 dropdownToggle.setAttribute('aria-current', 'page');
                 dropdownToggle.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
                 dropdownToggle.style.color = 'white';
                 dropdownToggle.style.borderColor = '#667eea';
                 dropdownToggle.style.boxShadow = '0 2px 8px rgba(102,126,234,0.3)';
               }
             }
           }
         }
       }
     });"]))

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
          [:div.container.flex-nowrap.overflow-auto.margin-top {:style "margin-top:75px;margin-bottom:25px;"}
           (cond
             (= ok -1) (menus-none)
             (= ok 0) (menus-public)
             (> ok 0) (menus-private request))
           [:div {:style "padding-left:14px;"} content]]
          (app-js)
          js
          [:footer.bg-light.text-center.fixed-bottom
           [:span "Copyright © "
            (t/year (t/now)) " " (:company-name config) " - All Rights Reserved"]]]))

(defn error-404 [content return-url]
  (html5 {:ng-app (:site-name config) :lang "es"}
         [:head
          [:title "Mesaje"]
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
          (app-css)
          [:link {:rel "shortcut icon" :type "image/x-icon" :href "data:image/x-icon;,"}]]
         [:body
          [:div.container.flex-nowrap.overflow-auto.margin-top {:style "margin-top:75px;margin-bottom:25px;"}
           (menus-none)
           [:div {:style "padding-left:14px;"}
            [:div.text-center
             [:p [:h3 [:b "Message: "]] [:h3 content]]
             [:p [:h3 [:a.text-info {:href return-url} "Click here to " [:strong "Continue"]]]]]]]
          (app-js)
          nil
          [:footer.bg-light.text-center.fixed-bottom
           [:span "Copyright © "
            (t/year (t/now)) " " (:company-name config) " - All Rights Reserved"]]]))
