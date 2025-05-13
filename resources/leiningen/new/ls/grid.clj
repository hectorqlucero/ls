(ns {{name}}.models.grid
  (:require
   [clojure.string :as st]))

;; start build-gid
(defn build-grid-head
  [href fields & args]
  (let [args (first args)
        new (:new args)]
    [:thead.table-light
     (for [field fields]
       [:th {:data-sortable "true"
             :data-field (key field)} (st/upper-case (val field))])
     [:th.text-center {:style "white-space:nowrap;width:128px;"} [:a.btn.btn-outline-success {:role "button"
                                                                                              :class (str "btn btn-sm btn-outline-success" (when (= new false) " disabled"))
                                                                                              :href (str href "/add")} [:i.bi.bi-plus-lg] "New Record"]]]))

(defn build-grid-body
  [rows href fields & args]
  (let [args (first args)
        edit (:edit args)
        delete (:delete args)]
    [:tbody
     (for [row rows]
       [:tr
        (for [field fields]
          [:td.text-truncate {:style "max-width:150px;overflow:hidden;white-space:nowrap"} ((key field) row)])

        [:td.text-center {:style "white-space:nowrap;width:128px;"}
         [:div.d-inline-flex.gap-1
          [:a {:role "button"
               :class (str "btn btn-sm btn-outline-warning" (when (= edit false) " disabled"))
               :style "margin:1px;"
               :href (str href "/edit/" (:id row))} [:i.bi.bi-pencil] "Edit"]
          [:a {:role "button"
               :class (str "confirm btn btn-sm btn-outline-danger" (when (= delete false) " disabled"))
               :style "margin:1px;"
               :href (str href "/delete/" (:id row))} [:i.bi.bi-trash] "Delete"]]]])]))

(defn build-grid
  [title rows table-id fields href & args]
  [:div.table-responsive
   [:h3.text-center.text-info title]
   [:table.table.table-sm.w-100 {:id table-id
                                 :data-locale "en-US"
                                 :data-show-fullscreen "true"
                                 :data-toggle "table"
                                 :data-show-columns "true"
                                 :data-show-toggle "true"
                                 :data-show-print "false"
                                 :data-search "true"
                                 :data-pagination "true"
                                 :data-key-events "true"}
    (if (seq args)
      (build-grid-head href fields (first args))
      (build-grid-head href fields))
    (if (seq args)
      (build-grid-body rows href fields (first args))
      (build-grid-body rows href fields))]])
;; End build-grid

;; start build-dashboard
(defn build-dashboard-head
  [title fields]
  [:thead.table-light
   [:tr
    [:th.text-muted.text-center {:colspan (count fields)} (st/upper-case title)]]
   [:tr
    [:div.d-inline-flex.gap-1
     (for [field fields]
       [:th {:data-sortable "true"
             :data-field (key field)
             :style "white-space:nowrap;"} (st/upper-case (val field))])]]])

(defn build-dashboard-body
  [rows fields]
  [:tbody
   (for [row rows]
     [:tr
      [:div.d-inline-flex.gap-1
       (for [field fields]
         [:td {:style "white-space:nowrap"} ((key field) row)])]])])

(defn build-dashboard
  [title rows table-id fields]
  [:div.table-responsive
   [:table.table.table-sm {:style "table-layout:auto;width:100%;"
                           :id table-id
                           :data-virtual-scroll "true"
                           :data-show-export "true"
                           :data-show-fullscreen "true"
                           :data-locale "es-MX"
                           :data-toggle "table"
                           :data-show-columns "true"
                           :data-show-toggle "true"
                           :data-show-print "true"
                           :data-search "true"
                           :data-pagination "true"
                           :data-key-events "true"}
    (build-dashboard-head title fields)
    (build-dashboard-body rows fields)]])
;; End build-dashboard

;; Start build-modal
(defn build-modal
  [title _ form]
  (list
   [:div.modal.fade {:id "exampleModal"
                     :data-bs-backdrop "static"
                     :data-bs-keyboard "false"
                     :tabindex "-1"
                     :aria-labelledby "exampleModalLabel"
                     :aria-hidden "true"}
    [:div.modal-dialog
     [:div.modal-content
      [:div.modal-header
       [:h1.modal-title.fs-5 {:id "exampleModalLabel"} title]
       [:button.btn-close {:type "button"
                           :data-bs-dismiss "modal"
                           :aria-label "Close"}]]

      [:div.modal-body
       [:span form]]]]]))
;; End build-modal

(defn modal-script
  []
  [:script
   "
   const myModal = new bootstrap.Modal(document.getElementById('exampleModal'), {
    keyboard: false
   })

   myModal.show();
   "])
;; End build-modal
