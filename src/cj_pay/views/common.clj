(ns cj-pay.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers)
  (:require [cj-pay.models.user :as users]
            )
  )


(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :blueprint (include-css "/css/blueprint/screen.css")
               :default (include-css "/css/default.css")
               :reset (include-css "/css/reset.css")
               :util.js (include-js "/js/util.js")
              })

(defpartial build-head [incls]
            [:head
             [:title "The Pay Master"]
             (map #(get includes %) incls)])

(def admin-links [{:url "/admin" :text "Admin/Main"}
                  {:url "/admin/users" :text "Add Users"}
                ])
(def main-links [{:url "/admin" :text "Admin"}])

(defpartial link-item [{:keys [url cls text]}]
            [:li
             (link-to {:class cls} url text)])


(defpartial nav-content []
  [:div.nav 
   [:h2 "Links"]
   [:ul.nav (map link-item admin-links)]
  ]
   )
   
(defpartial header-content []
       [:div.header 
         [:h1.push-1.span-8 "The Pay Master"]
         [:p.push-5.last (logged-in-status)]
         [:hr]
         ])

(defpartial logged-in-status [] 
  (if  (users/me) 
    [:span "Logged in as " (users/me) " - " (link-to "/logout" "Logout")]
    [:span (link-to "/login" "Login")]))
   
;; Layouts

(defpartial layout [& content]
  (html 
        (build-head [:blueprint :jquery :util.js])
                [:body
                  [:div.container.showgridx              ; change showgridx to showgrid to show blueprint grid
                    (header-content)  ; 24 col wide header
                    [:div.span-4 (nav-content)]          ; Nav bar that is 4 cols wide
                    [:div.span-20.last content ]         ; 20 cols for content
                    [:p.span-24 " "]   ; space 
                    [:hr]
                    [:div {:class "clear prepend-8 last"}  "Copyright (c) 2011 Warren Strange"] ; footer
                  ]
                ]))


(defpartial admin-layout [& content]
            (html5
              (build-head [:reset :default :jquery :util.js])
              [:body
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/admin" "Admin")]
                  [:ul.nav
                   (map link-item admin-links)]]
                 content]]]))


(defpartial main-layout [& content]
            (html5
              (build-head [:reset :default :jquery :util.js])
              [:body
              
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/" "The Payment")]
                  [:ul.nav
                   (map link-item main-links)]]
                 (logged-in-status)
                 content]]]))

