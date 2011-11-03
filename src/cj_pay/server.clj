(ns cj-pay.server
  (:require [noir.server :as server])
   (:use somnium.congomongo))


(def conn (make-connection "cjpay-db"  
                                :host "127.0.0.1"  
                                :port 27017))
; Set up Mongo DB connection
(set-connection! conn)

(server/load-views "src/cj_pay/views/")


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'cj-pay})))



; For dev - start server on load
(-main)