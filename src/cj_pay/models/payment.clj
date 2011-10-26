(ns cj-pay.models.payment
  (:require [simpledb.core :as db]
            [noir.util.crypt :as crypt]
            [noir.validation :as vali]
            [noir.session :as session]))


;; Payment record
;; TODO: Should we add an id
(defrecord Payment [user companyId amount date])


(defn add! [payment] 
    (db/put! :payments (conj (all) payment)))


(defn all []
  (db/get :payments))
  
(defn allk [] 
  (db/get :payments))


(defn payments-for-user [user-name] 
  (filter #(=  (:user %) user-name) (all)))

;; Some sample test data
(def samples 
  (map #(Payment. (str "test" %)  "acme" 20.00 "dec 1") (range 5)))

(defn mksamples [] 
  (doseq [x samples] 
    (add! x)
    (println "saving " x)))

(defn dotest []  (db/clear!) (mksamples)) 
