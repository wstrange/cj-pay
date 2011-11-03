(ns cj-pay.views.auth
"Authentication Functions
This uses the Google Identity Toolkit (GIT)
See http://code.google.com/apis/identitytoolkit/v1/acguide.html
"
  
  (:require [cj-pay.views.common :as common]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.response :as resp]
            [noir.request :as request]
            [clojure.string :as string]
            [cj-pay.models.user :as users]
            [cj-pay.util :as util]
            [noir.content.pages :as pages]
            [clj-http.client :as client]
            [clj-json.core :as json]
            
            )
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
            (text-field {:placeholder "Username"} :username username)
            (password-field {:placeholder "Password"} :password))


;; Auth check routes
;; TODO: 

(pre-route "/admin*" {}
           (when-not (users/admin?)
             (resp/redirect "/login")))


;; For Google Identity Toolkit (GIT)


; GIT will post back to query to see if this user is already registered
; Should return a json response of {"registered" :true/false } as appropriate 
(defpage [:post "/authn/userstatus"] {:keys [email]} 
  (println "/userstatus check" email)
  (resp/json {:registered (users/registered? email)}))


; After prompting for a password for a legacy account, 
; GITkit will need to know if a password is correct or incorrect. 
; This endpoint's URL is defined in the JavaScript widget's loginUrl parameter. 
; GITkit will make a POST to this endpoint and expects a JSON object as a response with one parameter.
; If a user entered their password correctly, you should create a 
; user session and log the user in. Then return the following response to GITkit:
; status: This is one of the following:
; "OK" - user entered password correctly
; "passwordError" - password incorrect
;
; Since we don't use passwords this is a no-op
; TODO: Should we return an error?
(defpage [:post "/authn/logincheck"] {:keys [email password]}
  (println "/logincheck " email " pw=" password)
  (resp/json {:status "OK"}))



; Create the verify callback url to GIT
(def verifyurl (str "https://www.googleapis.com/identitytoolkit/v1/relyingparty/verifyAssertion?key=" util/apikey))

; Contstruct the request uri. Needed by git
(defn requestUri [req] 
  " todo: why does (:scheme req ) put a : in front of http??"
  (str "http://" (:server-name req) ":" (:server-port req) (:uri req)))
  
; Create the html response to return to GIT 
; The argument js will be a javascript call that denotes success/failure
(defn- generate-git-response [js]
   (html [:body
          (include-js "https://ajax.googleapis.com/jsapi")
          (javascript-tag "google.load('identitytoolkit', '1.0', {packages: ['notify']});")
          (javascript-tag js)]))
          

; Called after the login has been been verified. 
; Log the user in and return the response to GIT
; See http://code.google.com/apis/identitytoolkit/v1/acguide.html
(defn login-verifed [res]
  (let [email (:verifiedEmail res)
        registered (users/login! email)
        js (json/generate-string {:email email :registered registered})]
    (if (not registered) 
      (users/add! (merge res {:username email})))
    (generate-git-response (str "window.google.identitytoolkit.notifyFederatedSuccess(" js  ")"))))


(defn login-failed [res]
   (generate-git-response "window.google.identitytoolkit.notifyFederatedError()"))
                              
  
;; GIT will callback to this  URL (get or post) once the user tries to
; sign in at the IDP. You must call (POST) GITs verification URL to verify the login
;; If the auth is valid git returns a 200 response and a json body with a bunch of info (verifiedEmail,..)
;; See http://code.google.com/apis/identitytoolkit/v1/acguide.html
; and http://code.google.com/apis/identitytoolkit/v1/reference.html
; This is a pre-route because we need access to the request string and body
(pre-route [:any "/authn/callback"] {:as req} 
  (let [x (json/generate-string { :requestUri (requestUri req) :postBody (:query-string req)})
        foo (println "request x" x)
        result (client/post  verifyurl {:body x  :content-type :json})]
    (if (= (:status result) 200) 
      (login-verifed (json/parse-string (:body result) true))
      (login-failed (:body result)))))


(defpage "/authn/logout" []
  (session/clear!)
  (resp/redirect "/"))

;;
;; git will callback to this url if the user has been verified
;; but has not been registered
(defpage [:any "/authn/signup"] {:keys [email] }
  (println "/authn/signup page req="  email)
  (let [user (users/get-user email)
        displayName (:displayName user)]
  (common/layout 
    [:h1 "Signup page"]
    [:p "Please complete the new user registration process"]
    [:p "You have signed up as " email ". Your displayName is " displayName]
    (link-to "/welcome" "Complete your registration"))))

;; Pages

(defpage "/admin" []
         (common/layout
           [:h1 "Admin Page"]
           [:p "Curent Users"]
           [:ul.items 
            (map user-item (users/all)) ] 
           [:p "end"]
           (link-to "/admin/createdata" "create sample data")
           
           ))



(defpage "/admin/user/edit/:old-name" {:keys [old-name]}
         (let [user (users/get-user old-name)]
           (common/layout
             [:h2 "Edit User"]
             (form-to [:post (str "/admin/user/edit/" old-name)]
                      (user-fields user)
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Submit")]
                        [:li (link-to {:class "delete"} (str "/admin/user/remove/" old-name) "Remove")]]
                      [:button "Submit"]
                      ))))



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
