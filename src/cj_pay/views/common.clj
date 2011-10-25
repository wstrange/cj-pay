(ns cj-pay.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))


(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :default (include-css "/css/default.css")
               :reset (include-css "/css/reset.css")
               :util.js (include-js "/js/util.js")
              })

(defpartial build-head [incls]
            [:head
             [:title "The Pay Master"]
             (map #(get includes %) incls)])

(def admin-links [{:url "/admin" :text "Admin/Main"}
                  {:url "/admin/users" :text "Users"}
                ])

(defpartial link-item [{:keys [url cls text]}]
            [:li
             (link-to {:class cls} url text)])


;; Layouts

(defpartial layout [& content]
            (html5
              (build-head [:reset :default :jquery :util.js])
               
              [:body
               [:div#wrapper
                content]]))

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

