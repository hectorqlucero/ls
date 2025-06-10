(ns {{name}}.models.grid
  (:require
   [clojure.string :as st]))

;; =============================================================================
;; Grid/Table Builders (using DataTables)
;; =============================================================================

(defn build-grid-head
  [href fields & args]
  (let [args (first args)
        new-record (:new args)]
    [:thead
     [:tr
      (for [field fields]
        [:th.text-nowrap.text-uppercase.fw-semibold
         {:data-sortable "true"
          :data-field (key field)}
         (st/upper-case (val field))])
      [:th.text-center
       {:style "width:1%; white-space:nowrap; padding-left:0.25rem; padding-right:0.25rem;"}
       (let [disabled? (not new-record)]
         [:a.btn.btn-success.btn-lg.fw-semibold.shadow-sm.new-record-btn
          (merge
           {:href "#"
            :tabindex (when disabled? -1)
            :aria-disabled (when disabled? "true")
            :class (str "btn btn-success btn-lg fw-semibold shadow-sm new-record-btn"
                        (when disabled? " disabled"))}
           (when-not disabled?
             {:data-url (str href "/add-form")
              :data-bs-toggle "modal"
              :data-bs-target "#exampleModal"}))
          [:i.bi.bi-plus-lg.me-2]
          "New Record"])]]]))

(defn build-grid-body
  [rows href fields & args]
  (let [args (first args)
        edit (:edit args)
        delete (:delete args)]
    [:tbody
     (for [row rows]
       [:tr
        (for [field fields]
          [:td.text-truncate.align-middle
           ((key field) row)])
        [:td.text-center.align-middle
         {:style "width:1%; white-space:nowrap; padding-left:0.25rem; padding-right:0.25rem;"}
         [:div.d-flex.justify-content-center.align-items-center.gap-2
          ;; Edit button
          (let [disabled? (not edit)]
            [:a.btn.btn-warning.btn-lg.fw-semibold.shadow-sm.rounded-pill.edit-record-btn
             (merge
              {:href "#"
               :tabindex (when disabled? -1)
               :aria-disabled (when disabled? "true")
               :class (str "btn btn-warning btn-lg fw-semibold shadow-sm rounded-pill edit-record-btn"
                           (when disabled? " disabled"))}
              (when-not disabled?
                {:data-url (str href "/edit-form/" (:id row))
                 :data-bs-toggle "modal"
                 :data-bs-target "#exampleModal"}))
             [:i.bi.bi-pencil.me-2]
             "Edit"])
          ;; Delete button
          (let [disabled? (not delete)]
            [:a.btn.btn-danger.btn-lg.fw-semibold.shadow-sm.rounded-pill
             {:href (str href "/delete/" (:id row))
              :tabindex (when disabled? -1)
              :aria-disabled (when disabled? "true")
              :class (str "btn btn-danger btn-lg fw-semibold shadow-sm rounded-pill"
                          (when disabled? " disabled"))}
             [:i.bi.bi-trash.me-2]
             "Delete"])]]])]))

;; --- Unified Table Head ---
(defn unified-table-head
  [fields & [{:keys [actions? new? href]}]]
  [:thead
   [:tr
    (for [field fields]
      [:th.text-nowrap.text-uppercase.fw-semibold.px-2
       {:data-sortable "true"
        :data-field (key field)}
       (st/upper-case (val field))])
    (when actions?
      [:th.text-center.px-2
       {:style "width:1%; white-space:nowrap; padding-left:0.25rem; padding-right:0.25rem;"}
       [:div.d-flex.justify-content-center.align-items-center
        (let [disabled? (not new?)]
          [:a.btn.btn-success.btn-lg.fw-semibold.shadow-sm.new-record-btn
           (merge
            {:href "#"
             :tabindex (when disabled? -1)
             :aria-disabled (when disabled? "true")
             :class (str "btn btn-success btn-lg fw-semibold shadow-sm new-record-btn"
                         (when disabled? " disabled"))}
            (when-not disabled?
              {:data-url (str href "/add-form")
               :data-bs-toggle "modal"
               :data-bs-target "#exampleModal"}))
           [:i.bi.bi-plus-lg.me-2]
           "New Record"])]])]])

;; --- Unified Table Body ---
(defn unified-table-body
  [rows fields]
  [:tbody
   (for [row rows]
     [:tr
      (for [field fields]
        [:td.text-truncate.align-middle
         ((key field) row)])])])

;; --- Unified Grid ---
(defn build-grid
  [title rows table-id fields href & [args]]
  (let [args (or args {})]
    [:div.card.shadow.mb-4
     [:div.card-body.bg-gradient.bg-primary.text-white.rounded-top
      [:h4.mb-0.fw-bold title]]
     [:div.p-3.bg-white.rounded-bottom
      [:div.table-responsive
       [:table.table.table-hover.table-bordered.table-striped.table-sm.compact.align-middle.display.dataTable.w-100
        {:id table-id}
        (unified-table-head fields {:actions? true :new? (:new args) :href href})
        (build-grid-body rows href fields args)]]]]))

;; --- Unified Dashboard ---
(defn build-dashboard
  [title rows table-id fields]
  [:div.card.shadow.mb-4
   [:div.card-body.bg-gradient.bg-primary.text-white.rounded-top
    [:h4.mb-0.fw-bold title]]
   [:div.p-3.bg-white.rounded-bottom
    [:div.table-responsive
     [:table.table.table-hover.table-bordered.table-striped.table-sm.compact.align-middle.display.dataTable.w-100
      {:id table-id}
      (unified-table-head fields)
      (unified-table-body rows fields)]]]])

;; =============================================================================
;; Modal Builder
;; =============================================================================

(defn build-modal
  [title _ form]
  (list
   [:div.modal.fade {:id "exampleModal"
                     :data-bs-backdrop "static"
                     :data-bs-keyboard "false"
                     :tabindex "-1"
                     :aria-labelledby "exampleModalLabel"
                     :aria-hidden "true"}
    [:div.modal-dialog.modal-dialog-centered {:style "max-width: 700px; width: 100%;"}
     [:div.modal-content
      [:div.modal-header.bg-primary.text-white
       [:h1.modal-title.fs-5.fw-bold {:id "exampleModalLabel"
                                      :style "margin: 0;
                                             font-size: 1.25rem;
                                             text-shadow: 0 1px 3px rgba(0,0,0,0.2);
                                             letter-spacing: 0.025em;"}
        title]
       [:button.btn-close
        {:type "button"
         :data-bs-dismiss "modal"
         :aria-label "Close"}]]
      [:div.modal-body.p-0.w-100
       (if (and (vector? form) (#{:div :form} (first form)))
         (let [[tag attrs & body] form
               class-str (-> (or (:class attrs) "")
                             (clojure.string/replace #"container-fluid" "")
                             (clojure.string/replace #"container" "")
                             (clojure.string/replace #"d-flex" "")
                             (clojure.string/replace #"justify-content-center" "")
                             (clojure.string/replace #"align-items-center" "")
                             (str " w-100")
                             clojure.string/trim)
               new-attrs (assoc attrs :class class-str)]
           (into [tag new-attrs] body))
         [:div.w-100 form])]]]]))

(defn modal-script
  []
  [:script
   "
   const myModal = new bootstrap.Modal(document.getElementById('exampleModal'), {
    keyboard: false
   })
   myModal.show();
   "])

;; =============================================================================
;; Example Usage (Documentation)
;; =============================================================================

(comment
  ;; Example usage for main functions in this file

  (build-grid
   "Employee List"
   [{:id 1 :name "Alice" :email "alice@example.com"}
    {:id 2 :name "Bob" :email "bob@example.com"}]
   "employee_table"
   [[:name "Name"] [:email "Email"]]
   "/employees"
   {:new true :edit true :delete true})

  (build-dashboard
   "Employee Dashboard"
   [{:id 1 :name "Alice" :email "alice@example.com"}
    {:id 2 :name "Bob" :email "bob@example.com"}]
   "dashboard_table"
   [[:name "Name"] [:email "Email"]])

  (build-modal
   "Edit Employee"
   nil
   [:form
    {:method "POST" :action "/employees/edit"}
    [:div.mb-3
     [:label.form-label.fw-semibold {:for "name"} "Name"]
     [:input.form-control.form-control-lg {:type "text" :id "name" :name "name" :placeholder "Enter name..." :required true :value ""}]]
    [:div.mb-3
     [:label.form-label.fw-semibold {:for "email"} "Email"]
     [:input.form-control.form-control-lg {:type "email" :id "email" :name "email" :placeholder "Enter email..." :required true :value ""}]]
    [:button.btn.btn-primary {:type "submit"} "Save"]]))
