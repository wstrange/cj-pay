(ns cj-pay.views.welcome
  (:require [cj-pay.views.common :as common]
            [noir.content.pages :as pages])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
         (common/layout
           [:h1 "Welcome to the Pay Master"]
           [:p "You will enter your payment details here"]))

(defpage "/welcome" []
         (common/layout
           [:h1 "Welcome to the Pay Master"]
           [:p "Logged in user landing page"]))
