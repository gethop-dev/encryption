(defproject magnet/encryption "0.1.0"
  :description "Encryption/decryption library"
  :url "https://github.com/magnetcoop/encryption"
  :license {:name "Mozilla Public Licence 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[caesium "0.10.0"]
                 [com.taoensso/nippy "2.14.0"]
                 [org.clojure/clojure "1.9.0"]]
  :profiles
  {:dev {:plugins [[jonase/eastwood "0.3.4"]
                   [lein-cljfmt "0.6.2"]]}
   :repl {:repl-options {:host "0.0.0.0"
                         :port 4001}
          :plugins [[cider/cider-nrepl "0.18.0"]]}})
