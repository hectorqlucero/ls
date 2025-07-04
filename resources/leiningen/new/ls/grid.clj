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
        delete (:delete args)
        field-count (count fields)]
    [:tbody
     (if (empty? rows)
       ;; Show "No data available" message when there are no rows
       ;; Create individual cells to match header count (DataTable requirement)
       [:tr
        ;; First cell with the message
        [:td.text-center.text-muted
         [:em "No data available in table"]]
        ;; Empty cells for remaining data columns
        (for [_ (range (dec field-count))]
          [:td.text-center.text-muted ""])
        ;; Empty cell for actions column
        [:td.text-center.text-muted ""]]
       ;; Normal rows when data exists
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
               "Delete"])]]]))]))

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

;; --- Enhanced Grid Head with Custom New Record URL ---
(defn build-grid-head-with-custom-new
  [fields args href custom-new-url]
  (let [{:keys [new edit delete]} args]
    [:thead
     [:tr
      (for [field fields]
        [:th.text-nowrap.text-uppercase.fw-semibold.px-2
         {:data-sortable "true"
          :data-field (key field)}
         (st/upper-case (val field))])
      [:th.text-center.px-2
       {:style "width:1%; white-space:nowrap; padding-left:0.25rem; padding-right:0.25rem;"}
       [:div.d-flex.justify-content-center.align-items-center
        (let [disabled? (not new)]
          [:a.btn.btn-success.btn-lg.fw-semibold.shadow-sm.new-record-btn
           (merge
            {:href "#"
             :tabindex (when disabled? -1)
             :aria-disabled (when disabled? "true")
             :class (str "btn btn-success btn-lg fw-semibold shadow-sm new-record-btn"
                         (when disabled? " disabled"))}
            (when-not disabled?
              {:data-url custom-new-url
               :data-bs-toggle "modal"
               :data-bs-target "#exampleModal"}))
           [:i.bi.bi-plus-lg.me-2]
           "New Record"])]]]]))

;; --- Build Grid with Custom New Record URL ---
(defn build-grid-with-custom-new
  [title rows table-id fields href args custom-new-url]
  [:div.card.shadow.mb-4
   [:div.card-body.bg-gradient.bg-primary.text-white.rounded-top
    [:h4.mb-0.fw-bold title]]
   [:div.p-3.bg-white.rounded-bottom
    [:div.table-responsive
     [:table.table.table-hover.table-bordered.table-striped.table-sm.compact.align-middle.display.dataTable.w-100
      {:id table-id}
      (build-grid-head-with-custom-new fields args href custom-new-url)
      (build-grid-body rows href fields args)]]]])

;; =============================================================================
;; Subgrid functionality - linking grids via foreign keys
;; =============================================================================

(defn build-subgrid-trigger
  "Creates a trigger button/link to open a subgrid for a specific parent record"
  [parent-record subgrid-config]
  (let [{:keys [title table-name href icon label]} subgrid-config
        parent-id (get parent-record (keyword (:primary-key subgrid-config "id")))
        subgrid-url (str href "?parent_id=" parent-id "&parent_table=" table-name)]
    [:button.btn.btn-info.btn-sm.me-1
     {:type "button"
      :data-subgrid-url subgrid-url
      :data-parent-id parent-id
      :data-subgrid-title (str title " for " (or (:name parent-record)
                                                 (:lastname parent-record)
                                                 (:title parent-record)
                                                 (str "Record #" parent-id)))
      :data-bs-toggle "modal"
      :data-bs-target "#subgridModal"}
     [:i {:class (or icon "bi bi-list-ul")}]
     (when label [:span.ms-1 label])]))

(defn build-enhanced-grid-body
  "Enhanced grid body that includes subgrid triggers"
  [rows href fields args subgrid-configs]
  [:tbody
   (for [row rows]
     [:tr
      (for [field fields]
        [:td.text-truncate.align-middle
         ((key field) row)])
      [:td.text-center.align-middle
       {:style "width:1%; white-space:nowrap; padding-left:0.25rem; padding-right:0.25rem;"}
       [:div.d-flex.justify-content-center.align-items-center.gap-1
        ;; Subgrid triggers
        (for [subgrid-config subgrid-configs]
          (build-subgrid-trigger row subgrid-config))
        ;; Original action buttons
        (let [edit (:edit args)
              delete (:delete args)]
          (list
           ;; Edit button
           (let [disabled? (not edit)]
             [:a.btn.btn-warning.btn-sm.fw-semibold.shadow-sm.rounded-pill.edit-record-btn
              (merge
               {:href "#"
                :tabindex (when disabled? -1)
                :aria-disabled (when disabled? "true")
                :class (str "btn btn-warning btn-sm fw-semibold shadow-sm rounded-pill edit-record-btn"
                            (when disabled? " disabled"))}
               (when-not disabled?
                 {:data-url (str href "/edit-form/" (:id row))
                  :data-bs-toggle "modal"
                  :data-bs-target "#exampleModal"}))
              [:i.bi.bi-pencil.me-1]
              "Edit"])
           ;; Delete button
           (let [disabled? (not delete)]
             [:a.btn.btn-danger.btn-sm.fw-semibold.shadow-sm.rounded-pill
              {:href (str href "/delete/" (:id row))
               :tabindex (when disabled? -1)
               :aria-disabled (when disabled? "true")
               :class (str "btn btn-danger btn-sm fw-semibold shadow-sm rounded-pill"
                           (when disabled? " disabled"))}
              [:i.bi.bi-trash.me-1]
              "Delete"])))]]])])

(defn build-subgrid-modal
  "Modal container for subgrids"
  [subgrid-url]
  [:div.modal.fade {:id "subgridModal"
                    :data-bs-backdrop "static"
                    :data-bs-keyboard "false"
                    :tabindex "-1"
                    :aria-labelledby "subgridModalLabel"
                    :aria-hidden "true"}
   [:div.modal-dialog.modal-xl.modal-dialog-centered
    [:div.modal-content
     [:div.modal-header.bg-info.text-white
      [:h1.modal-title.fs-5.fw-bold {:id "subgridModalLabel"} "Subgrid"]
      [:button.btn-close
       {:type "button"
        :data-bs-dismiss "modal"
        :aria-label "Close"}]]
     [:div.modal-body.p-2
      [:div#subgrid-content {:data-url subgrid-url}
       [:div.text-center.p-4
        [:div.spinner-border.text-primary {:role "status"}]
        [:div.mt-2 "Loading..."]]]]
     [:div.modal-footer
      [:button.btn.btn-secondary
       {:type "button"
        :data-bs-dismiss "modal"}
       "Close"]]]]])

(defn subgrid-javascript
  "JavaScript to handle subgrid interactions"
  []
  [:script
   "
   // Wait for jQuery to be available before executing
   function waitForJQuery() {
     if (typeof $ !== 'undefined') {
       // jQuery is available, initialize subgrid functionality
       $(document).ready(function() {
         // Handle subgrid button clicks
         $(document).on('click', '[data-subgrid-url]', function(e) {
           e.preventDefault();
           var url = $(this).data('subgrid-url');
           var title = $(this).data('subgrid-title');
           var modal = $('#subgridModal');
           
           // Store the current subgrid URL for refreshing after saves
           modal.data('current-subgrid-url', url);
           
           // Set modal title
           modal.find('#subgridModalLabel').text(title);
           
           // Show loading spinner
           modal.find('#subgrid-content').html(`
             <div class='text-center p-4'>
               <div class='spinner-border text-primary' role='status'></div>
               <div class='mt-2'>Loading...</div>
             </div>
           `);
            // Load subgrid content
           $.ajax({
             url: url,
             type: 'GET',
             xhrFields: {
               withCredentials: true
             },
             success: function(data, textStatus, jqXHR) {
               // Check if response is a redirect to login (authentication issue)
               if (typeof data === 'string' && (data.includes('home/login') || data.includes('Login') || data.includes('username') && data.includes('password'))) {
                 modal.find('#subgrid-content').html(`
                   <div class='alert alert-warning'>
                     <i class='bi bi-exclamation-triangle'></i>
                     Session expired. Please refresh the page and log in again.
                   </div>
                 `);
                 return;
               }
               
               modal.find('#subgrid-content').html(data);
               
               // Initialize DataTable for subgrid if it has a table
               var subTable = modal.find('table.dataTable');
               if (subTable.length > 0 && !$.fn.DataTable.isDataTable(subTable)) {
                 subTable.DataTable({
                   responsive: true,
                   pageLength: 10,
                   language: {
                     search: 'Search:',
                     lengthMenu: 'Show _MENU_ entries',
                     info: 'Showing _START_ to _END_ of _TOTAL_ entries',
                     paginate: {
                       first: 'First',
                       last: 'Last',
                       next: 'Next',
                       previous: 'Previous'
                     }
                   }
                 });
               }
             },
             error: function(jqXHR, textStatus, errorThrown) {
               console.log('Subgrid load failed:', textStatus, errorThrown, jqXHR.status);
               if (jqXHR.status === 302 || jqXHR.status === 401) {
                 modal.find('#subgrid-content').html(`
                   <div class='alert alert-warning'>
                     <i class='bi bi-exclamation-triangle'></i>
                     Authentication required. Please refresh the page and log in again.
                   </div>
                 `);
               } else {
                 modal.find('#subgrid-content').html(`
                   <div class='alert alert-danger'>
                     <i class='bi bi-exclamation-triangle'></i>
                     Error loading subgrid content. Status: ${jqXHR.status}
                   </div>
                 `);
               }
             }
           });
         });
         
         // Handle modal cleanup
         $('#subgridModal').on('hidden.bs.modal', function() {
           // Destroy any DataTables in the subgrid to prevent conflicts
           var subTable = $(this).find('table.dataTable');
           if (subTable.length > 0 && $.fn.DataTable.isDataTable(subTable)) {
             subTable.DataTable().destroy();
           }
           
           // Clear the stored URL
           $(this).removeData('current-subgrid-url');
         });
       });
     } else {
       // jQuery not available yet, wait a bit and try again
       setTimeout(waitForJQuery, 100);
     }
   }
   
   // Start waiting for jQuery
   waitForJQuery();
   "])

(defn build-grid-with-subgrids
  "Enhanced version of build-grid that supports subgrids.
   Expects subgrid configs in the :subgrids key of args."
  [title rows table-id fields href args]
  (let [subgrid-configs (:subgrids args)
        base-grid (build-grid title rows table-id fields href args)
        has-subgrids? (seq subgrid-configs)]
    (if-not has-subgrids?
      base-grid
      ;; Add subgrid functionality to existing grid
      (let [enhanced-body (build-enhanced-grid-body rows href fields args subgrid-configs)]
        [:div
         ;; Main grid with enhanced body
         [:div.card.shadow.mb-4
          [:div.card-body.bg-gradient.bg-primary.text-white.rounded-top
           [:h4.mb-0.fw-bold title]]
          [:div.p-3.bg-white.rounded-bottom
           [:div.table-responsive
            [:table.table.table-hover.table-bordered.table-striped.table-sm.compact.align-middle.display.dataTable.w-100
             {:id table-id}
             (unified-table-head fields {:actions? true :new? (:new args) :href href})
             enhanced-body]]]]
         ;; Subgrid modal
         (build-subgrid-modal (get-in (first subgrid-configs) [:href]))
         ;; Subgrid JavaScript
         (subgrid-javascript)]))))



;; =============================================================================
;; Convenience functions for common subgrid scenarios
;; =============================================================================

(defn create-subgrid-config
  "Helper to create subgrid configuration"
  [options]
  (merge
   {:primary-key "id"
    :icon "bi bi-list-ul"
    :label nil}
   options))

(defn users-with-roles-subgrid
  "Example: Users grid with roles subgrid"
  [title rows]
  (let [table-id "users_table"
        labels ["lastname" "name" "username" "DOB" "Cell Phone" "Level" "status"]
        db-fields [:lastname :firstname :username :dob_formatted :cell :level_formatted :active_formatted]
        fields (apply array-map (interleave db-fields labels))
        args {:new true :edit true :delete true}
        href "/admin/users"
        ;; Define subgrid for user roles
        roles-subgrid (create-subgrid-config
                       {:title "User Roles"
                        :table-name "user_roles"
                        :foreign-key "user_id"
                        :href "/admin/users/roles"
                        :icon "bi bi-person-badge"
                        :label "Roles"})]
    (build-grid-with-subgrids title rows table-id fields href (assoc args :subgrids [roles-subgrid]))))

;; =============================================================================
;; Example usage and documentation
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
