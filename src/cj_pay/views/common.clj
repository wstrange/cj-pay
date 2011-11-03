(ns cj-pay.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers)
  (:require [cj-pay.models.user :as users]
            [cj-pay.util :as util]))

; Define all of the JS includes that we might need
(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :jquery-ui (include-js "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.2/jquery-ui.min.js")
               :blueprint (include-css "/css/blueprint/screen.css")
               :default (include-css "/css/default.css")
               :reset (include-css "/css/reset.css")
               :util.js (include-js "/js/util.js")
               :google-apis (include-js "https://ajax.googleapis.com/ajax/libs/googleapis/0.0.4/googleapis.min.js")
               :jsapi (include-js "https://ajax.googleapis.com/jsapi")
              })
; Define any scripts 
; These are needed by GIT. The api page will show you how to generate these
; The script generates a Sign In button that is inserted in the "chooser" div
(def javascripts {:git-load (javascript-tag " google.load('identitytoolkit', '1.0', {packages: ['ac']});")
                  :git-init (javascript-tag (str "$(function(){window.google.identitytoolkit.setConfig(" 
                       util/git-params-json ");$('#chooser').accountChooser();});"))})

(defpartial build-head [incls scripts]
            [:head
             [:title "The Pay Master"]
             (map #(get includes %) incls)
             (map #(get javascripts %) scripts) 
             ])

(def admin-links [{:url "/admin" :text "Admin/Main"}
                  {:url "/admin/users" :text "Add Users"}
                ])
(def main-links [{:url "/admin" :text "Admin"}])

(defpartial link-item [{:keys [url cls text]}]
            [:li
             (link-to {:class cls} url text)])

; Navigation Side bar
(defpartial nav-content []
  [:div.nav 
   [:h2 "Links"]
   [:ul.nav (map link-item admin-links)]
  ]
   )

;; Display the logged in status or login link
; The chooser div will get a GIT Sign in Button inserted
(defpartial logged-in-status [] 
  (let [u (users/me)]
  (if u  ; If user logged in?
    [:div u " - " (link-to "/authn/logout" "Logout")]
    [:div#chooser "Login"])))

; Top master header
(defpartial header-content []
       [:div.header 
         [:br]
         [:h1.span-10 "The Pay Master"]
         [:p.span-8.last {:align "right"}(logged-in-status)]
         [:hr]
         ])

  
;; Layouts

; Default layout
(defpartial layout [& content]
  (html 
        (build-head [:blueprint :jquery :util.js] [])
                [:body
                  [:div.container.showgrid             ; change showgridx to showgrid to show blueprint grid
                    (header-content)  ; 24 col wide header
                    [:div.span-4 (nav-content)]          ; Nav bar that is 4 cols wide
                    [:div.span-20.last content ]         ; 20 cols for content
                    [:p.span-24 " "]   ; space 
                    [:hr]
                    [:div {:class "clear prepend-8 last"}  "Copyright (c) 2011 Warren Strange"] ; footer
                  ]
                ]))

;; Layout when the user is not logged in.
;; Will display login button 
(defpartial public-layout [& content]
  (html5 
    (build-head [:blueprint :jquery :util.js :jquery-ui :jsapi :google-apis] [:git-load :git-init])
          [:body
                  [:div.container.showgrid             ; change showgridx to showgrid to show blueprint grid
                    (header-content)  ; 24 col wide header
                    [:div.span-4 (nav-content)]          ; Nav bar that is 4 cols wide
                    [:div.span-20.last content ]         ; 20 cols for content
                    [:p.span-24 " "]   ; space 
                    [:hr]
                    [:div {:class "clear prepend-8 last"}  "Copyright (c) 2011 Warren Strange"] ; footer
                  ]]))

  
;; For logged in pages
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

