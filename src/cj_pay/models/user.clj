(ns cj-pay.models.user
  (:require [somnium.congomongo :as mongo]
            [noir.util.crypt :as crypt]
            [noir.validation :as vali]
            [noir.session :as session]))

;; User model 
; :username :firstname :lastname 
;; Gets


(defn get-user [username]
  (mongo/fetch-one :users :where {:username username}))

; return true / false
; Due to json encoding we must generate fales (not a nil)
(defn registered? [u] 
  "Return true/false if the user is registered
   Should registiered include a signup completion check?"
  (if (get-user (:username u))
    true
    false))
    
(defn admin? []
  (session/get :admin))

(defn me []
  (session/get :username))

(defn all [] 
  "Return all users... dodgy..."
  (mongo/fetch :users))


;; Mutations and Checks

(defn prepare [{password :password :as user}]
  (assoc user :password (crypt/encrypt password)))

(comment 
(defn valid? [{:keys [username password]}]
  (vali/rule (not (db/get-in :users [username]))
             [:username "That username is already taken"])
  (vali/rule (vali/min-length? username 3)
             [:username "Username must be at least 3 characters."])
  ;(vali/rule (vali/min-length? password 5)
  ;           [:password "Password must be at least 5 characters."])
  (not (vali/errors? :username :password))))


;; Operations

(defn add! [user]
  (mongo/insert! :users user))

(defn login! [username]
  " Log the user in. Return true if they are already registered"
  (session/put! :username username)
  (registered? username))
   

(defn edit! [user changes]
  (mongo/update! :users user (merge user changes)))

(defn remove! [user]
  (mongo/destroy! :users user))

