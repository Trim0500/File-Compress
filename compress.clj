(ns compress 
  (:require [clojure.java.io :as io]))

(def fileObject (io/file "."))

(def fileNames (.list fileObject))

(defn PrintDirectoryContents
  []
  (println "File List:")
  (doseq [item fileNames]
    (println "*./" item)))

(defn ReadFileContents
  [fileName]
  (println (slurp fileName)))