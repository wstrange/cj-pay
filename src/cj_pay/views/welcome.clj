(ns cj-pay.views.welcome
  (:require [cj-pay.views.common :as common]
            [noir.content.pages :as pages])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
         (common/public-layout
           [:h1 "Welcome to the Pay Master"]
           [:p "This is the public page"]))

(defpage "/welcome" []
         (common/layout
           [:h1 "Welcome to the Pay Master"]
           [:p "Logged in user landing page"]))
