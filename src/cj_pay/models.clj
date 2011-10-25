(ns cj-pay.models
  (:require [simpledb.core :as db]
            [cj-pay.models.user :as users]
            ))

(defn initialize []
  (db/init)
  (when-not (db/get :users)
    ;;db values need to be initialized.. this should only happen once.
    (users/init!)
    ))
