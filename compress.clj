(ns compress 
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

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

(def freqFileContent (slurp "frequency.txt"))

(def words (str/split freqFileContent #"\s"))

(defn CompressFileContent
  [fileName]
  (let [fileContent (slurp fileName)
        fileWords (str/split fileContent #"\s")
        compressedWords (map (fn [word] 
                                (let [firstMatchFrequency (first (for [i (range (count words))
                                                                       :when (== (compare (nth words i) word) 0)]
                                                                   i))]
                                  (if (not= nil firstMatchFrequency)
                                    (str firstMatchFrequency)))) fileWords)]
    (spit (str fileName ".ct") (str/join " " compressedWords))
    )
  (println "Outputted the compressed contents to the file!"))
