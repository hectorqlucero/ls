(ns {{name}}.models.form
  (:require
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [{{name}}.migrations :refer [config]]))

(defn password-form
  [title]
  (list
   [:div.container.border.w-25.bg-light
    [:legend title]
    [:form {:method "POST"
            :action "/change/password"}
     (anti-forgery-field)
     [:div.form-group
      [:label.fw-bold {:for "email"} "Email:"]
      [:input.form-control {:id "email"
                            :name "email"
                            :type "email"
                            :placeholder "Email here..."}]]
     [:div.form-group
      [:label.fw-bold {:for "password"} "New Password"]
      [:input.form-control {:id "password"
                            :name "password"
                            :type "password"
                            :style "margin-bottom:5px;"
                            :placeholder "Tu contraseña aqui..."}]]
     [:input.btn.btn-outline-success {:type "submit"
                                      :value "Change Password"}]]]))

(defn login-form
  [title href]
  (list
   [:div.container.border.w-50.bg-light
    [:legend title]
    [:form {:method "POST"
            :action href}
     (anti-forgery-field)
     [:div.form-group
      [:label.font-weight-bold {:for "username"} "Email:"]
      [:input.form-control {:id "username"
                            :name "username"
                            :type "email"
                            :required "true"
                            :class "mandatory"
                            :oninvalid "this.setCustomValidity('Email is required...')"
                            :oninput "this.setCustomValidity('')"
                            :placeholder "Email aqui..."}]]
     [:div.form-group
      [:label.font-weight-bold {:for "password"} "Password:"]
      [:input.form-control {:id "password"
                            :style "margin-bottom:5px;"
                            :name "password"
                            :required "true"
                            :class "mandatory"
                            :oninvalid "this.setCustomValidity('Password is required...')"
                            :oninput "this.setCustomValidity('')"
                            :placeholder "Password here..."
                            :type "Password"}]]
     [:input.btn.btn-outline-success {:type "submit"
                                      :value "Login"
                                      :style "margin-right:2px;"}]
     [:a.btn.btn-outline-info {:role "button"
                               :href "/change/password"} "Change Password"]]]))
;; Start form
(defn build-hidden-field
  "args:type,id,name,value"
  [args]
  [:input {:type "hidden"
           :id (:id args)
           :name (:name args)
           :value (:value args)}])

(defn build-image-field
  [row]
  (list
   [:input {:id "file"
            :name "file"
            :type "file"}]
   [:div {:style "float:left;margin-right:2px;"}
    [:img#image1 {:width "95"
                  :height "71"
                  :src (str (:path config) (:imagen row))
                  :onError "this.src='/images/placeholder_profile.png'"
                  :style "margin-right:wpx;cursor:pointer;"}]]))

(defn build-image-field-script
  []
  [:script
   (str
    "
    $(document).ready(function() {
      $('img').click(function() {
        var img = $(this);
        if(img.width() < 500) {
          img.animate({width: '500', height: '500'}, 1000);
        } else {
          img.animate({width: img.attr(\"width\"), height: img.attr(\"height\")}, 1000);
        }
      });
    });
    ")])

(defn build-dashboard-image
  [row]
  (list
   [:div {:style "float:left;margin-right:2px;"}
    [:img#image1 {:width "32"
                  :height "32"
                  :src (str (:path config) (:imagen row))
                  :onError "this.src='/images/placeholder_profile.png'"
                  :style "margin-right:wpx;cursor:pointer;"}]]))

(defn build-dashboard-image-script
  []
  [:script
   (str
    "
    $(document).ready(function() {
      $('img').click(function() {
        var img = $(this);
        if(img.width() < 500) {
          img.animate({width: '500', height: '500'}, 1000);
        } else {
          img.animate({width: img.attr(\"width\"), height: img.attr(\"height\")}, 1000);
        }
      });
    });
    ")])

(defn build-field
  "args:label,type,id,name,placeholder,required,error,value"
  [args]
  (let [my-class (str "form-control" (when (= (:required args) true) " mandatory"))
        args (assoc args :class my-class)]
    (list
     [:div.form-group
      [:label.font-weight-bold {:for (:name args)} (:label args)]
      [:input args]])))

(defn build-textarea
  "args:label,id,name,placeholder,required,error,value"
  [args]
  (list
   [:div.form-group
    [:label.font-weight-bold {:for (:name args)} (:label args)]
    [:textarea {:id (:id args)
                :name (:name args)
                :rows (:rows args)
                :placeholder (:placeholder args)
                :required (:required args)
                :class (str "form-control" (when (= (:required args) true) " mandatory"))
                :oninvalid (str "this.setCustomValidity('" (:error args) "')")
                :oninput "this.setCustomValidity('')"} (:value args)]]))

(defn build-select
  "args:label,id,name,required,error"
  [args]
  (let [options (:options args)]
    (list
     [:div.form-group
      [:label.font-weight-bold {:for (:name args)} (:label args)]
      [:select {:id (:id args)
                :name (:name args)
                :required (:required args)
                :class (str "form-control form-select" (when (= (:required args) true) " mandatory"))
                :oninvalid (str "this.setCustomValidity('" (:error args) "')")
                :oninput "this.setCustomValidity('')"}
       (map (partial (fn [option]
                       (list
                        [:option {:value (:value option)
                                  :selected (if (= (:value args) (:value option)) true false)} (:label option)]))) options)]])))

(defn build-radio
  [args]
  (let [options (:options args)]
    (list
     [:div.form-group
      [:label.font-weight-bold {:for "#"} (:label args)]
      (map (partial (fn [option]
                      (list
                       [:div.form-check
                        [:input.form-check-input {:type "radio"
                                                  :id (:id option)
                                                  :name (:name args)
                                                  :value (:value option)
                                                  :checked (if (= (:value args) (:value option)) true false)}
                         [:label.font-weight-bold {:for (:id option)} (:label option)]]]))) options)])))

(defn build-primary-input-button
  "args: type,value"
  [args]
  [:input.btn.btn-outline-success {:type (:type args)
                                   :style "padding:5px;margin:5px;"
                                   :value (:value args)}])

(defn build-secondary-input-button
  "args: type,value"
  [args]
  [:input.btn.btn-outline-info {:type (:type args)
                                :style "padding:5px;margin:5px;"
                                :value (:value args)}])

(defn build-primary-anchor-button
  "args: label,href"
  [args]
  [:a.btn.btn-outline-success {:type "button"
                               :href (:href (:href args))} (:label args)])

(defn build-secondary-anchor-button
  "args: label,href"
  [args]
  [:a.btn.btn-outline-info {:type "button"
                            :href (:href (:href args))} (:label args)])

(defn build-modal-buttons
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
(defn form
  [href fields buttons]
  (list
   [:div.container.border.bg-light
    [:form {:method "POST"
            :enctype "multipart/form-data"
            :action href}
     (anti-forgery-field)
     fields
     buttons]]))
;; End form

(comment
  "Ejemplo de uso: value=mysql data"
  (build-radio {:label "Status"
                :name "active"
                :value "T"
                :options [{:id "activeT"
                           :label "Active"
                           :value "T"}
                          {:id "activeF"
                           :label "Inactive"
                           :value "F"}]})
  (build-select {:label "Level:"
                 :id "level"
                 :name "level"
                 :value "U"
                 :required true
                 :error "El nivel es requerido..."
                 :options [{:value ""
                            :label "Seleccionar nivel..."}
                           {:value "U"
                            :label "User"}
                           {:value "A"
                            :label "Admin"}
                           {:value "S"
                            :label "System"}]})
  (build-hidden-field {:id "id"
                       :name "id"
                       :value "El valor de la base de datos"})
  (build-textarea {:label "Comentarios"
                   :id "comentarios"
                   :name "comentarios"
                   :rows "3"
                   :placeholder "Comentarios aqui..."
                   :required true
                   :error "El comentario es requerido!"
                   :value "El perro loco de Mexicali!"})
  (build-field {:label "Nombre" ;; text field example
                :type "text"
                :id "nombre"
                :name "nombre"
                :placeholder "El nombre aqui..."
                :required false
                :value (:nombre "row")})
  (build-field {:label "Email" ;; email field example
                :type "email"
                :id "correo_electronico"
                :name "correo_electronico"
                :placeholder "El correo electronico aqui!"
                :required false
                :value (:correo_electronico "row")})
  (build-field {:label "Buscar contacto"
                :type "search"
                :id "buscar_contacto"
                :name "buscar_contacto"
                :placeholder "Busqueda aqui!"
                :required false
                :value (:buscar_contacto "row")})
  (build-field {:label "Celular" ;; phone field example
                :type "tel"
                :id "celular"
                :name "celular"
                :placeholder "Celular aqui!"
                :required false
                :value (:celular "row")})
  (build-field {:label "URL" ;; url field exampl
                :type "url"
                :id "url_pagina"
                :name "url_pagina"
                :placeholder "url de pagina aqui!"
                :required false
                :value (:url_pagina "row")})
  (build-field {:label "Edad" ;; number field example
                :type "number"
                :id "edad"
                :name "edad"
                :min "1"
                :max "120"
                :step "1"
                :placeholder "Edad aqui!"
                :required false
                :value (:edad "row")})
  (build-field {:label "Fecha de Nacimiento" ;; date field example
                :type "date"
                :id "nacimiento"
                :name "nacimiento"
                :required false
                :value (:nacimiento "row")})
  (build-field {:label "Cuando estaras disponible este verano?" ;; date with controls- does not work on all browsers
                :type "date"
                :id "disponible_fecha"
                :name "disponible_fecha"
                :min "2024-06-01"
                :max "2024-08-31"
                :step "7"
                :balue (:disponible_fecha "row")})
  (build-field {:label "Mes" ;; month field example - does not work on all browsers
                :type "month"
                :id "mes"
                :name "mes"
                :required false
                :value (:mes "row")})
  (build-field {:label "Semana" ;; week field example - does not work on all browsers
                :type "week"
                :id "semana"
                :name "semana"
                :required false
                :value (:semana "row")})
  (build-field {:label "Escoja un color" ;; color field example
                :type "color"
                :id "color"
                :name "color"
                :required false
                :value (:color "row")}))
