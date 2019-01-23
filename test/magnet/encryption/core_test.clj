;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.encryption.core-test
  (:require [caesium.randombytes :as randombytes]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [magnet.encryption.core :as core])
  (:import [clojure.lang ExceptionInfo]))

(defn enable-instrumentation [f]
  (-> (stest/enumerate-namespace 'magnet.encryption.core) stest/instrument)
  (f))

(use-fixtures :once enable-instrumentation)

(def sample-message "Hello world")

(def encryption-key (core/new-key!))

(defn any-pos-int-but
  "Returns an integer other than the one passed as argument.
 The integer is bounded by the `max-num` argument that has to be bigger than `x`.
 `max-num` defaults to 100."
  ([x]
   {:pre [(pos? x)]}
   (any-pos-int-but x 100))
  ([x max-num]
   {:pre [(pos? x)
          (< x max-num)
          (> max-num 1)]}
   (let [shot (inc (rand-int max-num))]
     (if (= shot x)
       (any-pos-int-but x max-num)
       shot))))

(deftest encryption
  (is (=
       (core/decrypt-value
        (core/encrypt-value! sample-message encryption-key)
        encryption-key)
       sample-message))
  (is
   (let [my-map {:name "John Doe"
                 :fav-dishes ["pizza" "hamburgers"]}]
     (=
      (core/decrypt-value
       (core/encrypt-value! my-map encryption-key)
       encryption-key)
      my-map))
   "Should be able to encrypt/decrypt more complex data structures")

  (is
   (thrown? ExceptionInfo
            (let [enc-key (randombytes/randombytes (any-pos-int-but core/keybytes))]
              (core/encrypt-value! sample-message enc-key)))
   "Should fail due to wrong number of bytes in encryption key")

  (is
   (thrown? ExceptionInfo
            (let [enc-key "123"]
              (core/encrypt-value! sample-message enc-key)))
   "Should fail because key is not a byte-array")

  (is
   (thrown? ExceptionInfo
            (core/decrypt-value (caesium.randombytes/randombytes (rand-int 100))
                                encryption-key))
   "Should fail because value to decrypt is faked.")

  (is
   (thrown? ExceptionInfo
            (let [my-map {:name "John Doe"
                          :fav-dishes ["pizza" "hamburgers"]}
                  encrypted-ba (core/encrypt-value! my-map encryption-key)
                  ;; Flip least significant bit of a ciphertext byte (chosen at random)
                  pos (rand-int (count encrypted-ba))
                  _ (aset-byte encrypted-ba pos (bit-xor (get encrypted-ba pos) 0x01))]
              (core/decrypt-value encrypted-ba encryption-key)))
   "Should fail because value to decrypt has been manipulated.")

  (is
   (thrown? ExceptionInfo
            (core/decrypt-value
             (core/encrypt-value! sample-message encryption-key)
             (core/new-key!)))
   "Should fail because key used for decryption is not the same as key used for encryption."))
