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
   [:div.container.d-flex.justify-content-center.align-items-center
    {:style "min-height: 80vh;"}
    [:div.card.shadow-lg.w-100
     {:style "max-width: 420px;"}
     [:div.card-header.bg-primary.text-white.text-center
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-4
      [:form {:method "POST"
              :action "/change/password"
              :class "needs-validation"
              :novalidate true}
       (anti-forgery-field)
       [:div.mb-3
        [:label.form-label.fw-semibold {:for "email"}
         [:i.bi.bi-envelope.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "email"
          :name "email"
          :type "email"
          :placeholder "Enter your email address..."
          :required true
          :autocomplete "username"}]]
       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.bi.bi-lock.me-2] "New Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :type "password"
          :placeholder "Enter new password..."
          :required true
          :autocomplete "new-password"}]]
       [:div.d-flex.gap-2.justify-content-end.mt-4
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"}
         [:i.bi.bi-key.me-2] "Change Password"]]]]]]))

(defn login-form
  "Renders a professional login form with Bootstrap 5 styling"
  [title href]
  (list
   [:div.container.d-flex.justify-content-center.align-items-center
    {:style "min-height: 80vh;"}
    [:div.card.shadow-lg.w-100
     {:style "max-width: 420px;"}
     [:div.card-header.bg-primary.text-white.text-center
      [:h4.mb-0.fw-bold title]]
     [:div.card-body.p-4
      [:form {:method "POST"
              :action href
              :class "needs-validation"
              :novalidate true}
       (anti-forgery-field)
       [:div.mb-3
        [:label.form-label.fw-semibold {:for "username"}
         [:i.bi.bi-person.me-2] "Email Address"]
        [:input.form-control.form-control-lg
         {:id "username"
          :name "username"
          :type "email"
          :required true
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Email is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your email address..."
          :autocomplete "username"}]]
       [:div.mb-4
        [:label.form-label.fw-semibold {:for "password"}
         [:i.bi.bi-lock.me-2] "Password"]
        [:input.form-control.form-control-lg
         {:id "password"
          :name "password"
          :required true
          :class "mandatory"
          :oninvalid "this.setCustomValidity('Password is required...')"
          :oninput "this.setCustomValidity('')"
          :placeholder "Enter your password..."
          :type "password"
          :autocomplete "current-password"}]]
       [:div.d-flex.gap-2.justify-content-end.mt-4
        [:button.btn.btn-success.btn-lg.fw-semibold
         {:type "submit"}
         [:i.bi.bi-box-arrow-in-right.me-2] "Sign In"]
        [:a.btn.btn-outline-info.btn-lg.fw-semibold
         {:role "button"
          :href "/change/password"}
         [:i.bi.bi-key.me-2] "Change Password"]]]]]]))

;; =============================================================================
;; Form Field Builders
;; =============================================================================

(defn build-image-field
  "Renders an image upload field with preview functionality"
  [row]
  (list
   [:div.mb-3
    [:label.form-label.fw-semibold [:i.bi.bi-image.me-2] "Upload Image"]
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
       :onError "this.src='/images/placeholder_profile.png'"}]
     [:div.position-absolute.top-0.end-0.translate-middle
      [:span.badge.bg-primary.rounded-pill
       [:i.bi.bi-search-plus]]]]]))

(defn build-image-field-script
  "JavaScript for image preview functionality with smooth animations"
  []
  [:script
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
    "])

(defn build-field
  "Creates a professional form field with Bootstrap 5 styling and correct HTML5/Bootstrap5 field type rendering.
   Args: {:label string :type string :id string :name string :placeholder string :required bool :error string :value string :options vector (for select/radio) :step :min :max :multiple :readonly :disabled :pattern string :autocomplete string :accept string :autofocus bool :checked bool :size int :maxlength int :minlength int :list string :inputmode string :spellcheck bool :form string :dirname string :tabindex int :aria-label string :aria-describedby string ...}"
  [args]
  (let [type (:type args)
        my-class (str "form-control form-control-lg" (when (= (:required args) true) " mandatory"))
        base-style "transition: all 0.3s ease; border-radius: 8px; background-color: #f8fafc;"
        label-el [:label.form-label.fw-semibold {:for (:name args)}
                  (:label args)
                  (when (= (:required args) true)
                    [:span.text-danger.ms-1 "*"])]]
    (cond
      ;; Support for hidden fields
      (= type "hidden")
      [:input (merge {:type "hidden"
                      :id (:id args)
                      :name (:name args)
                      :value (:value args)}
                     (select-keys args [:autocomplete :form :dirname :tabindex :aria-label :aria-describedby]))]

      (= type "select")
      [:div.mb-3
       label-el
       [:select.form-select.form-select-lg
        (merge
         {:id (:id args)
          :name (:name args)
          :required (:required args)
          :class (str "form-select form-select-lg" (when (= (:required args) true) " mandatory"))
          :oninvalid (str "this.setCustomValidity('" (:error args) "')")
          :oninput "this.setCustomValidity('')"
          :multiple (:multiple args)
          :disabled (:disabled args)
          :readonly (:readonly args)
          :autofocus (:autofocus args)
          :size (:size args)
          :tabindex (:tabindex args)
          :aria-label (:aria-label args)
          :aria-describedby (:aria-describedby args)}
         (when (:value args) {:defaultValue (:value args)}))
        (for [option (:options args)]
          [:option (merge
                    {:value (:value option)
                     :selected (if (= (:value args) (:value option)) true false)}
                    (select-keys option [:disabled :label]))
           (:label option)])]]

      (or (= type "radio") (= type "checkbox"))
      [:div.mb-3
       [:label.form-label.fw-semibold.d-block (:label args)]
       [:div.mt-2
        (for [option (:options args)]
          [:div.form-check.form-check-inline.me-4
           [:input.form-check-input
            (merge
             {:type type
              :id (:id option)
              :name (:name args)
              :value (:value option)
              :checked (when (= (:value args) (:value option)) true)
              :required (:required args)
              :disabled (:disabled args)
              :readonly (:readonly args)
              :style "transform: scale(1.2); background-color: #f8fafc; border-radius: 8px;"
              :autofocus (:autofocus args)
              :tabindex (:tabindex args)
              :aria-label (:aria-label args)
              :aria-describedby (:aria-describedby args)
              :form (:form args)}
             (select-keys option [:pattern :min :max :step :autocomplete :spellcheck :form :dirname :list :inputmode :size :maxlength :minlength]))]
           [:label.form-check-label.fw-medium.ms-2
            {:for (:id option)}
            (:label option)]])]]

      (= type "textarea")
      [:div.mb-3
       label-el
       [:textarea
        (merge
         {:class my-class
          :id (:id args)
          :name (:name args)
          :rows (or (:rows args) 4)
          :placeholder (:placeholder args)
          :required (:required args)
          :oninvalid (str "this.setCustomValidity('" (:error args) "')")
          :oninput "this.setCustomValidity('')"
          :style (str base-style "resize: vertical;")
          :disabled (:disabled args)
          :readonly (:readonly args)
          :autofocus (:autofocus args)
          :tabindex (:tabindex args)
          :aria-label (:aria-label args)
          :aria-describedby (:aria-describedby args)
          :form (:form args)
          :maxlength (:maxlength args)
          :minlength (:minlength args)
          :spellcheck (:spellcheck args)
          :dirname (:dirname args)}
         (select-keys args [:autocomplete :inputmode :list]))
        (:value args)]]

      ;; All other HTML5 input types
      (or (= type "number") (= type "email") (= type "password") (= type "date") (= type "time")
          (= type "file") (= type "tel") (= type "url") (= type "color") (= type "range")
          (= type "search") (= type "datetime-local") (= type "month") (= type "week"))
      [:div.mb-3
       label-el
       [:input
        (merge
         {:type type
          :id (:id args)
          :name (:name args)
          :placeholder (:placeholder args)
          :value (:value args)
          :required (:required args)
          :class my-class
          :oninvalid (str "this.setCustomValidity('" (:error args) "')")
          :oninput "this.setCustomValidity('')"
          :step (:step args)
          :min (:min args)
          :max (:max args)
          :pattern (:pattern args)
          :autocomplete (:autocomplete args)
          :autofocus (:autofocus args)
          :disabled (:disabled args)
          :readonly (:readonly args)
          :tabindex (:tabindex args)
          :aria-label (:aria-label args)
          :aria-describedby (:aria-describedby args)
          :form (:form args)
          :dirname (:dirname args)
          :size (:size args)
          :maxlength (:maxlength args)
          :minlength (:minlength args)
          :list (:list args)
          :inputmode (:inputmode args)
          :accept (:accept args)
          :spellcheck (:spellcheck args)})]]

      :else
      [:div.mb-3
       label-el
       [:input
        (merge
         {:type (or type "text")
          :id (:id args)
          :name (:name args)
          :placeholder (:placeholder args)
          :value (:value args)
          :required (:required args)
          :class my-class
          :oninvalid (str "this.setCustomValidity('" (:error args) "')")
          :oninput "this.setCustomValidity('')"
          :disabled (:disabled args)
          :readonly (:readonly args)
          :pattern (:pattern args)
          :autocomplete (:autocomplete args)
          :autofocus (:autofocus args)
          :tabindex (:tabindex args)
          :aria-label (:aria-label args)
          :aria-describedby (:aria-describedby args)
          :form (:form args)
          :dirname (:dirname args)
          :size (:size args)
          :maxlength (:maxlength args)
          :minlength (:minlength args)
          :list (:list args)
          :inputmode (:inputmode args)
          :spellcheck (:spellcheck args)})]])))

;; =============================================================================
;; Button Builders
;; =============================================================================

(defn build-primary-input-button
  "Creates a primary styled input button
   Args: {:type string :value string}"
  [args]
  [:input.btn.btn-primary.btn-lg.fw-semibold.shadow-sm.rounded
   {:type (:type args)
    :value (or (:value args) "Submit")}])

(defn build-secondary-input-button
  "Creates a secondary styled input button
   Args: {:type string :value string}"
  [args]
  [:input.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm.rounded
   {:type (:type args)
    :value (or (:value args) "Cancel")}])

(defn build-primary-anchor-button
  "Creates a primary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-primary.btn-lg.fw-semibold.shadow-sm.rounded
   {:type "button"
    :href (:href args)}
   (:label args)])

(defn build-secondary-anchor-button
  "Creates a secondary styled anchor button
   Args: {:label string :href string}"
  [args]
  [:a.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm.rounded
   {:type "button"
    :href (:href args)}
   (:label args)])

(defn build-modal-buttons
  "Creates professional modal buttons with conditional rendering"
  [& args]
  (let [args (first args)
        view (:view args)]
    (list
     (when-not (= view true)
       [:button.btn.btn-primary.btn-lg.fw-semibold.shadow-sm.rounded
        {:type "submit"}
        "Submit"])
     [:button.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm.rounded
      {:type "button"
       :data-bs-dismiss "modal"}
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
       [:button.btn.btn-primary.btn-lg.fw-semibold.shadow-sm.rounded
        {:type "submit"
         :onclick "if(this.form && !this.form.checkValidity()){this.form.reportValidity();return false;}"} ; HTML5 validation
        "Submit"])
     [:a.btn.btn-outline-secondary.btn-lg.fw-semibold.shadow-sm.rounded
      {:type "button"
       :href cancel-url}
      "Cancel"])))

;; =============================================================================
;; Main Form Builder
;; =============================================================================

(defn form
  "Creates a professional form container with Bootstrap 5 styling and themed colors.
   If title is passed, it is rendered in the header. Handles HTML5 validation.
   If :bare is true, returns only the <form>...</form> for modal AJAX."
  ([href fields buttons] (form href fields buttons nil))
  ([href fields buttons title] (form href fields buttons title nil))
  ([href fields buttons title opts]
   (let [bare (:bare opts)]
     (if bare
       ;; Only the <form> for modal AJAX
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
           buttons)]]
       ;; Full card for standalone page
       (list
        [:div.d-flex.justify-content-center.align-items-center.w-100
         {:style "min-height: 45vh;"}
         [:div.card.shadow-lg.w-100
          {:style "max-width: 540px;"}
          (when title
            [:div.card-header
             [:h4.mb-0.fw-bold.text-center title]])
          [:div.card-body.p-4
           [:form {:method "POST"
                   :enctype "multipart/form-data"
                   :action href
                   :class "needs-validation"
                   :novalidate true}
            (anti-forgery-field)
            fields
            [:div.d-flex.gap-2.justify-content-end.mt-4
             (cond
               (and (sequential? buttons) (not (vector? (first buttons))))
               (doall buttons)
               (sequential? buttons)
               (doall buttons)
               :else
               buttons)]]]]])))))

;; =============================================================================
;; Usage Examples (Documentation)
;; =============================================================================

(comment
  ;; Example usage for testing
  (build-field {:type "hidden" :id "user-id" :name "user-id" :value "42"})
  (build-field {:label "Full Name" :type "text" :id "name" :name "name" :placeholder "Enter name" :required true :maxlength 50 :pattern "[A-Za-z ]+" :autocomplete "name" :autofocus true})
  (build-field {:label "Email" :type "email" :id "email" :name "email" :placeholder "Enter email" :required true :autocomplete "email"})
  (build-field {:label "Password" :type "password" :id "pw" :name "pw" :placeholder "Password" :required true :minlength 8 :autocomplete "new-password"})
  (build-field {:label "Age" :type "number" :id "age" :name "age" :min 0 :max 120 :step 1 :required true})
  (build-field {:label "Birthday" :type "date" :id "bday" :name "bday" :required true})
  (build-field {:label "Appointment" :type "datetime-local" :id "appt" :name "appt"})
  (build-field {:label "Favorite Color" :type "color" :id "color" :name "color" :value "#ff0000"})
  (build-field {:label "Satisfaction" :type "range" :id "satisfaction" :name "satisfaction" :min 1 :max 10 :step 1 :value 5})
  (build-field {:label "Phone" :type "tel" :id "phone" :name "phone" :pattern "[0-9\\-\\+ ]{7,15}" :autocomplete "tel"})
  (build-field {:label "Website" :type "url" :id "website" :name "website" :placeholder "https://..."})
  (build-field {:label "Resume" :type "file" :id "resume" :name "resume" :accept ".pdf,.doc,.docx" :multiple true})
  (build-field {:label "Month" :type "month" :id "month" :name "month"})
  (build-field {:label "Week" :type "week" :id "week" :name "week"})
  (build-field {:label "Search" :type "search" :id "search" :name "search" :placeholder "Search..."})
  (build-field {:label "User Level" :type "select" :id "level" :name "level" :value "U" :required true
                :options [{:value "" :label "Select..."}
                          {:value "U" :label "User"}
                          {:value "A" :label "Admin" :disabled true}
                          {:value "S" :label "Sys"}]
                :multiple false :size 1})
  (build-field {:label "Status" :type "radio" :name "active" :value "T"
                :options [{:id "activeT" :label "Active" :value "T"}
                          {:id "activeF" :label "Inactive" :value "F"}]})
  (build-field {:label "Interests" :type "checkbox" :name "interests" :value "sports"
                :options [{:id "sports" :label "Sports" :value "sports"}
                          {:id "music" :label "Music" :value "music"}
                          {:id "tech" :label "Tech" :value "tech"}]})
  (build-field {:label "Comments" :type "textarea" :id "comments" :name "comments" :rows 4 :placeholder "Your comments..." :maxlength 500 :minlength 10 :spellcheck true})

  ;; build-image-field: Image upload with preview
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (build-image-field row)

  ;; build-image-field-script: JS for image preview
  (build-image-field-script)

  ;; build-primary-input-button: Primary submit button
  (build-primary-input-button {:type "submit" :value "Save"})

  ;; build-secondary-input-button: Secondary/cancel button
  (build-secondary-input-button {:type "button" :value "Cancel"})

  ;; build-primary-anchor-button: Primary anchor button
  (build-primary-anchor-button {:label "Go" :href "/go"})

  ;; build-secondary-anchor-button: Secondary anchor button
  (build-secondary-anchor-button {:label "Back" :href "/back"})

  ;; build-modal-buttons: Modal dialog buttons
  (build-modal-buttons {:view false})

  ;; build-form-buttons: Form submit/cancel buttons
  (build-form-buttons {:cancel-url "/" :view false})

  ;; form: Main form container with fields and buttons
  (form "/submit"
        [(build-field {:label "Name" :type "text" :id "name" :name "name" :placeholder "Name" :required true})
         (build-field {:label "Email" :type "email" :id "email" :name "email" :placeholder "Email" :required true})
         (build-field {:label "User Level" :type "select" :id "level" :name "level" :value "U" :required true
                       :options [{:value "" :label "Select..."}
                                 {:value "U" :label "User"}
                                 {:value "A" :label "Admin"}
                                 {:value "S" :label "Sys"}]})]
        (list (build-primary-input-button {:type "submit" :value "Submit"})
              (build-secondary-input-button {:type "button" :value "Cancel"}))
        "Example Form"))
