[![Build Status](https://travis-ci.org/magnetcoop/encryption.svg?branch=master)](https://travis-ci.org/magnetcoop/encryption)
# Encryption

A library for encrypting and decrypting arbitrary Clojure values,
using [caesium](https://github.com/lvh/caesium) symmetric encryption
primitives.

## Installation

[![Clojars Project](https://clojars.org/magnet/encryption/latest-version.svg)](https://clojars.org/magnet/encryption)

**IMPORTANT**: `caesium` depends on
[libsodium](https://github.com/jedisct1/libsodium) native library
being installed in the development/production environment. Make sure
you install the `libsodium` library version recommended for you
environment before trying to use this encryption library. Otherwise
execution/compilation will fail with some mysterious syntax error messages
in `caesium/binding.clj`.

## Usage

This library provides the following methods:

* `new-key!` Create a new symmetric key for encryption/decryption. The
  key has the right cryptographic properties to be used with
  `encrypt-value` and `decrypt-value`. Returns a byte array with the
  key.
* `encrypt-value! [val key]` Encrypt `val` (any Clojure value)
  using `key` as the encryption key. Returns an opaque byte
  array structure containing all the elements needed to decrypt it
  (except the decryption key). Throws an exception if `key` is
  not a valid encryption key.
* `decrypt-value [encrypted-val key]` Decrypt `encrypted-val`
  and return its original value, using `key` as the decryption
  key. Throws an exception if `key` is not a valid decryption
  key, or if `encrypted-val` is not a valid encrypted value (as
  returned by `encrypt-value`).

### Example code

``` clojure
user=> (require '[magnet.encryption.core :as core])
nil
user=> (def key (core/new-key!))
#'user/key
user=> key
[-122, -75, 125, 109, -111, 102, 69, -68, -8, -55, 0, -56, -75, -91,
 121, 20, -44, -60, 52, 75, -75, -120, -36, 53, -107, 71, -114, 33,
 -8, -59, 52, -32]
user=> (def simple-value "Hello world")
#'user/simple-value
user=> (def complex-value {:name "John Doe"
                           :fav-dishes ["pizza" "hamburgers"]})
#'user/complex-value
user=> (def encrypted-simple-value (core/encrypt-value! simple-value key))
#'user/encrypted-simple-value
user=> (def encrypted-complex-value (core/encrypt-value! complex-value key))
#'user/encrypted-complex-value
user=> encrypted-simple-value
[78, 80, 89, 4, -26, 109, -17, 76, -98, 98, 58, -83, 51, -9, 39, 96,
 -95, -59, -35, -20, 72, 80, 84, 83, 104, 102, 70, -88, 109, -16, 72,
 -25, -83, 36, 98, 11, 96, 53, 94, -13, 22, 4, 115, 46, -116, 75,
 118, 82, -6, 31, 118, 15, 1, -107, 17, -37, -18]
user=> encrypted-complex-value
[78, 80, 89, 4, -119, -59, 52, -100, -21, -47, -98, -80, -106, 21,
 64, -102, 58, -75, 81, -112, 84, -22, -82, -37, 45, -30, -30, -36,
 64, 5, -22, 75, 34, -105, -118, 56, 71, 96, 50, 43, -82, 84, -34,
 118, 103, 75, -12, -7, -80, -99, 24, 33, 54, 9, 124, -46, 92, -25,
 104, 31, -59, 80, 40, 60, 47, 34, -85, -114, -83, -3, -46, -96, 16,
 -99, 54, 70, -67, -57, -42, -21, 124, 77, -14, 8, -60, -53, -53, 53,
 37, -117, -56, -28, -102, -31]
user=> (def decrypted-value (core/decrypt-value encrypted-complex-value key))
#'user/decrypted-value
user=> decrypted-value
{:name "John Doe", :fav-dishes ["pizza" "hamburgers"]}
user=> (= complex-value decrypted-value)
true
user=> 
```

## License

Copyright (c) 2018, 2019 Magnet S Coop.

The source code for the library is subject to the terms of the Mozilla
Public License, v. 2.0. If a copy of the MPL was not distributed with
this file, You can obtain one at https://mozilla.org/MPL/2.0/.
