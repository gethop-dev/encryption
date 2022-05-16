;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/
(ns dev.gethop.encryption.core
  (:require [caesium.crypto.secretbox :as secretbox]
            [caesium.randombytes :as random]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [taoensso.encore :as enc]
            [taoensso.nippy :as nippy]
            [taoensso.nippy.encryption :refer [IEncryptor]]))

(def ^:const keybytes
  "Encryption/decryption key length, in bytes."
  ;; Define our own constant, so users of the library don't need to
  ;; require 3rd party namespaces and know about the specific
  ;; underlying implementation libraries' details.
  secretbox/keybytes)

(defn- get-fresh-nonce!
  "Get a fresh nonce, using a cryptographically secure random source."
  []
  ;; Nonces are critital to guarantee that encrypting the same input
  ;; data with the same encryption key twice don't produce the same
  ;; final encrypted data. Otherwise, we leak information, even if it's
  ;; encrypted (and other kinds of attacks are possible as well).
  ;;
  ;; Thus nonces must be used only once with the same key. We cannot
  ;; reuse them, and have to create them fresh *every single time*.
  ;;
  ;; However nonces don't need to be confidential, so we can store
  ;; them in plain sight without problem.
  ;;
  ;; According to the NaCl authors (highly respected cryptographers),
  ;; when using the default `crypto_secretbox` construct from
  ;; nacl/libsodium/caesium (crypto_secretbox_xsalsa20poly1305) nonces
  ;; are long enough that randomly generated nonces have negligible risk
  ;; of collision (reuse).
  (random/randombytes secretbox/noncebytes))

(defn encrypt-ba
  "Encrypt the byte-array `ba` with encryption key `key`.
  Returns a byte array containing the encryption nonce
  of (`caesium.crypto.secretbox/noncebytes` size`, followed by the
  encrypted values of the original `ba` array. It can be fed directly
  into `decrypt-ba` function."
  [key ba]
  (let [nonce (get-fresh-nonce!)
        cipher-text (secretbox/secretbox-easy ba nonce key)]
    (enc/ba-concat nonce cipher-text)))

(s/def ::byte-array bytes?)
(s/def ::crypt-key (s/and ::byte-array #(= (count %) keybytes)))
(s/def ::encrypt-ba-args (s/cat :key ::crypt-key :ba ::byte-array))
(s/def ::encrypt-ba-ret ::byte-array)
(s/fdef encrypt-ba
  :args ::encrypt-ba-args
  :ret  ::encrypt-ba-ret)

(defn decrypt-ba
  "Decrypt the byte-array `ba` with encryption key `key`.
  This function assumes that `ba` contains the original nonce value
  used to encrypt it at the beginning of the byte array, followed by
  the values to decrypt."
  [key ba]
  (let [[nonce cipher-text] (enc/ba-split ba secretbox/noncebytes)]
    (secretbox/secretbox-open-easy cipher-text nonce key)))

(s/def ::decrypt-ba-args (s/cat :key ::crypt-key :ba ::byte-array))
(s/def ::decrypt-ba-ret ::byte-array)
(s/fdef decrypt-ba
  :args ::decrypt-ba-args
  :ret  ::decrypt-ba-ret)

;; Implement our own custom encryptor for Nippy freeze/thaw, using an
;; authenticated encryption construction that is non-malleable
;; (libsodium/caesium crypto_secretbox)
(deftype CaesiumSecretBoxEncryptor [header-id]
  IEncryptor
  (header-id [_] header-id)
  (encrypt [_ key ba]
    (encrypt-ba key ba))
  (decrypt [_ key ba]
    (decrypt-ba key ba)))

(def caesium-secretbox-encryptor
  (->CaesiumSecretBoxEncryptor :caesium-secretbox-easy))

(defn new-key!
  "Generates a new encryption/decryption key.
  The key has the right cryptographic properties to be used with
  `encrypt-value` and `decrypt-value`."
  []
  (secretbox/new-key!))

(defn encrypt-value!
  "Encrypt `val` using `crypt-key` as the encryption key.
  Returns an opaque byte array structure containing all the elements
  needed to decrypt it (except the decryption key)."
  [val crypt-key]
  (nippy/freeze val {:encryptor caesium-secretbox-encryptor :password crypt-key}))

(s/def ::encrypt-value-args (s/cat :val any? :crypt-key ::crypt-key))
(s/def ::encrypt-value-ret ::byte-array)
(s/fdef encrypt-value!
  :args ::encrypt-value-args
  :ret  ::encrypt-value-ret)

(defn decrypt-value
  "Decrypt `encrypted-val` using `crypt-key` as the decryption key."
  [encrypted-val crypt-key]
  (nippy/thaw encrypted-val {:encryptor caesium-secretbox-encryptor :password crypt-key}))

(s/def ::decrypt-value-args (s/cat :encrypted-val ::byte-array :crypt-key ::crypt-key))
(s/def ::decrypt-value-ret any?)
(s/fdef decrypt-value
  :args ::decrypt-value-args
  :ret  ::decrypt-value-ret)
