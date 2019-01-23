(defproject magnet/encryption "0.1.0"
  :description "Encryption/decryption library"
  :license {:name "Mozilla Public Licence 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[caesium "0.10.0"]
                 [com.taoensso/nippy "2.14.0"]
                 [org.clojure/clojure "1.9.0"]]
  :signing {:gpg-key "447B768548DBB3B7D7D39518AD76B1599339C36A"}
  :profiles
  {:dev {:plugins [[jonase/eastwood "0.3.4"]
                   [lein-cljfmt "0.6.2"]]}
   :repl {:repl-options {:host "0.0.0.0"
                         :port 4001}
          :plugins [[cider/cider-nrepl "0.18.0"]]}})
