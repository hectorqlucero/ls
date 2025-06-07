(ns {{name}}.models.grid
  (:require
   [clojure.string :as st]))
;; start build-gid
(defn build-grid-head
  [href fields & args]
  (let [args (first args)
        new (:new args)]
    [:thead.table-light {:style "background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);"}
     [:tr
      (for [field fields]
        [:th {:data-sortable "true"
              :data-field (key field)
              :style "color: #2d3748;
                     font-weight: 600;
                     font-size: 0.95rem;
                     padding: 1rem 0.75rem;
                     border-bottom: 2px solid #e2e8f0;
                     text-transform: uppercase;
                     letter-spacing: 0.05em;
                     background: transparent;
                     border-top-left-radius: 0.5rem;
                     border-top-right-radius: 0.5rem;"}
         (st/upper-case (val field))])
      [:th.text-center {:style "white-space:nowrap;
                               width:128px;
                               color: #2d3748;
                               font-weight: 600;
                               font-size: 0.95rem;
                               padding: 1rem 0.75rem;
                               border-bottom: 2px solid #e2e8f0;
                               background: transparent;"}
       [:a.btn.btn-primary.btn-lg.fw-semibold.shadow-sm
        {:role "button"
         :class (str "btn btn-primary btn-lg fw-semibold shadow-sm" (when (= new false) " disabled"))
         :href (str href "/add")
         :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(13,110,253,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1); font-size: 1rem;"}
        [:i.bi.bi-plus-lg {:style "margin-right: 0.5rem; font-size: 1rem;"}]
        "New Record"]]]]))

(defn build-grid-body
  [rows href fields & args]
  (let [args (first args)
        edit (:edit args)
        delete (:delete args)]
    [:tbody
     (for [row rows]
       [:tr {:style "transition: all 0.3s ease;
                    border-bottom: 1px solid #f1f5f9;
                    background: transparent;"}
        (for [field fields]
          [:td.text-truncate {:style "max-width:150px;
                                     overflow:hidden;
                                     white-space:nowrap;
                                     padding: 0.875rem 0.75rem;
                                     color: #4a5568;
                                     font-size: 1rem;
                                     font-weight: 500;
                                     background: #f8fafc;
                                     border-radius: 8px;"}
           ((key field) row)])
        [:td.text-center {:style "white-space:nowrap;
                                 width:128px;
                                 padding: 0.875rem 0.75rem;
                                 background: transparent;"}
         [:div.d-flex.gap-2.justify-content-end
          [:a.btn.btn-warning.btn-lg.fw-semibold.shadow-sm
           {:role "button"
            :class (str "btn btn-warning btn-lg fw-semibold shadow-sm" (when (= edit false) " disabled"))
            :href (str href "/edit/" (:id row))
            :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 100px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(251,191,36,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1); font-size: 1rem;"}
           [:i.bi.bi-pencil {:style "margin-right: 0.25rem; font-size: 0.95rem;"}]
           "Edit"]
          [:a.btn.btn-danger.btn-lg.fw-semibold.shadow-sm
           {:role "button"
            :class (str "btn btn-danger btn-lg fw-semibold shadow-sm" (when (= delete false) " disabled"))
            :href (str href "/delete/" (:id row))
            :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 100px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(220,53,69,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1); font-size: 1rem;"}
           [:i.bi.bi-trash {:style "margin-right: 0.25rem; font-size: 0.95rem;"}]
           "Delete"]]]])]))

(defn build-grid
  [title rows table-id fields href & args]
  [:div.table-responsive
   {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            border: 1px solid #e2e8f0;
            overflow: hidden;
            margin: 1.5rem 0;"}
   [:div
    {:style "padding: 1.5rem 1.5rem 0.5rem 1.5rem;
             background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
             color: white;
             border-top-left-radius: 16px;
             border-top-right-radius: 16px;"}
    [:h3.text-center
     {:style "margin: 0;
              font-weight: 700;
              font-size: 1.5rem;
              text-shadow: 0 2px 4px rgba(0,0,0,0.1);
              letter-spacing: 0.025em;"}
     title]]
   [:table.table.table-sm.w-100
    {:id table-id
     :data-locale "en-US"
     :data-show-fullscreen "true"
     :data-toggle "table"
     :data-show-columns "true"
     :data-show-toggle "true"
     :data-show-print "false"
     :data-search "true"
     :data-pagination "true"
     :data-key-events "true"
     :style "margin: 0; border: none; background: transparent;"}
    (if (seq args)
      (build-grid-head href fields (first args))
      (build-grid-head href fields))
    (if (seq args)
      (build-grid-body rows href fields (first args))
      (build-grid-body rows href fields))]])

;; start build-dashboard
(defn build-dashboard-head
  [title fields]
  [:thead.table-light {:style "background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);"}
   [:tr
    [:th.text-center {:colspan (count fields)
                      :style "color: #2d3748;
                             font-weight: 700;
                             font-size: 1.1rem;
                             padding: 1.25rem;
                             border-bottom: 2px solid #667eea;
                             background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                             color: white;
                             text-shadow: 0 1px 3px rgba(0,0,0,0.2);
                             letter-spacing: 0.025em;
                             border-top-left-radius: 16px;
                             border-top-right-radius: 16px;"}
     (st/upper-case title)]]
   [:tr
    (for [field fields]
      [:th {:data-sortable "true"
            :data-field (key field)
            :style "white-space:nowrap;
                   color: #2d3748;
                   font-weight: 600;
                   font-size: 0.95rem;
                   padding: 0.875rem 0.75rem;
                   border-bottom: 1px solid #e2e8f0;
                   text-transform: uppercase;
                   letter-spacing: 0.05em;
                   background: transparent;"}
       (st/upper-case (val field))])]])

(defn build-dashboard-body
  [rows fields]
  [:tbody
   (for [row rows]
     [:tr {:style "transition: all 0.3s ease;
                  border-bottom: 1px solid #f1f5f9;
                  background: transparent;"}
      (for [field fields]
        [:td {:style "white-space:nowrap;
                     padding: 0.875rem 0.75rem;
                     color: #4a5568;
                     font-size: 1rem;
                     font-weight: 500;
                     background: #f8fafc;
                     border-radius: 8px;"}
         ((key field) row)])])])

(defn build-dashboard
  [title rows table-id fields]
  [:div.table-responsive
   {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            border: 1px solid #e2e8f0;
            overflow: hidden;
            margin: 1.5rem 0;"}
   [:table.table.table-sm
    {:style "table-layout:auto;
             width:100%;
             margin: 0;
             border: none;
             background: transparent;"
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

;; Start build-modal
(defn build-modal
  [title _ form]
  (list
   [:div.modal.fade {:id "exampleModal"
                     :data-bs-backdrop "static"
                     :data-bs-keyboard "false"
                     :tabindex "-1"
                     :aria-labelledby "exampleModalLabel"
                     :aria-hidden "true"
                     :style "backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px);"}
    [:div.modal-dialog.modal-dialog-centered {:style "max-width: 700px; width: 100%;"}
     [:div.modal-content {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                                 border: 1px solid #e2e8f0;
                                 border-radius: 16px;
                                 box-shadow: 0 20px 40px rgba(0,0,0,0.15);
                                 overflow: hidden;
                                 width: 100%;"}
      [:div.modal-header {:style "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                 border-bottom: none;
                                 padding: 1.5rem;
                                 color: white;
                                 border-top-left-radius: 16px;
                                 border-top-right-radius: 16px;"}
       [:h1.modal-title.fs-5 {:id "exampleModalLabel"
                              :style "margin: 0;
                                     font-weight: 700;
                                     font-size: 1.25rem;
                                     text-shadow: 0 1px 3px rgba(0,0,0,0.2);
                                     letter-spacing: 0.025em;"}
        title]
       [:button.btn-close
        {:type "button"
         :data-bs-dismiss "modal"
         :aria-label "Close"
         :style "background: none; border-radius: 8px; opacity: 1;"}
        [:span {:aria-hidden "true"
                :style "font-size: 2rem; color: #fff; font-weight: 700; line-height: 1;"} "×"]]]
      [:div.modal-body.p-0 {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%); width: 100%; padding: 2rem;"}
       ;; Patch the button row to be horizontal in the modal
       (if (and (vector? form) (= :form (first form)))
         (let [[tag attrs & body] form
               new-body (map (fn [el]
                               (if (and (vector? el)
                                        (= :div (first el))
                                        (some #(and (vector? %) (re-find #"btn" (str (second %)))) (rest el)))
                                 ;; Force button row to flex horizontal
                                 (assoc el 1 (merge (second el)
                                                    {:class "d-flex gap-2 justify-content-end w-100 mt-4"}))
                                 el))
                             body)]
           (into [tag (merge {:style "width:100%;" :class "w-100"} attrs)] new-body))
         [:div {:style "width:100%;"} form])]]]]))
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
