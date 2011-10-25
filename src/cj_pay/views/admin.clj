(ns cj-pay.views.admin
  (:require [cj-pay.views.common :as common]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.response :as resp]
            [clojure.string :as string]
            [cj-pay.models.user :as users]
            [noir.content.pages :as pages])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
        hiccup.form-helpers))


;; Partials 

(defpartial error-text [errors]
            [:p (string/join "<br/>" errors)])

(defpartial user-item [{:keys [username]}]
            [:li
             (link-to (str "/admin/user/edit/" username) username)])


(defpartial user-fields [{:keys [username] :as usr}]
            (vali/on-error :username error-text)
            (label "username" "Username")
            (text-field {:placeholder "Username"} :username username)
            (label "password" "Password")
            (password-field {:placeholder "Password"} :password))

;; Pages

(defpage "/admin" []
         (common/admin-layout
           [:h1 "Admin Page"]
           [:p "Curent Users"]
           [:ul.items 
            (map user-item (users/all)) ] 
           [:p "end"]
           (link-to "/admin/createdata" "create sample data")
           
           ))



(defpage "/admin/user" []  
  "test")

(defpage "/admin/user/edit/:old-name" {:keys [old-name]}
         (let [user (users/get-username old-name)]
           (common/admin-layout
             [:h2 "Edit User"]
             (form-to [:post (str "/admin/user/edit/" old-name)]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Submit")]
                        [:li (link-to {:class "delete"} (str "/admin/user/remove/" old-name) "Remove")]]
                      (user-fields user)))))

;; Post action after user is updated
(defpage [:post "/admin/user/edit/:old-name"] {:keys [old-name] :as user}
  (println "user is " user)
         (if (users/edit! user)
           (resp/redirect "/admin")
           (render "/admin/user/edit/:old-name" user)))

(defpage "/admin/user/remove/:user-name" {:keys [user-name]}
  (println "removing" user-name)
  (users/remove! user-name)
  (resp/redirect "/admin"))


;; Create some sample data
(defpage "/admin/createdata" [] 
  (dotimes [i 5]
    (users/add! {:username (str "testuser" i) :password "password"}))
  (resp/redirect "/admin"))
