(ns closh.zero.frontend.plain
  (:gen-class)
  (:require [closh.zero.platform.eval :as eval]
            [closh.zero.compiler]
            [closh.zero.parser]
            [closh.zero.pipeline]
            #_[closh.zero.reader :as reader]
            #_[clojure.tools.reader.reader-types :refer [string-push-back-reader]])
  (:import (java.io PushbackReader StringReader)))

(defn read-all [rdr]
  (let [eof (Object.)
        opts {:eof eof :read-cond :allow :features #{:clj}}]
    (loop [forms []]
      (let [form (read opts rdr)]
        (if (= form eof)
          (seq forms)
          (recur (conj forms form)))))))

(defn -main [& args]
  (let [cmd (or (first args) "echo hello clojure")]
    (eval/eval
     `(-> ~(closh.zero.compiler/compile-interactive
            (closh.zero.parser/parse (read-all (PushbackReader. (StringReader. cmd)))))
          (closh.zero.pipeline/wait-for-pipeline)))))
