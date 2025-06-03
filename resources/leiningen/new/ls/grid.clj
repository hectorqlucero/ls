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
                     font-size: 0.875rem; 
                     padding: 1rem 0.75rem;
                     border-bottom: 2px solid #e2e8f0;
                     text-transform: uppercase;
                     letter-spacing: 0.05em;"}
         (st/upper-case (val field))])
      [:th.text-center {:style "white-space:nowrap;
                               width:128px;
                               color: #2d3748; 
                               font-weight: 600; 
                               font-size: 0.875rem; 
                               padding: 1rem 0.75rem;
                               border-bottom: 2px solid #e2e8f0;"}
       [:a.btn.btn-sm {:role "button"
                       :class (str "btn btn-sm" (when (= new false) " disabled"))
                       :href (str href "/add")
                       :style "transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                              background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                              border: 1px solid #10b981;
                              color: white;
                              font-weight: 500;
                              border-radius: 8px;
                              padding: 0.5rem 1rem;
                              text-decoration: none;
                              display: inline-flex;
                              align-items: center;
                              font-size: 0.875rem;
                              box-shadow: 0 2px 4px rgba(16,185,129,0.2);"
                       :onmouseover "if (!this.classList.contains('disabled')) {
                                       this.style.background='linear-gradient(135deg, #059669 0%, #047857 100%)';
                                       this.style.borderColor='#059669';
                                       this.style.transform='translateY(-2px)';
                                       this.style.boxShadow='0 6px 16px rgba(16,185,129,0.4)';
                                     }"
                       :onmouseout "if (!this.classList.contains('disabled')) {
                                      this.style.background='linear-gradient(135deg, #10b981 0%, #059669 100%)';
                                      this.style.borderColor='#10b981';
                                      this.style.transform='none';
                                      this.style.boxShadow='0 2px 4px rgba(16,185,129,0.2)';
                                    }"}
        [:i.bi.bi-plus-lg {:style "margin-right: 0.5rem; font-size: 0.875rem;"}]
        "New Record"]]]]))

(defn build-grid-body
  [rows href fields & args]
  (let [args (first args)
        edit (:edit args)
        delete (:delete args)]
    [:tbody
     (for [row rows]
       [:tr {:style "transition: all 0.3s ease;
                    border-bottom: 1px solid #f1f5f9;"
             :onmouseover "this.style.background='linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)';
                          this.style.transform='scale(1.005)';
                          this.style.boxShadow='0 4px 12px rgba(0,0,0,0.08)';"
             :onmouseout "this.style.background='transparent';
                         this.style.transform='none';
                         this.style.boxShadow='none';"}
        (for [field fields]
          [:td.text-truncate {:style "max-width:150px;
                                     overflow:hidden;
                                     white-space:nowrap;
                                     padding: 0.875rem 0.75rem;
                                     color: #4a5568;
                                     font-size: 0.9rem;
                                     font-weight: 500;"}
           ((key field) row)])

        [:td.text-center {:style "white-space:nowrap;
                                 width:128px;
                                 padding: 0.875rem 0.75rem;"}
         [:div.d-inline-flex.gap-1 {:style "gap: 0.5rem;"}
          [:a {:role "button"
               :class (str "btn btn-sm" (when (= edit false) " disabled"))
               :style "margin:1px;
                      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
                      border: 1px solid #f59e0b;
                      color: white;
                      font-weight: 500;
                      border-radius: 6px;
                      padding: 0.375rem 0.75rem;
                      text-decoration: none;
                      display: inline-flex;
                      align-items: center;
                      font-size: 0.8rem;
                      min-width: 60px;
                      justify-content: center;"
               :href (str href "/edit/" (:id row))
               :onmouseover "if (!this.classList.contains('disabled')) {
                               this.style.background='linear-gradient(135deg, #d97706 0%, #b45309 100%)';
                               this.style.borderColor='#d97706';
                               this.style.transform='translateY(-1px)';
                               this.style.boxShadow='0 4px 12px rgba(245,158,11,0.4)';
                             }"
               :onmouseout "if (!this.classList.contains('disabled')) {
                              this.style.background='linear-gradient(135deg, #f59e0b 0%, #d97706 100%)';
                              this.style.borderColor='#f59e0b';
                              this.style.transform='none';
                              this.style.boxShadow='none';
                            }"}
           [:i.bi.bi-pencil {:style "margin-right: 0.25rem; font-size: 0.75rem;"}]
           "Edit"]
          [:a {:role "button"
               :class (str "confirm btn btn-sm" (when (= delete false) " disabled"))
               :style "margin:1px;
                      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                      border: 1px solid #ef4444;
                      color: white;
                      font-weight: 500;
                      border-radius: 6px;
                      padding: 0.375rem 0.75rem;
                      text-decoration: none;
                      display: inline-flex;
                      align-items: center;
                      font-size: 0.8rem;
                      min-width: 70px;
                      justify-content: center;"
               :href (str href "/delete/" (:id row))
               :onmouseover "if (!this.classList.contains('disabled')) {
                               this.style.background='linear-gradient(135deg, #dc2626 0%, #b91c1c 100%)';
                               this.style.borderColor='#dc2626';
                               this.style.transform='translateY(-1px)';
                               this.style.boxShadow='0 4px 12px rgba(239,68,68,0.4)';
                             }"
               :onmouseout "if (!this.classList.contains('disabled')) {
                              this.style.background='linear-gradient(135deg, #ef4444 0%, #dc2626 100%)';
                              this.style.borderColor='#ef4444';
                              this.style.transform='none';
                              this.style.boxShadow='none';
                            }"}
           [:i.bi.bi-trash {:style "margin-right: 0.25rem; font-size: 0.75rem;"}]
           "Delete"]]]])]))

(defn build-grid
  [title rows table-id fields href & args]
  [:div.table-responsive {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                                 border-radius: 12px;
                                 box-shadow: 0 10px 25px rgba(0,0,0,0.08);
                                 border: 1px solid #e2e8f0;
                                 overflow: hidden;
                                 margin: 1rem 0;"}
   [:div {:style "padding: 1.5rem 1.5rem 0.5rem 1.5rem;
                 background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                 color: white;"}
    [:h3.text-center {:style "margin: 0;
                             font-weight: 700;
                             font-size: 1.5rem;
                             text-shadow: 0 2px 4px rgba(0,0,0,0.1);
                             letter-spacing: 0.025em;"}
     title]]
   [:table.table.table-sm.w-100 {:id table-id
                                 :data-locale "en-US"
                                 :data-show-fullscreen "true"
                                 :data-toggle "table"
                                 :data-show-columns "true"
                                 :data-show-toggle "true"
                                 :data-show-print "false"
                                 :data-search "true"
                                 :data-pagination "true"
                                 :data-key-events "true"
                                 :style "margin: 0;
                                        border: none;
                                        background: transparent;"}
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
                             letter-spacing: 0.025em;"}
     (st/upper-case title)]]
   [:tr
    (for [field fields]
      [:th {:data-sortable "true"
            :data-field (key field)
            :style "white-space:nowrap;
                   color: #2d3748;
                   font-weight: 600;
                   font-size: 0.875rem;
                   padding: 0.875rem 0.75rem;
                   border-bottom: 1px solid #e2e8f0;
                   text-transform: uppercase;
                   letter-spacing: 0.05em;"}
       (st/upper-case (val field))])]])

(defn build-dashboard-body
  [rows fields]
  [:tbody
   (for [row rows]
     [:tr {:style "transition: all 0.3s ease;
                  border-bottom: 1px solid #f1f5f9;"
           :onmouseover "this.style.background='linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)';
                        this.style.boxShadow='0 2px 8px rgba(0,0,0,0.06)';"
           :onmouseout "this.style.background='transparent';
                       this.style.boxShadow='none';"}
      (for [field fields]
        [:td {:style "white-space:nowrap;
                     padding: 0.875rem 0.75rem;
                     color: #4a5568;
                     font-size: 0.9rem;
                     font-weight: 500;"}
         ((key field) row)])])])

(defn build-dashboard
  [title rows table-id fields]
  [:div.table-responsive {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                                 border-radius: 12px;
                                 box-shadow: 0 10px 25px rgba(0,0,0,0.08);
                                 border: 1px solid #e2e8f0;
                                 overflow: hidden;
                                 margin: 1rem 0;"}
   [:table.table.table-sm {:style "table-layout:auto;
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
                     :aria-hidden "true"
                     :style "backdrop-filter: blur(10px);
                            -webkit-backdrop-filter: blur(10px);"}
    [:div.modal-dialog {:style "margin: 2rem auto;"}
     [:div.modal-content {:style "background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                                 border: 1px solid #e2e8f0;
                                 border-radius: 16px;
                                 box-shadow: 0 20px 40px rgba(0,0,0,0.15);
                                 overflow: hidden;"}
      [:div.modal-header {:style "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                 border-bottom: none;
                                 padding: 1.5rem;
                                 color: white;"}
       [:h1.modal-title.fs-5 {:id "exampleModalLabel"
                              :style "margin: 0;
                                     font-weight: 700;
                                     font-size: 1.25rem;
                                     text-shadow: 0 1px 3px rgba(0,0,0,0.2);
                                     letter-spacing: 0.025em;"}
        title]
       [:button.btn-close {:type "button"
                           :data-bs-dismiss "modal"
                           :aria-label "Close"
                           :style "background: rgba(255,255,255,0.2);
                                  border-radius: 8px;
                                  opacity: 0.8;
                                  transition: all 0.3s ease;
                                  filter: invert(1);"
                           :onmouseover "this.style.background='rgba(255,255,255,0.3)';
                                        this.style.opacity='1';
                                        this.style.transform='scale(1.1)';"
                           :onmouseout "this.style.background='rgba(255,255,255,0.2)';
                                       this.style.opacity='0.8';
                                       this.style.transform='scale(1)';"}]]

      [:div.modal-body {:style "padding: 2rem;
                               background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);"}
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
