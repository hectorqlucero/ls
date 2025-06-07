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
    {:style "min-height: 60vh;"}
    [:div.card.shadow-lg
     {:style "width: 100%; max-width: 400px; border-radius: 16px; border: 1px solid #e2e8f0; background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);"}
     [:div.card-header.bg-primary.text-white.text-center
      {:style "border-top-left-radius: 16px; border-top-right-radius: 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"}
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-4
      [:form {:method "POST"
              :action "/change/password"
              :class "needs-validation"
              :novalidate false}
       (anti-forgery-field)
       [:div.mb-3
        [:label.form-label.fw-semibold {:for "email"}
         [:i.fas.fa-envelope.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "email"
          :name "email"
          :type "email"
          :placeholder "Enter your email address..."
          :required true
          :autocomplete "username"
          :style "background: #f8fafc; border-radius: 8px;"}]]
       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.fas.fa-lock.me-2] "New Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :type "password"
          :placeholder "Enter new password..."
          :required true
          :autocomplete "new-password"
          :style "background: #f8fafc; border-radius: 8px;"}]]
       [:div.d-flex.gap-2.justify-content-end.mt-4
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"
          :style "border-radius: 8px;"}
         [:i.fas.fa-key.me-2] "Change Password"]]]]]]))

(defn login-form
  "Renders a professional login form with Bootstrap 5 styling"
  [title href]
  (list
   [:div.container-fluid.d-flex.justify-content-center.align-items-center
    {:style "min-height: 60vh;"}
    [:div.card.shadow-lg
     {:style "width: 100%; max-width: 450px; border-radius: 16px; border: 1px solid #e2e8f0; background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);"}
     [:div.card-header.bg-primary.text-white.text-center
      {:style "border-top-left-radius: 16px; border-top-right-radius: 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"}
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-4
      [:form {:method "POST"
              :action href
              :class "needs-validation"
              :novalidate false}
       (anti-forgery-field)
       [:div.mb-3
        [:label.form-label.fw-semibold {:for "username"}
         [:i.fas.fa-user.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "username"
          :name "username"
          :type "email"
          :required true
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Email is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your email address..."
          :autocomplete "username"
          :style "background: #f8fafc; border-radius: 8px;"}]]
       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.fas.fa-lock.me-2] "Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :required true
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Password is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your password..."
          :type "password"
          :autocomplete "current-password"
          :style "background: #f8fafc; border-radius: 8px;"}]]
       [:div.d-flex.gap-2.justify-content-end.mt-4
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"
          :style "border-radius: 8px;"}
         [:i.fas.fa-sign-in-alt.me-2] "Sign In"]
        [:a.btn.btn-outline-info.btn-lg.fw-semibold
         {:role "button"
          :href "/change/password"
          :style "border-radius: 8px;"}
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
      :accept "image/*"
      :style "background: #f8fafc; border-radius: 8px;"}]]
   [:div.text-center.mb-3
    [:div.image-preview-container.d-inline-block.position-relative
     [:img#image1.img-thumbnail.shadow-sm.rounded
      {:width "95"
       :height "71"
       :src (str (:path config) (:imagen row))
       :onError "this.src='/images/placeholder_profile.png'"
       :style "cursor: pointer; transition: all 0.3s ease; object-fit: cover; background: #f8fafc; border-radius: 8px;"}]
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
       :style "cursor: pointer; object-fit: cover; transition: all 0.3s ease; background: #f8fafc; border-radius: 8px;"}]
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
                      :style "transition: all 0.3s ease; background: #f8fafc; border-radius: 8px;"})]])))

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
      :style "transition: all 0.3s ease; resize: vertical; background: #f8fafc; border-radius: 8px;"}
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
        :style "transition: all 0.3s ease; background: #f8fafc; border-radius: 8px;"}
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
                           :style "transform: scale(1.2); background: #f8fafc; border-radius: 8px;"}]
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
  [:input.btn.btn-primary.btn-lg.fw-semibold.shadow-sm
   {:type (:type args)
    :value (or (:value args) "Submit")
    :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(13,110,253,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}])

(defn build-secondary-input-button
  "Creates a secondary styled input button
   Args: {:type string :value string}"
  [args]
  [:input.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm
   {:type (:type args)
    :value (or (:value args) "Cancel")
    :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(108,117,125,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}])

(defn build-primary-anchor-button
  "Creates a primary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-primary.btn-lg.fw-semibold.shadow-sm
   {:type "button"
    :href (:href args)
    :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(13,110,253,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}
   (:label args)])

(defn build-secondary-anchor-button
  "Creates a secondary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm
   {:type "button"
    :href (:href args)
    :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(108,117,125,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}
   (:label args)])

(defn build-modal-buttons
  "Creates professional modal buttons with conditional rendering"
  [& args]
  (let [args (first args)
        view (:view args)]
    (list
     (when-not (= view true)
       [:button.btn.btn-primary.btn-lg.fw-semibold.shadow-sm
        {:type "submit"
         :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(13,110,253,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}
        "Submit"])
     [:button.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm
      {:type "button"
       :data-bs-dismiss "modal"
       :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(108,117,125,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}
      "Cancel"])))

(defn build-form-buttons
  "Creates professional form buttons with Bootstrap 5 styling and HTML5 validation support.
   Args: {:cancel-url string, :view bool (optional)}"
  [& args]
  (let [args (first args)
        view (:view args)
        cancel-url (:cancel-url args)]
    (list
     (when-not (= view true)
       [:button.btn.btn-primary.btn-lg.fw-semibold.shadow-sm
        {:type "submit"
         :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(13,110,253,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"
         :onclick "if(this.form && !this.form.checkValidity()){this.form.reportValidity();return false;}"} ; HTML5 validation
        "Submit"])
     [:a.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm
      {:type "button"
       :href cancel-url
       :style "border-radius: 8px; padding: 0.5rem 1.5rem; min-width: 120px; letter-spacing: 0.03em; box-shadow: 0 2px 8px rgba(108,117,125,0.10); transition: all 0.2s cubic-bezier(0.4,0,0.2,1);"}
      "Cancel"])))

;; =============================================================================
;; Main Form Builder
;; =============================================================================

(defn form
  "Creates a professional form container with Bootstrap 5 styling and themed colors.
   If title is passed, it is rendered in the header. Handles HTML5 validation."
  ([href fields buttons]
   (form href fields buttons nil))
  ([href fields buttons title]
   (list
    [:div.container.d-flex.justify-content-center.align-items-center
     {:style "min-height: 45vh;"}
     [:div.card.shadow-lg
      {:style "width: 100%; max-width: 700px; background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                border: 1px solid #e2e8f0; border-radius: 16px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); overflow: hidden;"}
      (when title
        [:div.card-header
         {:style "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                   border-bottom: none; padding: 1.5rem; color: white; border-top-left-radius: 16px; border-top-right-radius: 16px;"}
         [:h4.mb-0.fw-bold.text-center
          {:style "margin: 0; font-weight: 700; font-size: 1.25rem; text-shadow: 0 1px 3px rgba(0,0,0,0.2); letter-spacing: 0.025em; word-break: break-word;"}
          title]])
      [:div.card-body.p-4
       [:form {:method "POST"
               :enctype "multipart/form-data"
               :action href
               :class "needs-validation"
               :novalidate false}
        (anti-forgery-field)
        fields
        [:div.d-flex.gap-2.justify-content-end.mt-4
         (cond
           (and (sequential? buttons) (not (vector? (first buttons))))
           (doall buttons)
           (sequential? buttons)
           (doall buttons)
           :else
           buttons)]]]]])))

;; =============================================================================
;; Usage Examples (Documentation)
;; =============================================================================

(comment
  ;; Example usage for each builder function

  ;; Hidden field
  (build-hidden-field {:id "id" :name "id" :value "123"})

  ;; Image field
  (build-image-field {:imagen "profile.jpg" :path "/uploads/"})

  ;; Text field
  (build-field {:label "Full Name"
                :type "text"
                :id "nombre"
                :name "nombre"
                :placeholder "Enter full name..."
                :required true
                :value ""})

  ;; Textarea
  (build-textarea {:label "Comments"
                   :id "comentarios"
                   :name "comentarios"
                   :rows "4"
                   :placeholder "Enter your comments here..."
                   :required true
                   :error "Comments are required!"
                   :value "Sample comment text"})

  ;; Select
  (build-select {:label "User Level"
                 :id "level"
                 :name "level"
                 :value "U"
                 :required true
                 :error "User level is required..."
                 :options [{:value "" :label "Select user level..."}
                           {:value "U" :label "User"}
                           {:value "A" :label "Administrator"}
                           {:value "S" :label "System"}]})

  ;; Radio
  (build-radio {:label "Account Status"
                :name "active"
                :value "T"
                :options [{:id "activeT" :label "Active" :value "T"}
                          {:id "activeF" :label "Inactive" :value "F"}]})

  ;; Primary input button
  (build-primary-input-button {:type "submit" :value "Save"})

  ;; Secondary input button
  (build-secondary-input-button {:type "button" :value "Cancel"})

  ;; Primary anchor button
  (build-primary-anchor-button {:label "Go" :href "/go"})

  ;; Secondary anchor button
  (build-secondary-anchor-button {:label "Back" :href "/back"})

  ;; Modal buttons
  (build-modal-buttons {:view false})

  ;; Main form
  (form "/submit"
        [(build-field {:label "Name" :type "text" :id "name" :name "name" :placeholder "Name" :required true :value ""})]
        [(build-primary-input-button {:type "submit" :value "Submit"})
         (build-secondary-input-button {:type "button" :value "Cancel"})]
        "Example Form"))
