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
  (if (.exists (io/file fileName))
    (println (slurp fileName))
    (println "Oops: specified file does not exist")))

(def freqFileContent (slurp "frequency.txt"))

(def words (str/split freqFileContent #"\s"))

(defn CompressFileContent
  [fileName]
  (if (.exists (io/file fileName))
    (do 
      (let [fileContent (slurp fileName)
            fileWords (str/split fileContent #"\s")
            compressedWords (map (fn [word]
                                   (let [firstMatchFrequency (first (for [i (range (count words))
                                                                          :when (== (compare (nth words i) word) 0)]
                                                                      i))]
                                     (if (not= nil firstMatchFrequency)
                                       (str firstMatchFrequency) 
                                       (if (= nil (re-matches #"[0-9]+" word)) 
                                         word 
                                         (str "@" word "@"))))) fileWords)]
        (spit (str fileName ".ct") (str/join " " compressedWords)))
      (println "Outputted the compressed contents to the file!"))
    (println "Oops: specified file does not exist")))

(defn DecompressFileContent
  [fileName]
  (if (.exists (io/file fileName))
    (let [compressedContent (slurp fileName)
          compressedWords (str/split compressedContent #"\s")
          originalWords (map (fn [compressedWord]
                               (let [returnedWord (if (not= nil (re-matches #"^@[0-9]+@$" compressedWord))
                                                     (str/replace compressedWord #"@" "")
                                                     (if (not= nil (re-matches #"^[0-9][0-9]*$" compressedWord))
                                                       (get words (int (Integer/parseInt compressedWord)))
                                                       compressedWord))]
                                 returnedWord))
                             compressedWords)]
      (println (str (str/join " " originalWords))))
    (println "Oops: specified file does not exist")))
