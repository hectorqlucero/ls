(ns {{name}}.models.form
  (:require
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [{{name}}.models.crud :refer [config]]))

;; =============================================================================
;; Authentication Forms
;; =============================================================================

(defn password-form
  "Renders a professional password change form with Bootstrap 5 styling"
  [title]
  (list
   [:div.container-fluid.d-flex.justify-content-center.align-items-center
    [:div.card.shadow-lg {:style "width: 100%; max-width: 400px;"}
     [:div.card-header.bg-primary.text-white.text-center
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-5
      [:form {:method "POST"
              :action "/change/password"
              :class "needs-validation"
              :novalidate true}
       (anti-forgery-field)

       [:div.mb-3
        [:label.form-label.fw-semibold {:for "email"}
         [:i.fas.fa-envelope.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "email"
          :name "email"
          :type "email"
          :placeholder "Enter your email address..."
          :required true}]]

       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.fas.fa-lock.me-2] "New Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :type "password"
          :placeholder "Enter new password..."
          :required true}]]

       [:div.d-grid
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"}
         [:i.fas.fa-key.me-2] "Change Password"]]]]]]))

(defn login-form
  "Renders a professional login form with Bootstrap 5 styling"
  [title href]
  (list
   [:div.container-fluid.d-flex.justify-content-center.align-items-center
    [:div.card.shadow-lg {:style "width: 100%; max-width: 450px;"}
     [:div.card-header.bg-primary.text-white.text-center
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-5
      [:form {:method "POST"
              :action href
              :class "needs-validation"
              :novalidate true}
       (anti-forgery-field)

       [:div.mb-3
        [:label.form-label.fw-semibold {:for "username"}
         [:i.fas.fa-user.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "username"
          :name "username"
          :type "email"
          :required "true"
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Email is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your email address..."}]]

       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.fas.fa-lock.me-2] "Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :required "true"
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Password is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your password..."
          :type "password"}]]

       [:div.d-grid.gap-2
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"}
         [:i.fas.fa-sign-in-alt.me-2] "Sign In"]
        [:a.btn.btn-outline-info.btn-lg.fw-semibold
         {:role "button"
          :href "/change/password"}
         [:i.fas.fa-key.me-2] "Change Password"]]]]]]))

;; =============================================================================
;; Form Field Builders
;; =============================================================================

(defn build-hidden-field
  "Creates a hidden input field with specified attributes
   Args: {:id string :name string :value string}"
  [args]
  [:input {:type "hidden"
           :id (:id args)
           :name (:name args)
           :value (:value args)}])

(defn build-image-field
  "Renders an image upload field with preview functionality"
  [row]
  (list
   [:div.mb-3
    [:label.form-label.fw-semibold [:i.fas.fa-image.me-2] "Upload Image"]
    [:input.form-control.form-control-lg
     {:id "file"
      :name "file"
      :type "file"
      :accept "image/*"}]]

   [:div.text-center.mb-3
    [:div.image-preview-container.d-inline-block.position-relative
     [:img#image1.img-thumbnail.shadow-sm.rounded
      {:width "95"
       :height "71"
       :src (str (:path config) (:imagen row))
       :onError "this.src='/images/placeholder_profile.png'"
       :style "cursor: pointer; transition: all 0.3s ease;"}]
     [:div.position-absolute.top-0.end-0.translate-middle
      [:span.badge.bg-primary.rounded-pill
       [:i.fas.fa-search-plus]]]]]))

(defn build-image-field-script
  "JavaScript for image preview functionality with smooth animations"
  []
  [:script
   (str
    "
    $(document).ready(function() {
      $('img').click(function() {
        var img = $(this);
        if(img.width() < 500) {
          img.animate({width: '500', height: '500'}, 1000);
          img.addClass('shadow-lg');
        } else {
          img.animate({width: img.attr('width'), height: img.attr('height')}, 1000);
          img.removeClass('shadow-lg');
        }
      });
    });
    ")])

(defn build-dashboard-image
  "Renders a compact dashboard image with professional styling"
  [row]
  (list
   [:div.d-inline-block.me-2
    [:div.avatar-container.position-relative
     [:img#image1.rounded-circle.shadow-sm
      {:width "32"
       :height "32"
       :src (str (:path config) (:imagen row))
       :onError "this.src='/images/placeholder_profile.png'"
       :style "cursor: pointer; object-fit: cover; transition: all 0.3s ease;"}]
     [:div.position-absolute.bottom-0.end-0.translate-middle
      [:span.badge.bg-success.rounded-pill {:style "width: 8px; height: 8px; padding: 0;"}]]]]))

(defn build-dashboard-image-script
  "JavaScript for dashboard image interactions"
  []
  [:script
   (str
    "
    $(document).ready(function() {
      $('img').hover(
        function() { $(this).addClass('shadow'); },
        function() { $(this).removeClass('shadow'); }
      ).click(function() {
        var img = $(this);
        if(img.width() < 500) {
          img.animate({width: '500', height: '500'}, 1000);
          img.removeClass('rounded-circle').addClass('rounded shadow-lg');
        } else {
          img.animate({width: img.attr('width'), height: img.attr('height')}, 1000);
          img.addClass('rounded-circle').removeClass('rounded shadow-lg');
        }
      });
    });
    ")])

(defn build-field
  "Creates a professional form field with Bootstrap 5 styling
   Args: {:label string :type string :id string :name string :placeholder string :required bool :error string :value string}"
  [args]
  (let [my-class (str "form-control form-control-lg" (when (= (:required args) true) " mandatory"))
        args (assoc args :class my-class)]
    (list
     [:div.mb-3
      [:label.form-label.fw-semibold {:for (:name args)}
       (:label args)
       (when (= (:required args) true)
         [:span.text-danger.ms-1 "*"])]
      [:input (merge args
                     {:class my-class
                      :style "transition: all 0.3s ease;"})]])))

(defn build-textarea
  "Creates a professional textarea field with Bootstrap 5 styling
   Args: {:label string :id string :name string :placeholder string :required bool :error string :value string :rows string}"
  [args]
  (list
   [:div.mb-3
    [:label.form-label.fw-semibold {:for (:name args)}
     (:label args)
     (when (= (:required args) true)
       [:span.text-danger.ms-1 "*"])]
    [:textarea.form-control.form-control-lg
     {:id (:id args)
      :name (:name args)
      :rows (:rows args)
      :placeholder (:placeholder args)
      :required (:required args)
      :class (str "form-control form-control-lg" (when (= (:required args) true) " mandatory"))
      :oninvalid (str "this.setCustomValidity('" (:error args) "')")
      :oninput "this.setCustomValidity('')"
      :style "transition: all 0.3s ease; resize: vertical;"}
     (:value args)]]))

(defn build-select
  "Creates a professional select dropdown with Bootstrap 5 styling
   Args: {:label string :id string :name string :required bool :error string :options vector :value string}"
  [args]
  (let [options (:options args)]
    (list
     [:div.mb-3
      [:label.form-label.fw-semibold {:for (:name args)}
       (:label args)
       (when (= (:required args) true)
         [:span.text-danger.ms-1 "*"])]
      [:select.form-select.form-select-lg
       {:id (:id args)
        :name (:name args)
        :required (:required args)
        :class (str "form-select form-select-lg" (when (= (:required args) true) " mandatory"))
        :oninvalid (str "this.setCustomValidity('" (:error args) "')")
        :oninput "this.setCustomValidity('')"
        :style "transition: all 0.3s ease;"}
       (map (partial (fn [option]
                       (list
                        [:option {:value (:value option)
                                  :selected (if (= (:value args) (:value option)) true false)}
                         (:label option)]))) options)]])))

(defn build-radio
  "Creates a professional radio button group with Bootstrap 5 styling
   Args: {:label string :name string :options vector :value string}"
  [args]
  (let [options (:options args)]
    (list
     [:div.mb-3
      [:label.form-label.fw-semibold.d-block (:label args)]
      [:div.mt-2
       (map (partial (fn [option]
                       (list
                        [:div.form-check.form-check-inline.me-4
                         [:input.form-check-input
                          {:type "radio"
                           :id (:id option)
                           :name (:name args)
                           :value (:value option)
                           :checked (if (= (:value args) (:value option)) true false)
                           :style "transform: scale(1.2);"}]
                         [:label.form-check-label.fw-medium.ms-2
                          {:for (:id option)}
                          (:label option)]]))) options)]])))

;; =============================================================================
;; Button Builders
;; =============================================================================

(defn build-primary-input-button
  "Creates a primary styled input button
   Args: {:type string :value string}"
  [args]
  [:input.btn.btn-outline-success {:type (:type args)
                                   :style "padding:5px;margin:5px;"
                                   :value (:value args)}])

(defn build-secondary-input-button
  "Creates a secondary styled input button
   Args: {:type string :value string}"
  [args]
  [:input.btn.btn-outline-info {:type (:type args)
                                :style "padding:5px;margin:5px;"
                                :value (:value args)}])

(defn build-primary-anchor-button
  "Creates a primary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-outline-success {:type "button"
                               :href (:href (:href args))} (:label args)])

(defn build-secondary-anchor-button
  "Creates a secondary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-outline-info {:type "button"
                            :href (:href (:href args))} (:label args)])

(defn build-modal-buttons
  "Creates professional modal buttons with conditional rendering"
  [& args]
  (let [args (first args)
        view (:view args)]
    (list
     (when-not (= view true)
       [:input.btn.btn-outline-success {:type "submit"
                                        :style "padding:5px;margin:5px;"
                                        :value "Submit"}])
     [:button.btn.btn-outline-info {:type "button"
                                    :data-bs-dismiss "modal"} "Cancel"])))

;; =============================================================================
;; Main Form Builder
;; =============================================================================

(defn form
  "Creates a professional form container with Bootstrap 5 styling"
  [href fields buttons]
  (list
   [:div.container.border.bg-light
    [:form {:method "POST"
            :enctype "multipart/form-data"
            :action href}
     (anti-forgery-field)
     fields
     buttons]]))

;; =============================================================================
;; Usage Examples (Documentation)
;; =============================================================================

(comment
  "Professional form field examples with Bootstrap 5 styling"

  ;; Radio button example
  (build-radio {:label "Account Status"
                :name "active"
                :value "T"
                :options [{:id "activeT"
                           :label "Active"
                           :value "T"}
                          {:id "activeF"
                           :label "Inactive"
                           :value "F"}]})

  ;; Select dropdown example
  (build-select {:label "User Level"
                 :id "level"
                 :name "level"
                 :value "U"
                 :required true
                 :error "User level is required..."
                 :options [{:value ""
                            :label "Select user level..."}
                           {:value "U"
                            :label "User"}
                           {:value "A"
                            :label "Administrator"}
                           {:value "S"
                            :label "System"}]})

  ;; Hidden field example
  (build-hidden-field {:id "id"
                       :name "id"
                       :value "Database value"})

  ;; Textarea example
  (build-textarea {:label "Comments"
                   :id "comentarios"
                   :name "comentarios"
                   :rows "4"
                   :placeholder "Enter your comments here..."
                   :required true
                   :error "Comments are required!"
                   :value "Sample comment text"})

  ;; Text field example
  (build-field {:label "Full Name"
                :type "text"
                :id "nombre"
                :name "nombre"
                :placeholder "Enter full name..."
                :required true
                :value ""})

  ;; Email field example
  (build-field {:label "Email Address"
                :type "email"
                :id "correo_electronico"
                :name "correo_electronico"
                :placeholder "Enter email address..."
                :required true
                :value ""})

  ;; Search field example
  (build-field {:label "Search Contacts"
                :type "search"
                :id "buscar_contacto"
                :name "buscar_contacto"
                :placeholder "Search here..."
                :required false
                :value ""})

  ;; Phone field example
  (build-field {:label "Mobile Phone"
                :type "tel"
                :id "celular"
                :name "celular"
                :placeholder "Enter phone number..."
                :required false
                :value ""})

  ;; URL field example
  (build-field {:label "Website URL"
                :type "url"
                :id "url_pagina"
                :name "url_pagina"
                :placeholder "Enter website URL..."
                :required false
                :value ""})

  ;; Number field example
  (build-field {:label "Age"
                :type "number"
                :id "edad"
                :name "edad"
                :min "1"
                :max "120"
                :step "1"
                :placeholder "Enter age..."
                :required false
                :value ""})

  ;; Date field example
  (build-field {:label "Date of Birth"
                :type "date"
                :id "nacimiento"
                :name "nacimiento"
                :required false
                :value ""})

  ;; Date with constraints example
  (build-field {:label "Summer Availability"
                :type "date"
                :id "disponible_fecha"
                :name "disponible_fecha"
                :min "2024-06-01"
                :max "2024-08-31"
                :step "7"
                :value ""})

  ;; Month field example
  (build-field {:label "Month"
                :type "month"
                :id "mes"
                :name "mes"
                :required false
                :value ""})

  ;; Week field example
  (build-field {:label "Week"
                :type "week"
                :id "semana"
                :name "semana"
                :required false
                :value ""})

  ;; Color field example
  (build-field {:label "Choose Color"
                :type "color"
                :id "color"
                :name "color"
                :required false
                :value "#007bff"}))
