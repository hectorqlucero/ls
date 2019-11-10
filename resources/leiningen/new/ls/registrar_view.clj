(ns {{name}}.views.registrar
  (:require [{{name}}.models.util :refer [build-field build-button]]))

;; Start registrar
(defn registrar-view [title token]
  [:div.container.col-8
   [:div.card-header
    [:h4.mb-0 [:span {:style "color:rgb(18,123,163);"} title]]]
   [:form.fm {:enctype "multipart/form-data"
              :method "post"
              :style "width:100%;background-color:#EFEFEF;"}
    token
    (build-field
      "Correo Electronico:"
      {:id "email"
       :name "email"
       :class "form-control easyui-validatebox"
       :validType "email"
       :data-options "required:true"})
    (build-field
      "Contraseña:"
      {:id "password"
       :name "password"
       :class "form-control easyui-passwordbox"
       :data-options "required:true"})
    (build-field
      "Confirmar Contraseña:"
      {:id "password1"
       :name "password1"
       :class "form-control easyui-passwordbox"
       :data-options "required:true"
       :validType "confirmPass['#password']"})
    (build-field
      "Nombre:"
      {:id "firstname"
       :name "firstname"
       :class "form-control easyui-textbox"
       :data-options "required:true"})
    (build-field
      "Apellidos:"
      {:id "lastname"
       :name "lastname"
       :class "form-control easyui-textbox"
       :data-options "required:true"})
    (build-button
      "Registrarse"
      {:href "javascript:void(0)"
       :class "btn btn-primary easyui-linkbutton"
       :id "submit"})]])

(defn registrar-scripts []
  [:script
  (str
    "$(document).ready(function() {
        $(\"a#submit\").click(function() {
          $(\"form.fm\").form(\"submit\", {
            onSubmit: function() {
              return $(this).form(\"enableValidation\").form(\"validate\");
            },
            success: function(data) {
              try {
                var dta = JSON.parse(data);
                if(dta.hasOwnProperty('url')) {
                  $.messager.alert({
                    title: 'Procesado!',
                    msg: 'Usuario registrado correctamente!',
                    fn: function() {
                      window.location.href = dta.url
                    }
                  })
                } else if(dta.hasOwnProperty('error')) {
                  $.messager.alert('Error', dta.error, 'error');
                }
              } catch(e) {
                console.error(\"Invalid JSON\");
              }
            }
        });
      });
      
      $(\"#email\").textbox({
        onChange: function(value) {
          var url = \"/table_ref/validate_email/\"+value;
          $.get(url, function(data) {
            try {
              var dta = JSON.parse(data);
              if(dta.hasOwnProperty('email')) {
                if(value == dta.email) {
                  $.messager.alert({
                    title: 'Error',
                    msg: 'Este usuario ya existe!',
                    fn: function() {
                      window.location.href=\"/registrar\";
                    }
                  });
                }
              }
            } catch(e) {}
          });
        }
      });

      $.extend($.fn.validatebox.defaults.rules, {
        confirmPass: {
          validator: function(value, param) {
            var password = $(param[0]).passwordbox('getValue');
            return value == password;
          },
          message: 'La contraseña confirmadora no es igual.'
        }
      });
     });")])
;; End registrar

;; Start reset-password
(defn reset-password-view [title token]
  [:div.container.col-8
   [:div.card-header
    [:h4.mb-0 [:span {:style "color:rgb(18,123,163);"} title]]]
   [:form.fm {:enctype "multipart/form-data"
              :method "post"
              :style "width:100%;background-color:#EFEFEF;"}
    token
    [:input {:type "hidden" :name "username"}]
    (build-field
      "Email:"
      {:id "email"
       :name "email"
       :class "form-control easyui-validatebox"
       :validType "email"
       :data-options "required:true"})
    (build-button
      "Resetear Contraseña"
      {:href "javascript:void(0)"
       :class "btn btn-primary easyui-linkbutton"
       :id "submit"})]])

(defn reset-password-scripts []
  [:script
  (str
    "$(document).ready(function() {
        $(\"a#submit\").click(function() {
          $(\"form.fm\").form(\"submit\", {
            onSubmit: function() {
              return $(this).form(\"enableValidation\").form(\"validate\");
            },
            success: function(data) {
              try {
                var dta = JSON.parse(data);
                if(dta.hasOwnProperty('url')) {
                  $.messager.alert({
                    title: 'Información!',
                    msg: 'Revise su correo electronico donde vera instrucciones para resetear su contraseña!',
                    fn: function() {
                      window.location.href = dta.url
                    }
                  })
                } else if(dta.hasOwnProperty('error')) {
                  $.messager.alert('Error', dta.error, 'error');
                }
              } catch(e) {
                console.error(\"Invalid JSON\");
              }
            }
        });
      });

      function give_error() {
        $.messager.alert({
          title: 'Error',
          msg: 'Este correo no existe en la base de datos, intente otra vez!',
          fn: function() {
            window.location.href=\"/rpaswd\";
          }
        });
      }
      
      $(\"#email\").textbox({
        onChange: function(value) {
          var url = \"/table_ref/validate_email/\"+value;
          $.get(url, function(data) {
            try {
              var dta = JSON.parse(data);
              if(dta.hasOwnProperty('email')) {
                if(value !== dta.email) {
                  give_error();
                }
              } else {
                give_error();
              }
            } catch(e) {
              give_error()
            }
          });
        }
      });

     });")])
;; End reset-password

;; Start reset-jwt
(defn reset-jwt-view [title token username]
  [:div.container.col-8
   [:div.card-header
    [:h4.mb-0 [:span {:style "color:rgb(18,123,163);"} title]]]
   [:form.fm {:enctype "multipart/form-data"
              :method "post"
              :style "width:100%;background-color:#EFEFEF;"}
    token
    [:input {:type "hidden"
             :name "username"
             :value username}]
    (build-field
      "Contraseña:"
      {:id "password"
       :name "password"
       :class "form-control easyui-passwordbox"
       :data-options "required:true"})
    (build-field
      "Confirmar Contraseña:"
      {:id "password1"
       :name "password1"
       :class "form-control easyui-passwordbox"
       :data-options "required:true"
       :validType "confirmPass['#password']"})
    (build-button
      "Cambiar Contraseña"
      {:href "javascript:void(0)"
       :class "btn btn-primary easyui-linkbutton"
       :id "submit"})]])

(defn reset-jwt-scripts []
  [:script
  (str 
    "$(document).ready(function() {
      $.extend($.fn.validatebox.defaults.rules, {
        confirmPass: {
          validator: function(value, param) {
            var password = $(param[0]).passwordbox('getValue');
            return value == password;
          },
          message: 'La contraseña confirmadora no es igual.'
        }
      })

      $(\"a#submit\").click(function() {
        $(\".fm\").form(\"submit\", {
          url: \"/reset_password\",
          onSubmit: function() {
            return $(this).form(\"enableValidation\").form(\"validate\");
          },
          success: function(data) {
            var dta = JSON.parse(data);
            try {
              if(dta.hasOwnProperty('url')) {
                $.messager.alert({
                  title: 'Processado!',
                  msg: 'Su contraseña se ha cambiado!',
                  fn: function() {
                    window.location.href = dta.url;
                  }
                })
              } else if(dta.hasOwnProperty('error')) {
                $.messager.alert('Error', dta.error, 'error');
              }
            } catch(e) {
              console.error(\"Invalid JSON\");
            }
          }
        });
      });
    });")])
;; End reset-jwt
