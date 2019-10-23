(defproject magnet/encryption "0.2.0"
  :description "Encryption/decryption library"
  :url "https://github.com/magnetcoop/encryption"
  :license {:name "Mozilla Public Licence 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :min-lein-version "2.9.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [caesium "0.10.0"]
                 [com.taoensso/nippy "2.14.0"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :profiles
  {:dev [:project/dev :profiles/dev]
   :repl {:repl-options {:host "0.0.0.0"
                         :port 4001}}
   :profiles/dev {}
   :project/dev {:plugins [[jonase/eastwood "0.3.4"]
                           [lein-cljfmt "0.6.2"]]}})
