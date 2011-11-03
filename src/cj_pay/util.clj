(ns cj-pay.util
  "Misc Utility Functions. Move these somewhere else???"
  (:require 
            [clj-json.core :as json]))


; Get our google API key stored on disk
(def apikey (slurp "api-key"))

;; Google Git parameters for this app
;; You can figure these out by going to your google api console and having it generate 
;; the Javascript login widget code
(def gitkit-params {:developerKey apikey
                    :companyName "Noir Test"
                    :callbackUrl "http://localhost:8080/authn/callback"
                    :realm ""
                    :userStatusUrl "/authn/userstatus"
                    :loginUrl "/authn/login"
                    :signupUrl "/authn/signup"
                    :homeUrl "/welcome"
                    :logoutUrl "/authn/logout"
                    :language "en"
                    :idps ["Gmail", "Yahoo", "AOL", "Hotmail"],
                    :tryFederatedFirst true
                    :useCachedUserStatus false})

; git params as JSON
(def git-params-json (json/generate-string gitkit-params))