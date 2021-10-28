(ns reval.persist.edn
  (:require
   [taoensso.timbre :refer [debug info warnf]]
   [fipp.clojure]
   [reval.helper.date :refer [now-str]]))

; fast, but no pretty-print (makes it difficult to detect bugs)
#_(defn write [filename data]
    (spit filename  (pr-str data)))

(defn pprint [data opts]
  (with-out-str
    (fipp.clojure/pprint data opts)))

(defn save [file-name data]
  (info "saving edn file: " file-name)
  (let [comment (str "; saved on "
                     (now-str)
                     "\r\n")
        sedn (pprint data {:width 60})
        s (str comment sedn)]
    (spit file-name s)
    data  ; important to be here, as save-study is used often in a threading macro
    ))

(defn loadr [file-name]
  (info "loading edn file: " file-name)
  (-> (slurp file-name)
      (clojure.edn/read-string)))

(comment
  (save "/tmp/test.edn" {:a 1 :b [1 3 4]})
  (-> (loadr "/tmp/test.edn")
      :b)

;  
  )