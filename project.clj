(defproject dev.gethop/encryption "0.2.4-SNAPSHOT"
  :description "Encryption/decryption library"
  :url "https://github.com/gethop-dev/encryption"
  :license {:name "Mozilla Public Licence 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :min-lein-version "2.9.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [caesium "0.15.0"]
                 [com.taoensso/nippy "3.3.0"]
                 [com.taoensso/encore "3.74.1"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]]
  :profiles
  {:dev [:project/dev :profiles/dev]
   :repl {:repl-options {:host "0.0.0.0"
                         :port 4001}}
   :profiles/dev {}
   :project/dev {:plugins [[jonase/eastwood "1.2.3"]
                           [lein-cljfmt "0.8.0"]]}})
