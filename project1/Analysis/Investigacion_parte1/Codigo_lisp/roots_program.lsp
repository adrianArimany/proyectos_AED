(DEFUN FINDROOTS (A B C)
  (COND ((>= (* B B) (* 4 A C))
         (LIST (/ (+ (- B) (SQRT (- (* B B) (* 4 A C)))) (* 2 A))
               (/ (- (- B) (SQRT (- (* B B) (* 4 A C)))) (* 2 A))))
        (T (LIST (/ (- B) (* 2 A))
                 (/ (SQRT (- (* B B) (* 4 A C))) (* 2 A))))))


